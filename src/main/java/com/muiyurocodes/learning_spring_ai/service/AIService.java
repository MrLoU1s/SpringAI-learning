package com.muiyurocodes.learning_spring_ai.service;


import com.muiyurocodes.learning_spring_ai.dtos.Joke;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;




@Service
@RequiredArgsConstructor
public class AIService {

    private final ChatClient chatClient;
    //enable embedding
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    public float[] getEmbedding(String text){
        return embeddingModel.embed(text);
    }

    public void ingestDataToVectorStore(String text){
    Document document = new Document(text);
    vectorStore.add(List.of(document));
    }

    public  String getJoke(String topic){
    String systemPrompt = """
            You are a sarcastic Jester, give response in only 4 lines.
            You don't fear making jokes about dark humour or sensitive topics.
            Ensure that laughable score is 10/10
            Give a joke on the topic:{topic}
            """;

    //converting to prompt template
    PromptTemplate promptTemplate = new PromptTemplate(systemPrompt);
    String renderedText = promptTemplate.render(Map.of("topic", topic));

        var response =  chatClient.prompt()
                .user(renderedText)
                .advisors(
                        new SimpleLoggerAdvisor()
                )
                .call()
                .entity(Joke.class);

        return response.text();
    }
}
