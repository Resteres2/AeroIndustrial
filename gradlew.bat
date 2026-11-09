@echo off
setlocal
set ROOT=%~dp0
set GRADLE_VERSION=8.12.1
set DIST=%ROOT%.gradle-dist\gradle-%GRADLE_VERSION%
where gradle >nul 2>nul
if %ERRORLEVEL% EQU 0 (
  gradle %*
  exit /b %ERRORLEVEL%
)
if not exist "%DIST%\bin\gradle.bat" (
  if not exist "%ROOT%.gradle-dist" mkdir "%ROOT%.gradle-dist"
  set ZIP=%ROOT%.gradle-dist\gradle-%GRADLE_VERSION%-bin.zip
  if not exist "%ZIP%" powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%ZIP%'"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Path '%ZIP%' -DestinationPath '%ROOT%.gradle-dist' -Force"
)
call "%DIST%\bin\gradle.bat" %*
exit /b %ERRORLEVEL%
