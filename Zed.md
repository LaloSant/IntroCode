java @.zed/run.argfile -m com.introcode/com.introcode.App

java -agentlib:jdwp=transport=dt_socket,server=n,suspend=y,address=localhost:39787 @.zed/run.argfile -m com.introcode/com.introcode.App
