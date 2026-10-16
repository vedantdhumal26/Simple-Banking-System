@echo off
setlocal
echo ================================================================
echo  Starting Simple Banking System Web Application
echo ================================================================

REM Detect or configure Java and Maven
if defined JAVA_HOME (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
) else if exist "C:\Users\MACHINDRA DHUMAL\jdk-17\bin\java.exe" (
    set "JAVA_HOME=C:\Users\MACHINDRA DHUMAL\jdk-17"
    set "PATH=C:\Users\MACHINDRA DHUMAL\jdk-17\bin;%PATH%"
)

if exist "C:\Users\MACHINDRA DHUMAL\maven\bin\mvn.cmd" (
    set "M2_HOME=C:\Users\MACHINDRA DHUMAL\maven"
    set "PATH=C:\Users\MACHINDRA DHUMAL\maven\bin;%PATH%"
)

echo Checking Java installation...
java -version
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java was not found in PATH or JAVA_HOME.
    echo Please make sure JDK 17 is installed.
    pause
    exit /b 1
)

echo.
echo Starting Application Server...
echo The web dashboard will be available at: http://localhost:8080
echo.

if exist "target\simple-banking-system-1.0.0.jar" (
    java "-Dfile.encoding=UTF-8" -jar "target\simple-banking-system-1.0.0.jar"
) else (
    echo Building and running with Maven...
    mvn spring-boot:run
)

pause
