
package com.example.service;

import com.example.model.WorkflowContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExpressionEvaluator {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpressionEvaluator.class);
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)}");
    
    private final ScriptEngine scriptEngine;
    
    public ExpressionEvaluator() {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.scriptEngine = manager.getEngineByName("groovy");
        if (this.scriptEngine == null) {
            logger.warn("Groovy script engine not available, using JavaScript");
            this.scriptEngine = manager.getEngineByName("javascript");
        }
    }
    
    public boolean evaluate(String expression, WorkflowContext context) {
        try {
            String resolvedExpression = resolveVariables(expression, context);
            logger.debug("Evaluating expression: {} -> {}", expression, resolvedExpression);
            
            // Set context variables in script engine
            scriptEngine.put("context", context.getVariables());
            if (context.getLastResponse() != null) {
                scriptEngine.put("lastResponse", context.getLastResponse());
            }
            
            Object result = scriptEngine.eval(resolvedExpression);
            boolean boolResult = Boolean.TRUE.equals(result);
            logger.debug("Expression result: {}", boolResult);
            
            return boolResult;
        } catch (ScriptException e) {
            logger.error("Error evaluating expression '{}': {}", expression, e.getMessage());
            return false;
        }
    }
    
    public String resolveVariables(String input, WorkflowContext context) {
        if (input == null) {
            return null;
        }
        
        Matcher matcher = VARIABLE_PATTERN.matcher(input);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String variablePath = matcher.group(1);
            Object value = resolveVariablePath(variablePath, context);
            matcher.appendReplacement(result, value != null ? value.toString() : "null");
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
    
    private Object resolveVariablePath(String path, WorkflowContext context) {
        String[] parts = path.split("\\.");
        Object current = null;
        
        // Check context variables first
        if (context.getVariables().containsKey(parts[0])) {
            current = context.getVariables().get(parts[0]);
        } else if ("lastResponse".equals(parts[0]) && context.getLastResponse() != null) {
            current = context.getLastResponse();
        }
        
        // Navigate through nested properties
        for (int i = 1; i < parts.length && current != null; i++) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(parts[i]);
            } else {
                // Could add reflection-based property access here if needed
                current = null;
            }
        }
        
        return current;
    }
}
