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

import com.palantir.javaformat.java.FormatterService;
import java.util.ServiceLoader;

/**
 * Finds open-java-format on the plugin's classpath. The plugin does not bring it: a build adds it to
 * the plugin's dependencies, with the version it formats with.
 */
final class OpenJavaFormat {

  static final String MISSING =
      "open-java-format is missing from the plugin's dependencies. The plugin does not bring a"
          + " formatter of its own: add dev.openjavaformat:open-java-format, with the version to"
          + " format with, to the <dependencies> of the fmt-maven-plugin <plugin> element. See"
          + " https://github.com/openjavaformat/fmt-maven-plugin#usage";

  /** Where open-java-format registers its {@link FormatterService} for the {@link ServiceLoader}. */
  private static final String SERVICE_REGISTRATION =
      "META-INF/services/com.palantir.javaformat.java.FormatterService";

  private OpenJavaFormat() {}

  /**
   * Whether {@code loader} sees an open-java-format to load. It looks for the service registration
   * rather than a class, so that a missing formatter answers {@code false} instead of throwing a
   * {@link NoClassDefFoundError}.
   */
  static boolean isOnClasspath(ClassLoader loader) {
    return loader.getResource(SERVICE_REGISTRATION) != null;
  }

  /** The formatter behind the facade that the open-java-format Gradle plugin loads too. */
  static FormatterService load() {
    return ServiceLoader.load(FormatterService.class, FormatterService.class.getClassLoader())
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(MISSING));
  }
}
