#!/bin/sh

cd chess_server
echo Installing server
mvn package

cd ../ClientApp/app
echo Installing Client App
mvn package

cd ../..

cp ./tools/localhost_config.json ./ClientApp/app/target/config.json

echo Installation complete
