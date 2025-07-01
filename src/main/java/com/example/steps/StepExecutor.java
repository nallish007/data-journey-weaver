
package com.example.steps;

import com.example.model.WorkflowContext;

import java.util.Map;

public interface StepExecutor {
    Map<String, Object> execute(Map<String, Object> config, WorkflowContext context);
}
