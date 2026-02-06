package com.muiyurocodes.learning_spring_ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RAGService {

    private final ChatClient chatClient;
    //enable embedding
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    //The pdf resource is passed as follows
    @Value("classpath:Elden_Ring_Lore.pdf")
    Resource eldenRingLorePdf;

    public String askAI (String prompt){

        String template = """
                You are an AI assistant helping a developer, who is a huge fan of the video game Elden Ring.
                
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
                .topK(4)
                .similarityThreshold(0.4)
                        .filterExpression("file_name=='Elden_Ring_Lore.pdf'")
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

    public void  ingestPdfToVectorStore(){
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(eldenRingLorePdf);
        //Retrieves pages as documents
        List<Document> pages = pdfReader.get();
        //Split the documents into smaller chunks using a Transformer
        TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                .withChunkSize(300)
                .build();
        List<Document> chunks = tokenTextSplitter.apply(pages);
        //Adding the chunks to the vector store
        vectorStore.add(chunks);






    }

}
