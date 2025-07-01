
package com.example.steps;

import com.example.model.WorkflowContext;
import com.example.service.ExpressionEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ScriptStepExecutor implements StepExecutor {
    
    private static final Logger logger = LoggerFactory.getLogger(ScriptStepExecutor.class);
    
    @Autowired
    private ExpressionEvaluator expressionEvaluator;
    
    private final ScriptEngine scriptEngine;
    
    public ScriptStepExecutor() {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.scriptEngine = manager.getEngineByName("groovy");
        if (this.scriptEngine == null) {
            logger.warn("Groovy script engine not available, using JavaScript");
            this.scriptEngine = manager.getEngineByName("javascript");
        }
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> config, WorkflowContext context) {
        String language = (String) config.getOrDefault("language", "groovy");
        String script = (String) config.get("script");
        
        if (script == null) {
            logger.error("Script is null in configuration");
            return new HashMap<>();
        }
        
        logger.info("Executing {} script", language);
        logger.debug("Script content: {}", script);
        
        try {
            // Set context variables in script engine
            scriptEngine.put("context", context.getVariables());
            if (context.getLastResponse() != null) {
                scriptEngine.put("lastResponse", context.getLastResponse());
            }
            
            // Execute script
            Object result = scriptEngine.eval(script);
            
            if (result instanceof Map) {
                Map<String, Object> resultMap = (Map<String, Object>) result;
                logger.info("Script executed successfully, returned {} variables", resultMap.size());
                return resultMap;
            } else {
                logger.info("Script executed successfully");
                return new HashMap<>();
            }
            
        } catch (ScriptException e) {
            logger.error("Error executing script: {}", e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("scriptError", e.getMessage());
            return errorResult;
        }
    }
}
