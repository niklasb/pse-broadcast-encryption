@echo off
cd "%~dp0/.."
mvn clean install
cd modules/server
mvn package appassembler:assemble
