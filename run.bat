SET JAVA_HOME=c:\program files\java\jdk-9.0.1
SET MAVEN=C:\Tools\maven\apache-maven-3.5.2\bin\mvn
SET MAIN_CLASS=ch.rweiss.terminal.chart.Test

%MAVEN% exec:java -Dexec.mainClass=%MAIN_CLASS% -Dexec.arguments="%*" -Dexec.classpathScope=test
