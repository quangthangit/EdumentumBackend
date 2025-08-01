#!/bin/bash

# Quiz API Test Script
BASE_URL="http://localhost:8080"
API_BASE="$BASE_URL/api/v1/test/quiz"

echo "=== Quiz API Test Script ==="
echo "Base URL: $BASE_URL"
echo ""

# Test 1: Health Check
echo "1. Testing Health Check..."
curl -s -X GET "$API_BASE/health" | jq .
echo ""

# Test 2: Get Sample Data
echo "2. Getting Sample Data..."
curl -s -X GET "$API_BASE/sample-data" | jq .
echo ""

# Test 3: Test Ultra Simple Quiz
echo "3. Testing Ultra Simple Quiz..."
curl -s -X POST "$API_BASE/test-ultra-simple" \
  -H "Content-Type: application/json" | jq .
echo ""

# Test 4: Test Simple Quiz
echo "4. Testing Simple Quiz..."
curl -s -X POST "$API_BASE/test-simple" \
  -H "Content-Type: application/json" | jq .
echo ""

# Test 5: Test Minimal Quiz
echo "5. Testing Minimal Quiz..."
curl -s -X POST "$API_BASE/test-minimal" \
  -H "Content-Type: application/json" | jq .
echo ""

# Test 6: Create Math Quiz
echo "6. Creating Math Quiz..."
curl -s -X POST "$API_BASE/create-math-quiz" \
  -H "Content-Type: application/json" | jq .
echo ""

# Test 7: Create Geography Quiz
echo "7. Creating Geography Quiz..."
curl -s -X POST "$API_BASE/test-geography" \
  -H "Content-Type: application/json" | jq .
echo ""

# Test 8: Create Sample Quiz
echo "8. Creating Sample Quiz..."
curl -s -X POST "$API_BASE/create-sample" \
  -H "Content-Type: application/json" | jq .
echo ""

echo "=== Test Complete ===" 