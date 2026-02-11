@echo off
echo ========================================
echo Analytics Dashboard Setup
echo ========================================
echo.

echo Installing frontend dependencies...
cd procurement-ui
call npm install
echo.

echo ========================================
echo Setup Complete!
echo ========================================
echo.
echo To start the application:
echo 1. Backend: mvn spring-boot:run
echo 2. Frontend: cd procurement-ui && npm start
echo 3. Access Analytics Dashboard at: http://localhost:3000/analytics
echo.
pause
