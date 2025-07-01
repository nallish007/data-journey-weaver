
package com.example.steps;

import com.example.model.WorkflowContext;
import com.example.service.ExpressionEvaluator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class HttpStepExecutor implements StepExecutor {
    
    private static final Logger logger = LoggerFactory.getLogger(HttpStepExecutor.class);
    
    @Autowired
    private ExpressionEvaluator expressionEvaluator;
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    public HttpStepExecutor() {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> config, WorkflowContext context) {
        String url = expressionEvaluator.resolveVariables((String) config.get("url"), context);
        String method = (String) config.getOrDefault("method", "GET");
        Map<String, String> headers = (Map<String, String>) config.get("headers");
        Object body = config.get("body");
        
        int connectTimeout = (Integer) config.getOrDefault("connectTimeoutMs", 5000);
        int readTimeout = (Integer) config.getOrDefault("readTimeoutMs", 10000);
        
        logger.info("Executing HTTP request: {} {}", method, url);
        
        try {
            WebClient.RequestHeadersSpec<?> request = webClient
                    .method(HttpMethod.valueOf(method.toUpperCase()))
                    .uri(url);
            
            // Add headers
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    String headerValue = expressionEvaluator.resolveVariables(header.getValue(), context);
                    request = request.header(header.getKey(), headerValue);
                }
            }
            
            // Add body for POST/PUT requests
            WebClient.RequestHeadersSpec<?> finalRequest = request;
            if (body != null && ("POST".equals(method) || "PUT".equals(method))) {
                String bodyString = expressionEvaluator.resolveVariables(body.toString(), context);
                finalRequest = ((WebClient.RequestBodySpec) request).bodyValue(bodyString);
            }
            
            // Execute request
            String responseBody = finalRequest
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(readTimeout))
                    .block();
            
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("body", responseBody);
            
            // Parse JSON response if possible
            try {
                Object parsedBody = objectMapper.readValue(responseBody, Object.class);
                response.put("data", parsedBody);
                context.setVariable("userData", parsedBody);
            } catch (Exception e) {
                logger.debug("Response is not valid JSON, storing as string");
            }
            
            context.setLastResponse(response);
            logger.info("HTTP request completed successfully");
            
            return new HashMap<>();
            
        } catch (WebClientResponseException e) {
            logger.error("HTTP request failed with status {}: {}", e.getStatusCode(), e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", e.getStatusCode().value());
            response.put("error", e.getMessage());
            
            context.setLastResponse(response);
            return new HashMap<>();
            
        } catch (Exception e) {
            logger.error("HTTP request failed: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 500);
            response.put("error", e.getMessage());
            
            context.setLastResponse(response);
            return new HashMap<>();
        }
    }
}
