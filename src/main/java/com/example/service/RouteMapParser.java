
package com.example.service;

import com.example.model.RouteMapDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class RouteMapParser {
    
    private static final Logger logger = LoggerFactory.getLogger(RouteMapParser.class);
    private final ObjectMapper yamlMapper;
    
    public RouteMapParser() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }
    
    public RouteMapDefinition parseFromString(String yamlContent) throws IOException {
        logger.info("Parsing routemap definition from YAML string");
        try {
            RouteMapDefinition routeMap = yamlMapper.readValue(yamlContent, RouteMapDefinition.class);
            logger.info("Successfully parsed routemap with {} steps", routeMap.getSteps().size());
            return routeMap;
        } catch (IOException e) {
            logger.error("Failed to parse routemap YAML: {}", e.getMessage());
            throw e;
        }
    }
    
    public RouteMapDefinition parseFromResource(String resourcePath) throws IOException {
        logger.info("Parsing routemap definition from resource: {}", resourcePath);
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            RouteMapDefinition routeMap = yamlMapper.readValue(inputStream, RouteMapDefinition.class);
            logger.info("Successfully parsed routemap from resource with {} steps", routeMap.getSteps().size());
            return routeMap;
        } catch (IOException e) {
            logger.error("Failed to parse routemap from resource {}: {}", resourcePath, e.getMessage());
            throw e;
        }
    }
}
