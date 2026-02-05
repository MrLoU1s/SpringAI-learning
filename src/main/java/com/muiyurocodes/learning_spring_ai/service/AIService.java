package com.muiyurocodes.learning_spring_ai.service;


import com.muiyurocodes.learning_spring_ai.dtos.Joke;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
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

    public void ingestDataToVectorStore(){
        List<Document> documents = List.of(
                new Document("Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can 'just run'.",
                        Map.of("topic", "Spring Boot", "type", "Framework")),
                new Document("Java is a high-level, class-based, object-oriented programming language that is designed to have as few implementation dependencies as possible.",
                        Map.of("topic", "Java", "type", "Language")),
                new Document("Docker is a set of platform as a service products that use OS-level virtualization to deliver software in packages called containers.",
                        Map.of("topic", "Docker", "type", "DevOps")),
                new Document("Kubernetes is an open-source container orchestration system for automating software deployment, scaling, and management.",
                        Map.of("topic", "Kubernetes", "type", "DevOps")),
                new Document("PostgreSQL is a free and open-source relational database management system emphasizing extensibility and SQL compliance.",
                        Map.of("topic", "PostgreSQL", "type", "Database")),
                new Document("React is a free and open-source front-end JavaScript library for building user interfaces based on UI components.",
                        Map.of("topic", "React", "type", "Frontend")),
                new Document("Microservices is an architectural style that structures an application as a collection of services that are highly maintainable and testable.",
                        Map.of("topic", "Microservices", "type", "Architecture")),
                new Document("REST (Representational State Transfer) is a software architectural style that defines a set of constraints to be used for creating Web services.",
                        Map.of("topic", "REST", "type", "API")),
                new Document("GraphQL is a query language for APIs and a runtime for fulfilling those queries with your existing data.",
                        Map.of("topic", "GraphQL", "type", "API")),
                new Document("Git is a distributed version control system that tracks changes in any set of computer files, usually used for coordinating work among programmers.",
                        Map.of("topic", "Git", "type", "Version Control"))
        );

        vectorStore.add(documents);
    }

    public List<Document>  similaritySearch(String text){
       return  vectorStore.similaritySearch(SearchRequest.builder()
                       .query(text)
                       .topK(2)
               .build());
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
