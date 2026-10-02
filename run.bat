@echo off
chcp 65001 > nul
title Horizon Studios - Movie Production Management System
if not exist "bin" mkdir bin
if not exist "data" mkdir data

dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed. Please ensure JDK 17+ is installed.
    pause
    exit /b %ERRORLEVEL%
)

java -Dfile.encoding=UTF-8 -cp bin com.cineflow.Main
pause
