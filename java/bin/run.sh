#! /bin/bash

# get absolute path of this file
BIN_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
RUN_DIR="${BIN_DIR}/../"

echo "compiling..."
cd $BIN_DIR/../ && $BIN_DIR/../gradlew clean build && cd $RUN_DIR

echo "done compiling. starting..."
exec java -jar ${BIN_DIR}/../build/libs/importer.jar $@