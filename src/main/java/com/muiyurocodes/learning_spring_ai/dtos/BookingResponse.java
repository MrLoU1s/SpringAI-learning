package com.muiyurocodes.learning_spring_ai.dtos;

public record BookingResponse(
        Long id,
        String destination,
        String departureTime,
        String status
) {
}
