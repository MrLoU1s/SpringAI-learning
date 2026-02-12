package com.muiyurocodes.learning_spring_ai.tools;

import com.muiyurocodes.learning_spring_ai.dtos.BookingListResponse;
import com.muiyurocodes.learning_spring_ai.dtos.BookingResponse;
import com.muiyurocodes.learning_spring_ai.entities.BookingStatus;
import com.muiyurocodes.learning_spring_ai.entities.FlightBooking;
import com.muiyurocodes.learning_spring_ai.service.FlightBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FlightBookingTools {

    private final FlightBookingService flightBookingService;

    @Tool(
            name = "flight_booking_tool",
            description = "Create a new flight booking for a user"
    )
    public BookingResponse createBooking(
            @ToolParam(description = "unique Id of the user making the booking (e.g., userId is user123)")
            String userId,
            @ToolParam(description = "The destination of the flight. (e.g., London)")
            String destination,
            @ToolParam(description = "The departure date and time in ISO-8601 format,  for the flight booking (e.g., 2025-12-25T12:00:00) ")
            String departureTime
    ) {
        Instant instant;
        try {
            // Try parsing as Instant (e.g., 2025-12-25T12:00:00Z)
            instant = Instant.parse(departureTime);
        } catch (Exception e) {
            // Fallback: Parse as LocalDateTime and assume UTC if 'Z' is missing
            instant = LocalDateTime.parse(departureTime).toInstant(ZoneOffset.UTC);
        }

        var flightBooking = flightBookingService.createBooking(userId, destination, instant);
        return new BookingResponse(
                flightBooking.getId(),
                flightBooking.getDestination(),
                flightBooking.getDepartureTime().toString(),
                flightBooking.getBookingStatus().toString()
        );
    }

    @Tool(
            name = "get_user_bookings",
            description = "Retrieve all flight bookings for the current user, sorted by departure time(most recent first). " + "Returns an empty list message if none exists."
    )
    public BookingListResponse getUserBookings(
            @ToolParam(description = "The unique user ID", required = true)
            String userId
    ) {
        List<FlightBooking> bookings = flightBookingService.getUserBookings(userId);

        List<BookingResponse> bookingResponses = bookings.stream()
                .map(b -> new BookingResponse(
                        b.getId(),
                        b.getDestination(),
                        b.getDepartureTime().toString(),
                        b.getBookingStatus().toString()
                )).toList();

        String message = bookings.isEmpty()
                ? "You have no upcoming flight bookings."
                : "Here are your current flight bookings:";
        return new BookingListResponse(bookingResponses, message);
    }

    @Tool(
            name = "update_booking_status",
            description = "Update the statis of an existing flight booking (e.g., cancel it). " +
                    "Only the owner of the booking can modify it. " +
                    "Common use: set status to CANCELLED."
    )
    public BookingResponse updateBookingStatus(
            @ToolParam(description = "The booking ID returned from create or get bookings", required = true)
            Long bookingId,

            @ToolParam(description = "The user ID who owns the booking", required = true)
            String userId,

            @ToolParam(description = "New status: CONFIRMED, CANCELLED, or PENDING", required = true)
            BookingStatus newStatus
    ) {
        FlightBooking updated = flightBookingService.updateBookingStatus(bookingId, userId, newStatus);
        return new BookingResponse(
                updated.getId(),
                updated.getDestination(),
                updated.getDepartureTime().toString(),
                updated.getBookingStatus().toString()
        );
    }
}
