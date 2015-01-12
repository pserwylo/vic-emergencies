#!/bin/sh

DATE=`date +"%F_%H:%M:%S"`
FILE=feed_$DATE.json

wget -O $FILE http://emergency.vic.gov.au/feed/feed.json
