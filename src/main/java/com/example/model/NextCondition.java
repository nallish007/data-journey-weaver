
package com.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NextCondition {
    
    @JsonProperty("condition")
    private String condition;
    
    @JsonProperty("to")
    private String to;
    
    // Constructors
    public NextCondition() {}
    
    public NextCondition(String condition, String to) {
        this.condition = condition;
        this.to = to;
    }
    
    // Getters and Setters
    public String getCondition() {
        return condition;
    }
    
    public void setCondition(String condition) {
        this.condition = condition;
    }
    
    public String getTo() {
        return to;
    }
    
    public void setTo(String to) {
        this.to = to;
    }
    
    @Override
    public String toString() {
        return "NextCondition{" +
                "condition='" + condition + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
