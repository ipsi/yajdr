**** Building ****
Log4J declares some dependencies that aren't actually in the default Maven repository. Why? Damned if I know. Apparently
something to do with Licensing agreements or somesuch.

In order to resolve them, you can either download the *-dist.(zip|tar.gz) file and run the following commands to install them:

mvn install:install-file -Dfile=jmxri.jar -DgroupId=com.sun.jmx -DartifactId=jmxri -Dversion=1.2.1 -Dpackaging=jar
mvn install:install-file -Dfile=jmxtools.jar -DgroupId=com.sun.jdmk -DartifactId=jmxtools -Dversion=1.2.1 -Dpackaging=jar
mvn install:install-file -Dfile=jms.jar -DgroupId=javax.jms -DartifactId=jms -Dversion=1.1 -Dpackaging=jar

Make sure that you're in the same directory as the files you're installing!

OR

Go to the following web page for slightly more help on finding them: http://www.slingingsemicolons.com/blogs/dave/maven-and-log4j

Note that while it says you need to install the JTA libraries, I've found that Log4J doesn't depend on them. Or it doesn't appear
to, anyway.

mvn install:install-file -Dfile=jmxri.jar -DgroupId=com.sun.jmx -DartifactId=jmxri -Dversion=1.2.1 -Dpackaging=jar