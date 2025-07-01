
# Workflow Engine

A Java Spring Boot application that parses YAML workflow definitions and executes them with support for HTTP requests, conditional routing, and Groovy script execution.

## Features

- **YAML Workflow Parsing**: Parse workflow definitions from YAML format
- **HTTP Step Execution**: Make HTTP requests with configurable timeouts and headers
- **Script Step Execution**: Execute Groovy scripts for data transformation and logic
- **Conditional Routing**: Dynamic workflow routing based on conditions
- **Variable Resolution**: Template variable resolution with `${variable}` syntax
- **Expression Evaluation**: Groovy expression evaluation for conditions
- **REST API**: HTTP endpoints for workflow execution
- **Comprehensive Logging**: Detailed logging for debugging and monitoring

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Building and Running

```bash
# Clone the repository
git clone <your-repo-url>
cd workflow-engine

# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### API Endpoints

#### Execute Workflow from YAML String
```bash
POST /api/workflow/execute?workflowYaml=<yaml-content>
Content-Type: application/json

{
  "apiUrl": "https://jsonplaceholder.typicode.com/users/1",
  "httpMethod": "GET",
  "token": "your-api-token",
  "enrichmentUrl": "https://httpbin.org/post"
}
```

#### Execute Workflow from Resource File
```bash
POST /api/workflow/execute-from-resource?resourcePath=workflows/sample-workflow.yaml
Content-Type: application/json

{
  "apiUrl": "https://jsonplaceholder.typicode.com/users/1",
  "httpMethod": "GET",
  "token": "your-api-token",
  "enrichmentUrl": "https://httpbin.org/post"
}
```

### Example Workflow

The project includes a sample workflow (`src/main/resources/workflows/sample-workflow.yaml`) that demonstrates:

1. **HTTP Request**: Fetch user data from an API
2. **Data Transformation**: Transform user data using Groovy script
3. **Conditional Logic**: Route based on user age (adult vs minor)
4. **Data Enrichment**: Optional data enrichment for adult users
5. **Final Processing**: Compile final results

### Workflow Definition Structure

```yaml
workflow:
  initialKeys: [ apiUrl, httpMethod, payload, token, enrichmentUrl ]
  
  steps:
    - id: stepId
      class: com.example.steps.HttpStep  # or ScriptStep
      config:
        # Step-specific configuration
      next:
        - condition: "${expression}"  # Optional condition
          to: nextStepId
        - to: defaultNextStep
```

### Supported Step Types

#### HTTP Step (`com.example.steps.HttpStep`)
- Execute HTTP requests (GET, POST, PUT, DELETE)
- Configurable headers and body
- Timeout configuration
- Response parsing and variable setting

#### Script Step (`com.example.steps.ScriptStep`)  
- Execute Groovy scripts
- Access to workflow context and variables
- Return values become workflow variables
- Data transformation and business logic

### Variable Resolution

The engine supports template variable resolution using `${variable}` syntax:
- `${apiUrl}` - Direct variable access
- `${lastResponse.statusCode}` - Nested property access  
- `${user.firstName}` - Object property access

### Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=WorkflowEngineTest
```

## Architecture

- **WorkflowParser**: YAML parsing and validation
- **WorkflowEngine**: Core workflow execution engine
- **StepExecutors**: Pluggable step execution implementations
- **ExpressionEvaluator**: Variable resolution and condition evaluation
- **WorkflowContext**: Execution state and variable management

## Configuration

Application configuration is in `src/main/resources/application.yml`:

```yaml
server:
  port: 8080

logging:
  level:
    com.example: DEBUG
```

## License

This project is licensed under the MIT License.
