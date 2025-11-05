@echo off
echo Starting N8N Integration Spring Boot Application...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven 3.6 or higher
    pause
    exit /b 1
)

echo Java and Maven are available
echo.

REM Compile the project first
echo Compiling the project...
mvn clean compile
if %errorlevel% neq 0 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)

echo.
echo Starting the application...
echo The application will be available at: http://localhost:8080
echo API endpoints will be available at: http://localhost:8080/api/n8n/
echo.
echo Press Ctrl+C to stop the application
echo.

REM Run the Spring Boot application
mvn spring-boot:run
