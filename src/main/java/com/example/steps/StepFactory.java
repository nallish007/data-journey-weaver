
package com.example.steps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StepFactory {
    
    @Autowired
    private HttpStepExecutor httpStepExecutor;
    
    @Autowired
    private ScriptStepExecutor scriptStepExecutor;
    
    public StepExecutor createStepExecutor(String stepClass) {
        switch (stepClass) {
            case "com.example.steps.HttpStep":
                return httpStepExecutor;
            case "com.example.steps.ScriptStep":
                return scriptStepExecutor;
            default:
                throw new IllegalArgumentException("Unknown step class: " + stepClass);
        }
    }
}
