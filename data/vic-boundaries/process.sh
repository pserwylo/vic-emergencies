#!/bin/bash

OUTPUT=vic.geojson
INPUT=STE06aAUST.shp
SIMPLIFY=0.01 #0.5 (33kb) is too simple, 0.1 (35kb) is getting better, 

rm $OUTPUT
ogr2ogr \
	-simplify $SIMPLIFY \
	-where "STATE_NAME = 'Victoria'" \
	-f "geoJSON" \
	$OUTPUT \
	$INPUT
