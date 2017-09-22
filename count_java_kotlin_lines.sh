#!/bin/bash
git rev-list HEAD | while read line; do
git checkout -q $line
echo -n `git log -1 --pretty=format:%cI`
echo -n ,
KF=`find -f app/src obd/src logger/src -iname '*.kt'`
echo -n `wc -l $KF | grep ' total' | awk '{ print $1 }'`
echo -n ,
JF=`find -f app/src obd/src logger/src -iname '*.java'`
echo `wc -l $JF | grep ' total' | awk '{ print $1 }'`
done
git checkout staging
