@echo off
cd /d "%%~dp0"
if not exist out mkdir out
echo Compiling project...
dir /s /b src\main\java\*.java > .sources
javac -encoding UTF-8 -d out @.sources
if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)
echo Starting Smart Parking Management System...
java -cp out parking.ui.ParkingApplication
pause
