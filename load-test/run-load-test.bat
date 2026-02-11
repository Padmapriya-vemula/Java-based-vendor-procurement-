@echo off
echo Compiling Load Test...
javac -d . VendorSearchLoadTest.java

echo.
echo Running Load Test...
java loadtest.VendorSearchLoadTest

pause