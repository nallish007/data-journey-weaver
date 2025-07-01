
package com.example.controller;

import com.example.model.RouteMapContext;
import com.example.model.RouteMapDefinition;
import com.example.service.RouteMapEngine;
import com.example.service.RouteMapParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/routemap")
public class RouteMapController {
    
    private static final Logger logger = LoggerFactory.getLogger(RouteMapController.class);
    
    @Autowired
    private RouteMapParser routeMapParser;
    
    @Autowired
    private RouteMapEngine routeMapEngine;
    
    @PostMapping("/execute")
    public ResponseEntity<RouteMapContext> executeRouteMap(
            @RequestParam String routeMapYaml,
            @RequestBody Map<String, Object> requestData) {
        
        try {
            logger.info("Received routemap execution request");
            
            // Parse routemap definition
            RouteMapDefinition routeMap = routeMapParser.parseFromString(routeMapYaml);
            
            // Execute routemap
            RouteMapContext context = routeMapEngine.executeRouteMap(routeMap, requestData);
            
            return ResponseEntity.ok(context);
            
        } catch (Exception e) {
            logger.error("Error executing routemap: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/execute-from-resource")
    public ResponseEntity<RouteMapContext> executeRouteMapFromResource(
            @RequestParam String resourcePath,
            @RequestBody Map<String, Object> requestData) {
        
        try {
            logger.info("Received routemap execution request for resource: {}", resourcePath);
            
            // Parse routemap definition from resource
            RouteMapDefinition routeMap = routeMapParser.parseFromResource(resourcePath);
            
            // Execute routemap
            RouteMapContext context = routeMapEngine.executeRouteMap(routeMap, requestData);
            
            return ResponseEntity.ok(context);
            
        } catch (Exception e) {
            logger.error("Error executing routemap from resource: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}
