
package com.example.steps;

import com.example.model.RouteMapContext;

import java.util.Map;

public interface StepExecutor {
    Map<String, Object> execute(Map<String, Object> config, RouteMapContext context);
}
