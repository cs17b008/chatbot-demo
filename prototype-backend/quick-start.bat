@echo off
echo ===================================
echo N8N Webhook Integration Quick Start
echo ===================================
echo.

echo Step 1: Checking if n8n is installed...
where n8n >nul 2>nul
if %errorlevel% neq 0 (
    echo n8n is not installed. Please install it with: npm install n8n -g
    pause
    exit /b 1
)
echo ✓ n8n is installed

echo.
echo Step 2: Starting n8n in the background...
start "n8n" cmd /c "n8n start"
timeout /t 5 /nobreak >nul
echo ✓ n8n is starting on http://localhost:5678

echo.
echo Step 3: Building Spring Boot application...
call mvn clean compile
if %errorlevel% neq 0 (
    echo ✗ Build failed
    pause
    exit /b 1
)
echo ✓ Build successful

echo.
echo Step 4: Starting Spring Boot application...
echo Application will start on http://localhost:8080
echo.
echo Please configure your n8n webhook URL in application.properties before testing
echo.
call mvn spring-boot:run
