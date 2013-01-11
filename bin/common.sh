#!/bin/sh
CP=$(find 3rd_party -name "*.jar" | grep -v android | tr "\n" ":")
[ -z "$NO_NATIVE" ] && CC_JAVA_OPTS="-Djava.library.path=native/lib $CC_JAVA_OPTS"
JAVA="java -ea -cp ${CP}projects/server/bin $CC_JAVA_OPTS"
