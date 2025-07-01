
package com.example.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class WorkflowContext {
    
    private String workflowId;
    private String currentStepId;
    private Map<String, Object> variables;
    private Map<String, Object> lastResponse;
    private LocalDateTime startTime;
    private LocalDateTime lastUpdateTime;
    private WorkflowStatus status;
    
    public WorkflowContext(String workflowId) {
        this.workflowId = workflowId;
        this.variables = new HashMap<>();
        this.startTime = LocalDateTime.now();
        this.lastUpdateTime = LocalDateTime.now();
        this.status = WorkflowStatus.RUNNING;
    }
    
    // Getters and Setters
    public String getWorkflowId() {
        return workflowId;
    }
    
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
    
    public String getCurrentStepId() {
        return currentStepId;
    }
    
    public void setCurrentStepId(String currentStepId) {
        this.currentStepId = currentStepId;
        this.lastUpdateTime = LocalDateTime.now();
    }
    
    public Map<String, Object> getVariables() {
        return variables;
    }
    
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
    
    public void setVariable(String key, Object value) {
        this.variables.put(key, value);
        this.lastUpdateTime = LocalDateTime.now();
    }
    
    public Object getVariable(String key) {
        return this.variables.get(key);
    }
    
    public Map<String, Object> getLastResponse() {
        return lastResponse;
    }
    
    public void setLastResponse(Map<String, Object> lastResponse) {
        this.lastResponse = lastResponse;
        this.lastUpdateTime = LocalDateTime.now();
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    public WorkflowStatus getStatus() {
        return status;
    }
    
    public void setStatus(WorkflowStatus status) {
        this.status = status;
        this.lastUpdateTime = LocalDateTime.now();
    }
    
    public enum WorkflowStatus {
        RUNNING, COMPLETED, FAILED, ABORTED
    }
}
