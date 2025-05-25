@echo off
set DB_NAME=handicapp
set DB_USER=%USERNAME%

REM Check if psql is in the PATH
where psql >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    echo Error: psql not installed or not in the PATH.
    pause
    exit /b 1
)

REM Check if Maven is in the PATH
where mvn >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    echo Error: mvn not installed or not in the PATH.
    pause
    exit /b 1
)

REM Check if database already exists
psql -U %DB_USER% -lqt | findstr /C:" %DB_NAME% " >nul
IF %ERRORLEVEL% NEQ 0 (
    createdb -U %DB_USER% %DB_NAME%
    echo Database %DB_NAME% created.
) ELSE (
    echo Database %DB_NAME% already exists.
)

REM Import the SQL dump
psql -U %DB_USER% -d %DB_NAME% -f database\handicapp.sql
IF %ERRORLEVEL% NEQ 0 (
    echo Error importing SQL dump.
    pause
    exit /b 1
)

REM Compile and run using Maven
mvn clean compile exec:java
