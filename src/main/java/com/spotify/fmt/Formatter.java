/*-
 * -\-\-
 * com.spotify.fmt:fmt-maven-plugin
 * --
 * Copyright (C) 2016 - 2023 Spotify AB
 * --
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * -/-/-
 */

package com.spotify.fmt;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.palantir.javaformat.java.FormatterService;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.maven.plugin.logging.Log;

class Formatter {

  private static final Log log = Logging.getLog();

  private final FormattingConfiguration cfg;

  private final CopyOnWriteArrayList<String> processedFiles = new CopyOnWriteArrayList<>();
  private final CopyOnWriteArrayList<String> nonComplyingFiles = new CopyOnWriteArrayList<>();

  Formatter(FormattingConfiguration cfg) {
    this.cfg = cfg;
  }

  FormattingResult format() throws FormatterException {
    FormatterService formatter = OpenJavaFormat.load();

    for (File directoryToFormat : cfg.directoriesToFormat()) {
      formatSourceFilesInDirectory(directoryToFormat, formatter);
    }

    logNumberOfFilesProcessed();

    return FormattingResult.builder()
        .nonComplyingFiles(nonComplyingFiles)
        .processedFiles(processedFiles)
        .build();
  }

  public void formatSourceFilesInDirectory(File directory, FormatterService formatter)
      throws FormatterException {
    if (!directory.isDirectory()) {
      log.info("Directory '" + directory + "' is not a directory. Skipping.");
      return;
    }

    try (Stream<Path> paths = Files.walk(Paths.get(directory.getPath()))) {
      FileFilter fileNameFilter = getFileNameFilter();
      FileFilter pathFilter = getPathFilter();
      long failures =
          paths.collect(Collectors.toList()).parallelStream()
              .filter(p -> p.toFile().exists())
              .map(Path::toFile)
              .filter(fileNameFilter::accept)
              .filter(pathFilter::accept)
              .map(file -> formatSourceFile(file, formatter))
              .filter(r -> !r)
              .count();

      if (failures > 0) {
        throw new FormatterException(
            "There were errors when formatting files. Error count: " + failures);
      }
    } catch (IOException exception) {
      throw new FormatterException(exception.getMessage());
    }
  }

  private FileFilter getFileNameFilter() {
    if (cfg.verbose()) {
      log.debug("Filter files on '" + cfg.filesNamePattern() + "'.");
    }
    return pathname -> pathname.isDirectory() || pathname.getName().matches(cfg.filesNamePattern());
  }

  private FileFilter getPathFilter() {
    if (cfg.verbose()) {
      log.debug("Filter paths on '" + cfg.filesPathPattern() + "'.");
    }
    return pathname -> pathname.isDirectory() || pathname.getPath().matches(cfg.filesPathPattern());
  }

  private boolean formatSourceFile(File file, FormatterService formatter) {
    if (file.isDirectory()) {
      if (cfg.verbose()) {
        log.debug("File '" + file + "' is a directory. Skipping.");
      }
      return true;
    }

    if (cfg.verbose()) {
      log.debug("Formatting '" + file + "'.");
    }

    try {
      // Read and written like the open-java-format command line does it.
      String input = new String(Files.readAllBytes(file.toPath()), UTF_8);
      String formatted = formatter.formatSourceReflowStringsAndFixImports(input);
      if (!input.equals(formatted)) {
        if (cfg.writeReformattedFiles()) {
          Files.write(file.toPath(), formatted.getBytes(UTF_8));
        }
        nonComplyingFiles.add(file.getAbsolutePath());
      }
      processedFiles.add(file.getAbsolutePath());
      if (processedFiles.size() % 100 == 0) {
        logNumberOfFilesProcessed();
      }
    } catch (com.palantir.javaformat.java.FormatterException | IOException e) {
      log.error("Failed to format file '" + file + "'.", e);
      return false;
    }
    return true;
  }

  protected void logNumberOfFilesProcessed() {
    log.info(
        String.format(
            "Processed %d files (%d %s).",
            processedFiles.size(), nonComplyingFiles.size(), cfg.processingLabel()));
  }
}
