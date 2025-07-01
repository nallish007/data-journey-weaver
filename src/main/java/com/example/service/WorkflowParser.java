
package com.example.service;

import com.example.model.WorkflowDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class WorkflowParser {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkflowParser.class);
    private final ObjectMapper yamlMapper;
    
    public WorkflowParser() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }
    
    public WorkflowDefinition parseFromString(String yamlContent) throws IOException {
        logger.info("Parsing workflow definition from YAML string");
        try {
            WorkflowDefinition workflow = yamlMapper.readValue(yamlContent, WorkflowDefinition.class);
            logger.info("Successfully parsed workflow with {} steps", workflow.getSteps().size());
            return workflow;
        } catch (IOException e) {
            logger.error("Failed to parse workflow YAML: {}", e.getMessage());
            throw e;
        }
    }
    
    public WorkflowDefinition parseFromResource(String resourcePath) throws IOException {
        logger.info("Parsing workflow definition from resource: {}", resourcePath);
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            WorkflowDefinition workflow = yamlMapper.readValue(inputStream, WorkflowDefinition.class);
            logger.info("Successfully parsed workflow from resource with {} steps", workflow.getSteps().size());
            return workflow;
        } catch (IOException e) {
            logger.error("Failed to parse workflow from resource {}: {}", resourcePath, e.getMessage());
            throw e;
        }
    }
}
