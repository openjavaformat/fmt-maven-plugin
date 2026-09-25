// expected/Demo.java is what the open-java-format command line of the same version makes of the
// original src/main/java/Demo.java: the plugin has to produce exactly that, byte for byte.
String formatted = new File("${basedir}/src/main/java/Demo.java").getText("UTF-8")
String expected = new File("${basedir}/expected/Demo.java").getText("UTF-8")
assert formatted == expected

String buildLog = new File("${basedir}/build.log").getText("UTF-8")
assert buildLog.contains("Processed 1 files (1 reformatted).")
