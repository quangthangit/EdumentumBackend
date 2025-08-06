package com.EdumentumBackend.EdumentumBackend;

import com.EdumentumBackend.EdumentumBackend.dtos.MindMapDto;
import com.EdumentumBackend.EdumentumBackend.service.MindMapService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MindMapServiceTest {

    @Autowired
    private MindMapService mindMapService;

    @Test
    void testMindMapServiceExists() {
        assertNotNull(mindMapService);
    }
}