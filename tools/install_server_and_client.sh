#!/bin/sh

sudo apt install mysql-server
sudo mysql < ./tools/db_script.sql

cd chess_server
echo Installing server
mvn package

cd ../ClientApp/app
echo Installing Client App
mvn package

cd ../..

cp ./tools/localhost_config.json ./config.json

echo Installation complete
