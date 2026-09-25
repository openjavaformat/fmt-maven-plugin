String buildLog = new File("${basedir}/build.log").getText("UTF-8")
assert buildLog.contains("open-java-format is missing from the plugin's dependencies.")
assert !buildLog.contains("NoClassDefFoundError")
