
package com.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class WorkflowDefinition {
    
    @JsonProperty("initialKeys")
    private List<String> initialKeys;
    
    @JsonProperty("steps")
    private List<WorkflowStep> steps;
    
    // Constructors
    public WorkflowDefinition() {}
    
    public WorkflowDefinition(List<String> initialKeys, List<WorkflowStep> steps) {
        this.initialKeys = initialKeys;
        this.steps = steps;
    }
    
    // Getters and Setters
    public List<String> getInitialKeys() {
        return initialKeys;
    }
    
    public void setInitialKeys(List<String> initialKeys) {
        this.initialKeys = initialKeys;
    }
    
    public List<WorkflowStep> getSteps() {
        return steps;
    }
    
    public void setSteps(List<WorkflowStep> steps) {
        this.steps = steps;
    }
    
    @Override
    public String toString() {
        return "WorkflowDefinition{" +
                "initialKeys=" + initialKeys +
                ", steps=" + steps +
                '}';
    }
}
