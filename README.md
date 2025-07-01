
# RouteMap Engine

A Java Spring Boot application that parses YAML routemap definitions and executes them with support for HTTP requests, conditional routing, and Groovy script execution.

## Features

- **YAML RouteMap Parsing**: Parse routemap definitions from YAML format
- **HTTP Step Execution**: Make HTTP requests with configurable timeouts and headers
- **Script Step Execution**: Execute Groovy scripts for data transformation and logic
- **Conditional Routing**: Dynamic routemap routing based on conditions
- **Variable Resolution**: Template variable resolution with `${variable}` syntax
- **Expression Evaluation**: Groovy expression evaluation for conditions
- **REST API**: HTTP endpoints for routemap execution
- **Comprehensive Logging**: Detailed logging for debugging and monitoring

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Building and Running

```bash
# Clone the repository
git clone <your-repo-url>
cd routemap-engine

# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### API Endpoints

#### Execute RouteMap from YAML String
```bash
POST /api/routemap/execute?routeMapYaml=<yaml-content>
Content-Type: application/json

{
  "apiUrl": "https://jsonplaceholder.typicode.com/users/1",
  "httpMethod": "GET",
  "token": "your-api-token",
  "enrichmentUrl": "https://httpbin.org/post"
}
```

#### Execute RouteMap from Resource File
```bash
POST /api/routemap/execute-from-resource?resourcePath=routemaps/sample-routemap.yaml
Content-Type: application/json

{
  "apiUrl": "https://jsonplaceholder.typicode.com/users/1",
  "httpMethod": "GET",
  "token": "your-api-token",
  "enrichmentUrl": "https://httpbin.org/post"
}
```

### Example RouteMap

The project includes a sample routemap (`src/main/resources/routemaps/sample-routemap.yaml`) that demonstrates:

1. **HTTP Request**: Fetch user data from an API
2. **Data Transformation**: Transform user data using Groovy script
3. **Conditional Logic**: Route based on user age (adult vs minor)
4. **Data Enrichment**: Optional data enrichment for adult users
5. **Final Processing**: Compile final results

### RouteMap Definition Structure

```yaml
routemap:
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
- Access to routemap context and variables
- Return values become routemap variables
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
mvn test -Dtest=RouteMapEngineTest
```

## Architecture

- **RouteMapParser**: YAML parsing and validation
- **RouteMapEngine**: Core routemap execution engine
- **StepExecutors**: Pluggable step execution implementations
- **ExpressionEvaluator**: Variable resolution and condition evaluation
- **RouteMapContext**: Execution state and variable management

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
