#!/bin/sh

cd ClientApp/app
echo Installing Client App
mvn package

cd ../..

cp ./tools/official_server_config.json ./config.json

echo Installation complete