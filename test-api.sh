#!/bin/bash

# Test API endpoints
BASE_URL="http://localhost:8080"

echo "🧪 Testing API endpoints..."
echo "================================"

# Test 1: Basic connectivity
echo "1. Testing basic connectivity..."
curl -X GET "$BASE_URL/api/v1/test/ping" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test 2: Echo endpoint
echo "2. Testing echo endpoint..."
curl -X POST "$BASE_URL/api/v1/test/echo" \
  -H "Content-Type: application/json" \
  -d '{"test": "data"}' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test 3: Sample quiz data
echo "3. Testing sample quiz data..."
curl -X GET "$BASE_URL/api/v1/test/quiz/sample-data" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test 4: Create sample quiz
echo "4. Testing create sample quiz..."
curl -X POST "$BASE_URL/api/v1/test/quiz/create-sample" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"
echo "✅ Testing completed!" 