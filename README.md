[![Build Status](https://github.com/openjavaformat/fmt-maven-plugin/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/openjavaformat/fmt-maven-plugin/actions/workflows/ci.yml?query=branch%3Amain)
[![license](http://img.shields.io/badge/license-MIT-brightgreen.svg)](https://github.com/openjavaformat/fmt-maven-plugin/blob/main/LICENSE)
[![Maven Central](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fdev%2Fopenjavaformat%2Ffmt-maven-plugin%2Fmaven-metadata.xml&label=Maven%20Central)](https://central.sonatype.com/artifact/dev.openjavaformat/fmt-maven-plugin)

## fmt-maven-plugin 

Formats your code using [open-java-format](https://openjavaformat.dev), a modern, lambda-friendly, 120-character Java formatter.

**Documentation: [openjavaformat.dev/get-started/maven](https://openjavaformat.dev/get-started/maven/)**

This is a fork of [spotify/fmt-maven-plugin](https://github.com/spotify/fmt-maven-plugin), which formats with google-java-format. It works the same way, with open-java-format in its place.

The format cannot be configured by design.

If you want your IDE to stick to the same format, open-java-format also has plugins for [IntelliJ IDEA](https://openjavaformat.dev/get-started/intellij-idea/) and [Eclipse](https://openjavaformat.dev/get-started/eclipse/).

## Usage

### Standard pom.xml

To have your sources automatically formatted on each build, add to your pom.xml:

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>dev.openjavaformat</groupId>
                <artifactId>fmt-maven-plugin</artifactId>
                <version>2.27.0.1</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>format</goal>
                        </goals>
                    </execution>
                </executions>
                <dependencies>
                    <dependency>
                        <groupId>dev.openjavaformat</groupId>
                        <artifactId>open-java-format</artifactId>
                        <version>2.98.0.4</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
```

The `open-java-format` dependency is required. The plugin does not bring a formatter of its own, so it formats with exactly the version you name here: align it with the version in your IDE, pre-commit Git hook, etc. Without it the build stops with a message that says which dependency to add.

If you prefer, you can only check formatting at build time using the `check` goal:

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>dev.openjavaformat</groupId>
                <artifactId>fmt-maven-plugin</artifactId>
                <version>2.27.0.1</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>check</goal>
                        </goals>
                    </execution>
                </executions>
                <dependencies>
                    <dependency>
                        <groupId>dev.openjavaformat</groupId>
                        <artifactId>open-java-format</artifactId>
                        <version>2.98.0.4</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
```

The examples below leave the `<dependencies>` element out to stay short, but every configuration needs it.

#### Overriding the Default Lifecycle Phase

Both goals have a [Maven lifecycle phase](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#lifecycle-reference) configured by default.

| Goal      | Default Phase     |
|-----------|-------------------|
| `format`  | `process-sources` |
| `check`   | `verify`          |

You may prefer to run these goals in a different phase instead.  
Maven allows you to override the default phase by specifying a `<phase>` for the `<execution>`.

For example, you may prefer that the `check` goal is performed in an earlier phase such as `validate`:

```xml
                    <execution>
                        <phase>validate</phase>
                        <goals>
                            <goal>check</goal>
                        </goals>
                    </execution>
```

### What gets formatted

Every file is formatted the way open-java-format's other integrations format it: the code is laid out, imports are sorted, unused imports are removed and long strings are reflowed. The `style`, `skipSortingImports`, `skipRemovingUnusedImports` and `skipReflowingLongStrings` options of spotify/fmt-maven-plugin do not exist here.

### Options

`sourceDirectory` represents the directory where your Java sources that need to be formatted are contained. It defaults to `${project.build.sourceDirectory}`

`testSourceDirectory` represents the directory where your test's Java sources that need to be formatted are contained. It defaults to `${project.build.testSourceDirectory}`

`additionalSourceDirectories` represents a list of additional directories that contains Java sources that need to be formatted. It defaults to an empty list.

`verbose` is whether the plugin should print a line for every file that is being formatted. It defaults to `false`.

`filesNamePattern` represents the pattern that filters files to format. The defaults value is set to `.*\.java`.

`skip` is whether the plugin should skip the operation.

`skipSourceDirectory` is whether the plugin should skip formatting/checking the `sourceDirectory`. It defaults to `false`.

`skipTestSourceDirectory` is whether the plugin should skip formatting/checking the `testSourceDirectory`. It defaults to `false`.

`forkMode` lets you specify whether to run open-java-format in a fork or in-process. Also adds JVM arguments to expose JDK internal javac APIs. Value `default` (which is the default) will fork (to be able to run at all on JDK 16+), `never` runs in-process, regardless of JDK version and `always` will always fork. In-process formatting needs the javac exports in `.mvn/jvm.config`:

```
--add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
```

example:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>dev.openjavaformat</groupId>
            <artifactId>fmt-maven-plugin</artifactId>
            <version>2.27.0.1</version>
            <configuration>
                <sourceDirectory>some/source/directory</sourceDirectory>
                <testSourceDirectory>some/test/directory</testSourceDirectory>
                <verbose>true</verbose>
                <filesNamePattern>.*\.java</filesNamePattern>
                <additionalSourceDirectories>
                    <param>some/dir</param>
                    <param>some/other/dir</param>
                </additionalSourceDirectories>
                <skip>false</skip>
                <skipSourceDirectory>false</skipSourceDirectory>
                <skipTestSourceDirectory>false</skipTestSourceDirectory>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>format</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```



### check Options

`displayFiles` default = true. Display the list of the files that are not compliant.

`displayLimit` default = 100. Number of files to display that are not compliant.

`failOnError` default = true. Fail the build if non-compliant files are found.


example to not display the non-compliant files:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>dev.openjavaformat</groupId>
            <artifactId>fmt-maven-plugin</artifactId>
            <version>2.27.0.1</version>
            <configuration>
                <displayFiles>false</displayFiles>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>check</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

example to limit the display up to 10 files
```xml
<build>
    <plugins>
        <plugin>
            <groupId>dev.openjavaformat</groupId>
            <artifactId>fmt-maven-plugin</artifactId>
            <version>2.27.0.1</version>
            <configuration>
                <displayLimit>10</displayLimit>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>check</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

example to only warn about non-compliant files instead of failing the build
```xml
<build>
    <plugins>
        <plugin>
            <groupId>dev.openjavaformat</groupId>
            <artifactId>fmt-maven-plugin</artifactId>
            <version>2.27.0.1</version>
            <configuration>
                <failOnError>false</failOnError>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>check</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Command line

You can also use it on the command line

`mvn dev.openjavaformat:fmt-maven-plugin:format`

The plugin still has to be declared in your pom.xml with its `open-java-format` dependency: Maven takes a plugin's dependencies from the pom.xml, the command line cannot name them.

You can pass parameters via standard `-D` syntax.
`mvn dev.openjavaformat:fmt-maven-plugin:format -Dverbose=true`

`-Dfmt.skip` is whether the plugin should skip the operation.

### Requirements

Maven has to run on JDK 21 or newer: open-java-format is compiled for Java 21, and the plugin formats in a JVM started from the same JDK.

### Building

`mvn verify` on JDK 21 runs the unit tests and the integration tests, the projects in `src/test/resources`.

### Releasing

A tag is a release: pushing a four-number tag, `X.Y.Z.N`, makes [release.yml](.github/workflows/release.yml) build that version, sign it and publish it to Maven Central. The repository secrets it needs come from 1Password with `mise run gh:secrets`.

## License

MIT, see [LICENSE](LICENSE). The plugin was written at Coveo and Spotify, and this fork keeps their copyright notices. Neither Spotify AB nor Coveo endorses, sponsors or is affiliated with this fork.
