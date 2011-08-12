#!/usr/bin/env bash

bin=`dirname "$0"`
DATAGEN_HOME=`cd "$bin"; cd ..; pwd`

CLASSPATH=$DATAGEN_HOME:$DATAGEN_HOME/conf

for f in $DATAGEN_HOME/gruter-datagen*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done

for f in $DATAGEN_HOME/lib/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done

exec java -Duser.dir=$DATAGEN_HOME -classpath "$CLASSPATH" com.gruter.generator.DataGenerator "$@"