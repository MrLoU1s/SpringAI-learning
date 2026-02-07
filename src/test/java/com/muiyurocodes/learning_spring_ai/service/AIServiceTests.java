package com.muiyurocodes.learning_spring_ai.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class AIServiceTests {

    @Autowired
    private AIService aiService;

    @Test
    public void testGetJoke() {
        var joke = aiService.getJoke("Tom and Jerry");
        System.out.println(joke);
    }

    @Test
    public void testEmbedText(){
        var embed = aiService.getEmbedding("This is a large text");
        System.out.println(embed.length);
        for(float e: embed){
            System.out.println(e+" ");
        }
    }

    @Test
    public void testStoreEmbedText(){
        aiService.ingestDataToVectorStore();
    }

    @Test
    public void testSimilaritySearch() {
        var res = aiService.similaritySearch("relational database");
        for (var doc : res) {
            System.out.println(res);
        }
    }

//    @Test
//    public void testAskAI() {
//        var req = aiService.askAI("What is AI?");
//        System.out.println(req);
//    }

}



