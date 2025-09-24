@echo off
echo Building FAMTA Institute Application...
mvn clean compile
if %ERRORLEVEL% EQU 0 (
    echo Build successful! Starting application...
    mvn javafx:run
) else (
    echo Build failed!
    pause
)