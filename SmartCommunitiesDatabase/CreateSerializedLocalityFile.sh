CLASSPATH=classes
CLASSPATH=${CLASSPATH}:external/apache-log4j-1.2.16/log4j-1.2.16.jar
CLASSPATH=${CLASSPATH}:external/hibernate-distribution-3.6.0.Final/hibernate3.jar
CLASSPATH=${CLASSPATH}:external/hibernate-distribution-3.6.0.Final/lib/jpa/hibernate-jpa-2.0-api-1.0.0.Final.jar
CLASSPATH=${CLASSPATH}:external/hibernate-distribution-3.6.0.Final/lib/required/antlr-2.7.6.jar
CLASSPATH=${CLASSPATH}:external/hibernate-distribution-3.6.0.Final/lib/required/commons-collections-3.1.jar
CLASSPATH=${CLASSPATH}:external/hibernate-distribution-3.6.0.Final/lib/required/dom4j-1.6.1.jar
CLASSPATH=${CLASSPATH}:external/hibernate-distribution-3.6.0.Final/lib/required/javassist-3.12.0.GA.jar
CLASSPATH=${CLASSPATH}:external/hibernate-distribution-3.6.0.Final/lib/required/jta-1.1.jar
CLASSPATH=${CLASSPATH}:external/mysql-connector-java-5.0.8/mysql-connector-java-5.0.8-bin.jar
CLASSPATH=${CLASSPATH}:external/slf4j-1.6.4/slf4j-api-1.6.4.jar
CLASSPATH=${CLASSPATH}:external/slf4j-1.6.4/slf4j-log4j12-1.6.4.jar
java -cp ${CLASSPATH} edu.cmu.smartcommunities.database.controller.CreateSerializedLocalityFile
