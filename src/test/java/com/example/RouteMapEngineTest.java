
package com.example;

import com.example.model.RouteMapContext;
import com.example.model.RouteMapDefinition;
import com.example.service.RouteMapEngine;
import com.example.service.RouteMapParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RouteMapEngineTest {
    
    @Autowired
    private RouteMapParser routeMapParser;
    
    @Autowired
    private RouteMapEngine routeMapEngine;
    
    @Test
    public void testRouteMapParsing() throws Exception {
        RouteMapDefinition routeMap = routeMapParser.parseFromResource("routemaps/sample-routemap.yaml");
        
        assertNotNull(routeMap);
        assertEquals(5, routeMap.getInitialKeys().size());
        assertEquals(6, routeMap.getSteps().size());
        
        // Verify first step
        assertEquals("fetchUser", routeMap.getSteps().get(0).getId());
        assertEquals("com.example.steps.HttpStep", routeMap.getSteps().get(0).getStepClass());
    }
    
    @Test
    public void testScriptStepExecution() throws Exception {
        RouteMapDefinition routeMap = routeMapParser.parseFromResource("routemaps/sample-routemap.yaml");
        
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("apiUrl", "https://jsonplaceholder.typicode.com/users/1");
        requestData.put("httpMethod", "GET");
        requestData.put("token", "dummy-token");
        requestData.put("enrichmentUrl", "https://httpbin.org/post");
        
        RouteMapContext context = routeMapEngine.executeRouteMap(routeMap, requestData);
        
        assertNotNull(context);
        assertEquals(RouteMapContext.RouteMapStatus.COMPLETED, context.getStatus());
    }
}
