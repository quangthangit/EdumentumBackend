# Quiz API Testing Guide

## Overview
This guide provides instructions for testing the quiz creation API endpoints that have been refactored to use the new quiz data structure.

## Prerequisites
1. Make sure your Spring Boot application is running on `http://localhost:8080`
2. Ensure your database is properly configured and accessible
3. Install `curl` and `jq` for command-line testing (optional)

## API Endpoints

### 1. Health Check
**GET** `/api/v1/test/quiz/health`
- **Purpose**: Verify the API is running
- **Response**: Basic status information

### 2. Get Sample Data
**GET** `/api/v1/test/quiz/sample-data`
- **Purpose**: View the structure of sample quiz data
- **Response**: Complete quiz data structure for reference

### 3. Test Ultra Simple Quiz
**POST** `/api/v1/test/quiz/test-ultra-simple`
- **Purpose**: Create the simplest possible quiz (recommended first test)
- **Data**: Pre-built minimal quiz with 1 question
- **Response**: Created quiz with ID and full data

### 4. Test Simple Quiz
**POST** `/api/v1/test/quiz/test-simple`
- **Purpose**: Create a simple math quiz
- **Data**: Pre-built quiz with 1 math question
- **Response**: Created quiz with ID and full data

### 5. Test Minimal Quiz
**POST** `/api/v1/test/quiz/test-minimal`
- **Purpose**: Create a minimal quiz with basic structure
- **Data**: Pre-built quiz with 1 question and 2 answers
- **Response**: Created quiz with ID and full data

### 6. Create Math Quiz
**POST** `/api/v1/test/quiz/create-math-quiz`
- **Purpose**: Create a math quiz with multiple questions
- **Data**: Pre-built quiz with 2 math questions
- **Response**: Created quiz with ID and full data

### 7. Create Geography Quiz
**POST** `/api/v1/test/quiz/test-geography`
- **Purpose**: Create a geography quiz
- **Data**: Pre-built quiz with geography and astronomy questions
- **Response**: Created quiz with ID and full data

### 8. Create Sample Quiz
**POST** `/api/v1/test/quiz/create-sample`
- **Purpose**: Create a sample quiz with mixed content
- **Data**: Pre-built quiz with general knowledge questions
- **Response**: Created quiz with ID and full data

### 9. Create Custom Quiz
**POST** `/api/v1/test/quiz/create`
- **Purpose**: Create a custom quiz with your own data
- **Data**: JSON payload with quiz structure
- **Response**: Created quiz with ID and full data

## Testing Methods

### Method 1: Using HTTP Client (Recommended)
Use the provided `test-quiz-api.http` file with your HTTP client (VS Code REST Client, IntelliJ HTTP Client, etc.)

### Method 2: Using cURL
```bash
# Health check
curl -X GET http://localhost:8080/api/v1/test/quiz/health

# Test ultra simple quiz
curl -X POST http://localhost:8080/api/v1/test/quiz/test-ultra-simple \
  -H "Content-Type: application/json"
```

### Method 3: Using the Test Script
```bash
# Make the script executable
chmod +x test-quiz.sh

# Run the test script
./test-quiz.sh
```

### Method 4: Using Postman
1. Import the endpoints from the HTTP file
2. Set the base URL to `http://localhost:8080`
3. Run the requests in sequence

## Expected Response Structure

### Success Response
```json
{
  "status": "success",
  "message": "Quiz created successfully",
  "data": {
    "quizId": 1,
    "title": "Quiz Title",
    "description": "Quiz Description",
    "visibility": true,
    "total": 1,
    "topic": "Topic",
    "quizCreationType": "MANUAL",
    "quizData": {
      "questions": [...],
      "metadata": {...},
      "settings": {...}
    }
  }
}
```

### Error Response
```json
{
  "status": "error",
  "message": "Error description",
  "errorType": "ExceptionClassName"
}
```

## Troubleshooting

### Common Issues

1. **Connection Refused**
   - Ensure the Spring Boot application is running
   - Check if the port 8080 is available

2. **Database Connection Error**
   - Verify database configuration in `application.properties`
   - Check if the database is running and accessible

3. **JSON Parsing Error**
   - Ensure all required fields are present in the request
   - Check the data structure matches the expected format

4. **Validation Error**
   - Verify all required fields have valid values
   - Check field constraints (e.g., title length, question count)

### Debug Steps

1. **Start with Health Check**
   ```bash
   curl -X GET http://localhost:8080/api/v1/test/quiz/health
   ```

2. **Test Ultra Simple Quiz First**
   ```bash
   curl -X POST http://localhost:8080/api/v1/test/quiz/test-ultra-simple
   ```

3. **Check Application Logs**
   - Look for detailed error messages in the console
   - Check for database connection issues

4. **Verify Database Schema**
   - Ensure the quiz table exists with correct columns
   - Check if JSONB columns are properly configured

## Data Structure Reference

### QuizRequestDto
```json
{
  "title": "string (required)",
  "description": "string (required)",
  "visibility": "boolean",
  "total": "integer (required)",
  "topic": "string (required)",
  "quizCreationType": "MANUAL",
  "questions": [
    {
      "id": "string",
      "question": "string",
      "type": "MULTIPLE_CHOICE",
      "difficulty": "string",
      "bloom_level": "string",
      "points": "integer",
      "order_index": "integer",
      "explanation": "string",
      "answers": [
        {
          "id": "string",
          "text": "string",
          "correct": "boolean",
          "order_index": "integer"
        }
      ]
    }
  ]
}
```

## Next Steps

After successful testing:
1. Integrate the quiz creation with your frontend
2. Add user authentication and authorization
3. Implement quiz management features (edit, delete, list)
4. Add quiz taking functionality
5. Implement scoring and analytics 