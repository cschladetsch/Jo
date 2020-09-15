#!/bin/sh

target=$WORK2_ROOT/bin
jar=`find out -name \*.jar`
jo=$target/jo

echo Adding 'jo' to $target via $jar
cp $jar $target/jo.jar

echo java -jar $WORK2_ROOT/bin/jo.jar $* > $jo
chmod a+x $jo



