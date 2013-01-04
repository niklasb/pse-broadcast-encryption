#!/bin/sh
CP=$(find 3rd_party -name "*.jar" | grep -v android | tr "\n" ":")
JAVA="java -ea -cp ${CP}projects/server/bin $CC_JAVA_OPTS"
