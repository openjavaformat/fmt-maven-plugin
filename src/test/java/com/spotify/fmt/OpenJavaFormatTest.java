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

import static com.google.common.truth.Truth.assertThat;

import java.net.URL;
import java.net.URLClassLoader;
import org.junit.Test;

public class OpenJavaFormatTest {

  @Test
  public void foundWhereOpenJavaFormatIsADependency() {
    assertThat(OpenJavaFormat.isOnClasspath(getClass().getClassLoader())).isTrue();
  }

  @Test
  public void missingWithoutOpenJavaFormat() {
    // No parent: only the JDK's own classes, like a plugin realm without the dependency.
    ClassLoader withoutOpenJavaFormat = new URLClassLoader(new URL[0], null);

    assertThat(OpenJavaFormat.isOnClasspath(withoutOpenJavaFormat)).isFalse();
  }

  @Test
  public void loadsTheImplementationOpenJavaFormatRegisters() {
    assertThat(OpenJavaFormat.load().getClass().getName())
        .isEqualTo("com.palantir.javaformat.java.FormatterServiceImpl");
  }
}
