@echo off
title CineFlow - Movie Production Management System
if not exist "bin" mkdir bin
if not exist "data" mkdir data

dir /s /b src\*.java > sources.txt
javac -d bin @sources.txt
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed. Please ensure JDK 17+ is installed.
    pause
    exit /b %ERRORLEVEL%
)

java -cp bin com.cineflow.Main
pause
