package com.muiyurocodes.learning_spring_ai.dtos;

import java.util.List;

public record BookingListResponse(
        List<BookingResponse> bookingsList, String message
) {
}
