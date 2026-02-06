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
import java.util.stream.Collectors;


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

    public String askAI (String prompt){

        String template = """
                You are an AI assistant helping a developer.
                
                Rules:
                -use ONLY the information provided in the context
                -You may rephrase, summarize, and explain in natural language
                -Do not introduce new concepts or facts. 
                -If multiple context sections are relevant, combine them into a single explanation. 
                -If the answer is NOT present, say "I don't know King :("
                
                Context:
                {context}
                
                Answer in a friendly, conversation tone. 
                """;

        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query(prompt)
                .topK(2)
                        .similarityThreshold(0.5)
                .filterExpression("topic =='Spring Boot' or topic =='Spring AI'")
                .build());

        //converting docs to string
        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        PromptTemplate promptTemplate = new PromptTemplate(template);
        String systemPrompt = promptTemplate.render(Map.of("context", context));


        return chatClient.prompt()
                .system(systemPrompt)
                .user(prompt)
                .advisors(
                        new SimpleLoggerAdvisor()
                )
                .call()
                .content();
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
        vectorStore.add(springAiDocs());
    }

    public static List<Document> springAiDocs(){
        return List.of(
                new Document("Spring AI provides a unified interface for interacting with various AI models, including OpenAI, Ollama, and Azure OpenAI.",
                        Map.of("topic", "Spring AI", "type", "Framework")),
                new Document("The ChatClient API in Spring AI offers a fluent and expressive way to communicate with chat models, supporting prompts, system messages, and function calling.",
                        Map.of("topic", "ChatClient", "type", "API")),
                new Document("Spring AI supports Embedding Models, which convert text into vector representations for semantic search and RAG (Retrieval-Augmented Generation) applications.",
                        Map.of("topic", "Embeddings", "type", "Concept")),
                new Document("Vector Stores in Spring AI allow storing and retrieving high-dimensional vectors, enabling similarity search with databases like PGVector, Redis, and Chroma.",
                        Map.of("topic", "Vector Store", "type", "Database")),
                new Document("Retrieval-Augmented Generation (RAG) is a pattern supported by Spring AI where relevant data is fetched from a vector store and passed to the LLM for grounded responses.",
                        Map.of("topic", "RAG", "type", "Pattern")),
                new Document("Function Calling in Spring AI allows the LLM to invoke Java methods dynamically, enabling integration with external APIs and services.",
                        Map.of("topic", "Function Calling", "type", "Feature")),
                new Document("Spring AI's PromptTemplate helps in structuring dynamic prompts with placeholders, ensuring consistent and reusable interactions with AI models.",
                        Map.of("topic", "Prompt Engineering", "type", "Utility")),
                new Document("Output Parsers in Spring AI automatically map the unstructured text response from an LLM into structured Java objects like Records or Beans.",
                        Map.of("topic", "Output Parsing", "type", "Utility")),
                new Document("Spring AI supports Multimodal models, allowing users to send both text and images to compatible models like GPT-4 Vision for analysis.",
                        Map.of("topic", "Multimodal", "type", "Feature")),
                new Document("The Advisors API in Spring AI allows intercepting and modifying chat requests and responses, useful for logging, memory management, and safety checks.",
                        Map.of("topic", "Advisors", "type", "API"))
        );
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
