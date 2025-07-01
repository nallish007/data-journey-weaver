
package com.example;

import com.example.model.WorkflowContext;
import com.example.model.WorkflowDefinition;
import com.example.service.WorkflowEngine;
import com.example.service.WorkflowParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WorkflowEngineTest {
    
    @Autowired
    private WorkflowParser workflowParser;
    
    @Autowired
    private WorkflowEngine workflowEngine;
    
    @Test
    public void testWorkflowParsing() throws Exception {
        WorkflowDefinition workflow = workflowParser.parseFromResource("workflows/sample-workflow.yaml");
        
        assertNotNull(workflow);
        assertEquals(5, workflow.getInitialKeys().size());
        assertEquals(6, workflow.getSteps().size());
        
        // Verify first step
        assertEquals("fetchUser", workflow.getSteps().get(0).getId());
        assertEquals("com.example.steps.HttpStep", workflow.getSteps().get(0).getStepClass());
    }
    
    @Test
    public void testScriptStepExecution() throws Exception {
        WorkflowDefinition workflow = workflowParser.parseFromResource("workflows/sample-workflow.yaml");
        
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("apiUrl", "https://jsonplaceholder.typicode.com/users/1");
        requestData.put("httpMethod", "GET");
        requestData.put("token", "dummy-token");
        requestData.put("enrichmentUrl", "https://httpbin.org/post");
        
        WorkflowContext context = workflowEngine.executeWorkflow(workflow, requestData);
        
        assertNotNull(context);
        assertEquals(WorkflowContext.WorkflowStatus.COMPLETED, context.getStatus());
    }
}
