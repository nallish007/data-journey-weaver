
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
public class RouteMapEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(RouteMapEngine.class);
    
    @Autowired
    private StepFactory stepFactory;
    
    @Autowired
    private ExpressionEvaluator expressionEvaluator;
    
    public RouteMapContext executeRouteMap(RouteMapDefinition routeMap, Map<String, Object> initialData) {
        String routeMapId = UUID.randomUUID().toString();
        logger.info("Starting routemap execution with ID: {}", routeMapId);
        
        RouteMapContext context = new RouteMapContext(routeMapId);
        
        // Set initial variables
        for (String key : routeMap.getInitialKeys()) {
            if (initialData.containsKey(key)) {
                context.setVariable(key, initialData.get(key));
            }
        }
        
        // Start with the first step
        if (!routeMap.getSteps().isEmpty()) {
            String firstStepId = routeMap.getSteps().get(0).getId();
            executeStep(routeMap, context, firstStepId);
        }
        
        return context;
    }
    
    private void executeStep(RouteMapDefinition routeMap, RouteMapContext context, String stepId) {
        if ("end".equals(stepId)) {
            logger.info("RouteMap {} reached end step", context.getRouteMapId());
            context.setStatus(RouteMapContext.RouteMapStatus.COMPLETED);
            return;
        }
        
        RouteMapStep step = findStepById(routeMap, stepId);
        if (step == null) {
            logger.error("Step not found: {}", stepId);
            context.setStatus(RouteMapContext.RouteMapStatus.FAILED);
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
                executeStep(routeMap, context, nextStepId);
            }
            
        } catch (Exception e) {
            logger.error("Error executing step {}: {}", stepId, e.getMessage(), e);
            context.setStatus(RouteMapContext.RouteMapStatus.FAILED);
            context.setVariable("error", e.getMessage());
        }
    }
    
    private RouteMapStep findStepById(RouteMapDefinition routeMap, String stepId) {
        for (RouteMapStep step : routeMap.getSteps()) {
            if (stepId.equals(step.getId())) {
                return step;
            }
        }
        return null;
    }
    
    private String determineNextStep(RouteMapStep step, RouteMapContext context) {
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
