package com.muiyurocodes.learning_spring_ai.dtos;

public record Joke(
        String text,
        String category,
        String laughScore,
        Boolean isNSFW
){

}
