
package com.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class RouteMapDefinition {
    
    @JsonProperty("initialKeys")
    private List<String> initialKeys;
    
    @JsonProperty("steps")
    private List<RouteMapStep> steps;
    
    // Constructors
    public RouteMapDefinition() {}
    
    public RouteMapDefinition(List<String> initialKeys, List<RouteMapStep> steps) {
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
    
    public List<RouteMapStep> getSteps() {
        return steps;
    }
    
    public void setSteps(List<RouteMapStep> steps) {
        this.steps = steps;
    }
    
    @Override
    public String toString() {
        return "RouteMapDefinition{" +
                "initialKeys=" + initialKeys +
                ", steps=" + steps +
                '}';
    }
}
