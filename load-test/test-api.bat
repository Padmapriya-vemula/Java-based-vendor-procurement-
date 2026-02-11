@echo off
echo Testing Vendor Search API...
echo.

set BASE_URL=http://localhost:8080/api/vendors/search

echo 1. Basic search with pagination
curl -s "%BASE_URL%?page=0&size=5" | echo Response received
echo.

echo 2. Filter by rating
curl -s "%BASE_URL%?rating=4.0&sort=rating,desc" | echo Response received
echo.

echo 3. Filter by location
curl -s "%BASE_URL%?location=Mumbai&page=0&size=10" | echo Response received
echo.

echo 4. Filter by category and compliance
curl -s "%BASE_URL%?category=Electronics&compliance=true" | echo Response received
echo.

echo 5. Complex filter with sorting
curl -s "%BASE_URL%?rating=3.5&location=Delhi&category=IT&page=0&size=15&sort=name,asc" | echo Response received
echo.

echo 6. Large page size test
curl -s "%BASE_URL%?page=0&size=50&sort=createdAt,desc" | echo Response received
echo.

echo API tests completed!
pause