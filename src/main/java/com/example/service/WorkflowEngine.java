
package com.example.service;

import com.example.model.*;
import com.example.steps.StepExecutor;
import com.example.steps.StepFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class WorkflowEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkflowEngine.class);
    
    @Autowired
    private StepFactory stepFactory;
    
    @Autowired
    private ExpressionEvaluator expressionEvaluator;
    
    public WorkflowContext executeWorkflow(WorkflowDefinition workflow, Map<String, Object> initialData) {
        String workflowId = UUID.randomUUID().toString();
        logger.info("Starting workflow execution with ID: {}", workflowId);
        
        WorkflowContext context = new WorkflowContext(workflowId);
        
        // Set initial variables
        for (String key : workflow.getInitialKeys()) {
            if (initialData.containsKey(key)) {
                context.setVariable(key, initialData.get(key));
            }
        }
        
        // Start with the first step
        if (!workflow.getSteps().isEmpty()) {
            String firstStepId = workflow.getSteps().get(0).getId();
            executeStep(workflow, context, firstStepId);
        }
        
        return context;
    }
    
    private void executeStep(WorkflowDefinition workflow, WorkflowContext context, String stepId) {
        if ("end".equals(stepId)) {
            logger.info("Workflow {} reached end step", context.getWorkflowId());
            context.setStatus(WorkflowContext.WorkflowStatus.COMPLETED);
            return;
        }
        
        WorkflowStep step = findStepById(workflow, stepId);
        if (step == null) {
            logger.error("Step not found: {}", stepId);
            context.setStatus(WorkflowContext.WorkflowStatus.FAILED);
            return;
        }
        
        logger.info("Executing step: {} of type: {}", stepId, step.getStepClass());
        context.setCurrentStepId(stepId);
        
        try {
            StepExecutor executor = stepFactory.createStepExecutor(step.getStepClass());
            Map<String, Object> result = executor.execute(step.getConfig(), context);
            
            // Update context with step results
            if (result != null) {
                for (Map.Entry<String, Object> entry : result.entrySet()) {
                    context.setVariable(entry.getKey(), entry.getValue());
                }
            }
            
            // Determine next step
            String nextStepId = determineNextStep(step, context);
            if (nextStepId != null) {
                executeStep(workflow, context, nextStepId);
            }
            
        } catch (Exception e) {
            logger.error("Error executing step {}: {}", stepId, e.getMessage(), e);
            context.setStatus(WorkflowContext.WorkflowStatus.FAILED);
            context.setVariable("error", e.getMessage());
        }
    }
    
    private WorkflowStep findStepById(WorkflowDefinition workflow, String stepId) {
        return workflow.getSteps().stream()
                .filter(step -> stepId.equals(step.getId()))
                .findFirst()
                .orElse(null);
    }
    
    private String determineNextStep(WorkflowStep step, WorkflowContext context) {
        if (step.getNext() == null || step.getNext().isEmpty()) {
            return "end";
        }
        
        for (NextCondition nextCondition : step.getNext()) {
            if (nextCondition.getCondition() == null || 
                expressionEvaluator.evaluate(nextCondition.getCondition(), context)) {
                return nextCondition.getTo();
            }
        }
        
        return "end";
    }
}
