
package com.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class WorkflowStep {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("class")
    private String stepClass;
    
    @JsonProperty("config")
    private Map<String, Object> config;
    
    @JsonProperty("next")
    private List<NextCondition> next;
    
    // Constructors
    public WorkflowStep() {}
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getStepClass() {
        return stepClass;
    }
    
    public void setStepClass(String stepClass) {
        this.stepClass = stepClass;
    }
    
    public Map<String, Object> getConfig() {
        return config;
    }
    
    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
    
    public List<NextCondition> getNext() {
        return next;
    }
    
    public void setNext(List<NextCondition> next) {
        this.next = next;
    }
    
    @Override
    public String toString() {
        return "WorkflowStep{" +
                "id='" + id + '\'' +
                ", stepClass='" + stepClass + '\'' +
                ", config=" + config +
                ", next=" + next +
                '}';
    }
}
