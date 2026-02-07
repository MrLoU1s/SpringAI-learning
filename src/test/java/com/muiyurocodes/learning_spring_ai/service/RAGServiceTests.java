package com.muiyurocodes.learning_spring_ai.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RAGServiceTests {

    @Autowired
    private RAGService ragService;

    @Test
    public void testAskAI() {
        var response = ragService.askAI("What is the Shattering");
        System.out.println(response);
    }

    @Test
    public void testIngestDataToVectorStore() {
        ragService.ingestPdfToVectorStore();
    }

    @Test
    public void testAskAIWithAdvisors() {
        ragService.askAIWithAdvisors("What is my name Jarvis?", "louis1");
    }
}
