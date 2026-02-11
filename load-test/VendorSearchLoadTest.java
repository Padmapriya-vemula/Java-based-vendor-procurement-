package loadtest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class VendorSearchLoadTest {
    
    private static final String BASE_URL = "http://localhost:8080/api/vendors/search";
    private static final int CONCURRENT_USERS = 50;
    private static final int REQUESTS_PER_USER = 20;
    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    
    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger errorCount = new AtomicInteger(0);
    private static final AtomicInteger totalRequests = new AtomicInteger(0);
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Vendor Search API Load Test...");
        System.out.println("Concurrent Users: " + CONCURRENT_USERS);
        System.out.println("Requests per User: " + REQUESTS_PER_USER);
        System.out.println("Total Requests: " + (CONCURRENT_USERS * REQUESTS_PER_USER));
        
        long startTime = System.currentTimeMillis();
        
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
        
        // Submit load test tasks
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            executor.submit(() -> runUserScenario(client));
        }
        
        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.sleep(1000);
            System.out.println("Progress: " + totalRequests.get() + "/" + (CONCURRENT_USERS * REQUESTS_PER_USER));
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        printResults(duration);
    }
    
    private static void runUserScenario(HttpClient client) {
        String[] testScenarios = {
            "?rating=4.0&page=0&size=10&sort=rating,desc",
            "?location=Mumbai&page=0&size=5&sort=name,asc",
            "?category=Electronics&compliance=true&page=0&size=20",
            "?rating=3.5&location=Delhi&page=1&size=15&sort=createdAt,desc",
            "?compliance=false&page=0&size=10",
            "?category=Software&rating=4.5&page=0&size=25&sort=rating,desc",
            "?location=Bangalore&category=IT&page=0&size=10",
            "?page=0&size=50&sort=name,asc"
        };
        
        for (int i = 0; i < REQUESTS_PER_USER; i++) {
            String scenario = testScenarios[i % testScenarios.length];
            makeRequest(client, BASE_URL + scenario);
        }
    }
    
    private static void makeRequest(HttpClient client, String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(TIMEOUT)
                    .GET()
                    .build();
            
            HttpResponse<String> response = client.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            totalRequests.incrementAndGet();
            
            if (response.statusCode() == 200) {
                successCount.incrementAndGet();
            } else {
                errorCount.incrementAndGet();
                System.err.println("Error response: " + response.statusCode() + " for " + url);
            }
            
        } catch (IOException | InterruptedException e) {
            errorCount.incrementAndGet();
            totalRequests.incrementAndGet();
            System.err.println("Request failed: " + e.getMessage());
        }
    }
    
    private static void printResults(long duration) {
        System.out.println("\n=== LOAD TEST RESULTS ===");
        System.out.println("Total Duration: " + duration + " ms");
        System.out.println("Total Requests: " + totalRequests.get());
        System.out.println("Successful Requests: " + successCount.get());
        System.out.println("Failed Requests: " + errorCount.get());
        System.out.println("Success Rate: " + 
                String.format("%.2f%%", (successCount.get() * 100.0) / totalRequests.get()));
        System.out.println("Requests per Second: " + 
                String.format("%.2f", totalRequests.get() * 1000.0 / duration));
        System.out.println("Average Response Time: " + 
                String.format("%.2f ms", duration / (double) totalRequests.get()));
    }
}