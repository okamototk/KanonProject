start %MAVEN_HOME%/bin/mvn install:install-file -Dfile=contrib/lib/jasperreports/jasperreports.jar -DgroupId=jasperreports -DartifactId=jasperreports -Dversion=custom  -Dpackaging=jar 
start %MAVEN_HOME%/bin/mvn install:install-file -Dfile=contrib/lib/exchange/mpxj.jar -DgroupId=net.sf.mpxj -DartifactId=mpxj -Dversion=custom  -Dpackaging=jar 
start %MAVEN_HOME%/bin/mvn install:install-file -Dfile=contrib/lib/jdnc-0_7-all.jar -DgroupId=jdnc -DartifactId=jdnc -Dversion=0.7  -Dpackaging=jar
start %MAVEN_HOME%/bin/mvn install:install-file -Dfile=contrib/lib/nachocalendar-0_23.jar -DgroupId=net.sf.nachocalendar -DartifactId=nachocalendar -Dversion=0.23  -Dpackaging=jar 
start %MAVEN_HOME%/bin/mvn install:install-file -Dfile=contrib/lib/l2fprod-common-totd.jar  -DgroupId=com.l2fprod.common -DartifactId=l2fprod-common -Dversion=7.3 -Dpackaging=jar
