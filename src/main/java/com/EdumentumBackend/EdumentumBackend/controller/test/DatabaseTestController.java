package com.EdumentumBackend.EdumentumBackend.controller.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test/db")
@CrossOrigin(origins = "*")
public class DatabaseTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/health")
    public ResponseEntity<?> databaseHealthCheck() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Database connection successful");
            response.put("database", metaData.getDatabaseProductName());
            response.put("version", metaData.getDatabaseProductVersion());
            response.put("url", metaData.getURL());
            response.put("username", metaData.getUserName());
            
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Database connection failed: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/tables")
    public ResponseEntity<?> listTables() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Tables retrieved successfully");
            
            Map<String, Object> tableInfo = new HashMap<>();
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                String tableType = tables.getString("TABLE_TYPE");
                tableInfo.put(tableName, tableType);
            }
            response.put("tables", tableInfo);
            
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to retrieve tables: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/quiz-table")
    public ResponseEntity<?> checkQuizTable() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "quiz", "%");
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Quiz table structure retrieved");
            
            Map<String, Object> columnInfo = new HashMap<>();
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String dataType = columns.getString("TYPE_NAME");
                String isNullable = columns.getString("IS_NULLABLE");
                String columnSize = columns.getString("COLUMN_SIZE");
                
                Map<String, String> columnDetails = new HashMap<>();
                columnDetails.put("type", dataType);
                columnDetails.put("nullable", isNullable);
                columnDetails.put("size", columnSize);
                
                columnInfo.put(columnName, columnDetails);
            }
            response.put("columns", columnInfo);
            
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to retrieve quiz table structure: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(response);
        }
    }
} 