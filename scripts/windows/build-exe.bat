@echo off
setlocal enabledelayedexpansion

title HConex - Build EXE
cd /d %~dp0\..\..

echo [INFO] Building JAR with Maven...
call mvn clean package -Dmaven.test.skip=true
if errorlevel 1 (
  echo [ERROR] Maven build failed.
  pause
  exit /b 1
)

echo [INFO] Locating generated JAR...
set JAR_FILE=
for %%f in (target\*.jar) do (
  echo %%~nxf | findstr /I /V "original-" >nul
  if !errorlevel! equ 0 set JAR_FILE=%%~nxf
)

if "%JAR_FILE%"=="" (
  echo [ERROR] No runnable JAR found in target\.
  pause
  exit /b 1
)

echo [INFO] Using JAR: %JAR_FILE%

echo [INFO] Preparing app-input folder...
if exist target\app-input rmdir /s /q target\app-input
mkdir target\app-input
copy /y target\%JAR_FILE% target\app-input\hconex.jar >nul
if exist target\dependency\*.jar (
  copy /y target\dependency\*.jar target\app-input\ >nul
)

where jpackage >nul 2>&1
if errorlevel 1 (
  echo [ERROR] jpackage not found. Install JDK 17+ and ensure jpackage is in PATH.
  pause
  exit /b 1
)

echo [INFO] Packaging Windows EXE with jpackage...
if not exist dist mkdir dist

jpackage ^
  --type exe ^
  --name HConex ^
  --input target\app-input ^
  --main-jar hconex.jar ^
  --main-class com.hconex.Application ^
  --dest dist ^
  --win-shortcut ^
  --win-menu ^
  --win-dir-chooser

if errorlevel 1 (
  echo [WARN] EXE packaging failed (possibly missing WiX). Creating portable app-image...
  jpackage ^
    --type app-image ^
    --name HConex ^
    --input target\app-input ^
    --main-jar hconex.jar ^
    --main-class com.hconex.Application ^
    --dest dist
)

if errorlevel 1 (
  echo [ERROR] Packaging failed.
  pause
  exit /b 1
)

echo [SUCCESS] Package generated in dist\
dir dist
pause
exit /b 0
