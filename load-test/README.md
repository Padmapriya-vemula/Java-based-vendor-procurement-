# Vendor Search API Load Testing

## API Endpoint
```
GET /api/vendors/search
```

## Available Filters
- `rating` (Double): Minimum rating filter (e.g., rating=4.0)
- `location` (String): Location contains filter (e.g., location=Mumbai)
- `category` (String): Exact category match (e.g., category=Electronics)
- `compliance` (Boolean): Compliance status (e.g., compliance=true)

## Pagination & Sorting
- `page` (int): Page number (default: 0)
- `size` (int): Page size (default: 20)
- `sort` (String): Sort by field,direction (e.g., sort=rating,desc)

## Example API Calls

### Basic Search with Pagination
```
GET /api/vendors/search?page=0&size=10&sort=name,asc
```

### Filter by Rating
```
GET /api/vendors/search?rating=4.0&page=0&size=10&sort=rating,desc
```

### Filter by Location and Category
```
GET /api/vendors/search?location=Mumbai&category=Electronics&page=0&size=20
```

### Complex Filter
```
GET /api/vendors/search?rating=3.5&location=Delhi&category=IT&compliance=true&page=0&size=15&sort=createdAt,desc
```

## Load Testing

### Prerequisites
1. Ensure your Spring Boot application is running on port 8080
2. Have Java installed on your system

### Running the Load Test
1. Navigate to the load-test directory
2. Run the batch file:
   ```
   run-load-test.bat
   ```

### Load Test Configuration
- **Concurrent Users**: 50
- **Requests per User**: 20
- **Total Requests**: 1000
- **Timeout**: 30 seconds

### Test Scenarios
The load test covers various scenarios:
1. Rating filter with sorting
2. Location search with pagination
3. Category and compliance filters
4. Combined filters
5. Large page sizes
6. Different sorting options

### Expected Results
- Success Rate: >95%
- Average Response Time: <500ms
- Requests per Second: >100

## Performance Optimization Tips
1. Add database indexes on filtered columns (rating, location, category, compliance)
2. Use connection pooling
3. Enable query caching
4. Consider pagination limits
5. Monitor database performance during load tests