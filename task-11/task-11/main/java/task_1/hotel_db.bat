@echo off

set DB_NAME=hotel_db
set DB_USER=postgres

dropdb -U %DB_USER% %DB_NAME%
createdb -U %DB_USER% %DB_NAME%

echo Creating tables
psql -U %DB_USER% -d %DB_NAME% -f db\DDL.sql

echo Inserting test data
psql -U %DB_USER% -d %DB_NAME% -f db\DML.sql