package com.muiyurocodes.learning_spring_ai.repositories;

import com.muiyurocodes.learning_spring_ai.entities.FlightBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface FlightBookingRepository extends JpaRepository<FlightBooking, Long> {

    List<FlightBooking> findByUserIdOrderByDepartureTimeDesc(String userId);

    boolean existsByUserIdAndDestinationAndDepartureTime(
            String userId, String destination, Instant departureTime
    );

}
