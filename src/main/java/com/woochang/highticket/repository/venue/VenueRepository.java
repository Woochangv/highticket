package com.woochang.highticket.repository.venue;

import com.woochang.highticket.domain.venue.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
