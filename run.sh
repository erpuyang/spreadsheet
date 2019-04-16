#!/usr/bin/env bash

mvn clean package && cp target/spreasheet-jar-with-dependencies.jar spreasheet.jar && java -jar spreasheet.jar -i inputfile.csv -o outputfile.csv