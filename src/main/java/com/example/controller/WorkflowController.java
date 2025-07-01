
package com.example.controller;

import com.example.model.WorkflowContext;
import com.example.model.WorkflowDefinition;
import com.example.service.WorkflowEngine;
import com.example.service.WorkflowParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkflowController.class);
    
    @Autowired
    private WorkflowParser workflowParser;
    
    @Autowired
    private WorkflowEngine workflowEngine;
    
    @PostMapping("/execute")
    public ResponseEntity<WorkflowContext> executeWorkflow(
            @RequestParam String workflowYaml,
            @RequestBody Map<String, Object> requestData) {
        
        try {
            logger.info("Received workflow execution request");
            
            // Parse workflow definition
            WorkflowDefinition workflow = workflowParser.parseFromString(workflowYaml);
            
            // Execute workflow
            WorkflowContext context = workflowEngine.executeWorkflow(workflow, requestData);
            
            return ResponseEntity.ok(context);
            
        } catch (Exception e) {
            logger.error("Error executing workflow: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/execute-from-resource")
    public ResponseEntity<WorkflowContext> executeWorkflowFromResource(
            @RequestParam String resourcePath,
            @RequestBody Map<String, Object> requestData) {
        
        try {
            logger.info("Received workflow execution request for resource: {}", resourcePath);
            
            // Parse workflow definition from resource
            WorkflowDefinition workflow = workflowParser.parseFromResource(resourcePath);
            
            // Execute workflow
            WorkflowContext context = workflowEngine.executeWorkflow(workflow, requestData);
            
            return ResponseEntity.ok(context);
            
        } catch (Exception e) {
            logger.error("Error executing workflow from resource: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}
