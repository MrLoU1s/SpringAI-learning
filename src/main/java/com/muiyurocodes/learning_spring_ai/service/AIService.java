package com.muiyurocodes.learning_spring_ai.service;


import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@Service
@RequiredArgsConstructor
public class AIService {

    private final ChatClient chatClient;

    public  String getJoke(String topic){
        return chatClient.prompt()
                .user("Give me a joke on the topic: "+topic)
                .call().content();
    }
}
