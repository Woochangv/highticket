package com.woochang.highticket.domain.venue;

import com.woochang.highticket.domain.BaseTimeEntity;
import com.woochang.highticket.dto.venue.VenueCreateRequest;
import com.woochang.highticket.dto.venue.VenueUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "venues")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Venue extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 50, nullable = false)
    String name;
    @Column(length = 100, nullable = false)
    String location;
    @Column(nullable = false)
    int capacity;

    public Venue(String name, String location, int capacity) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
    }

    public static Venue of(VenueCreateRequest request) {
        return new Venue(request.getName(), request.getLocation(), request.getCapacity());
    }

    public void updateWith(VenueUpdateRequest request) {
        if (request.getName() != null) this.name = request.getName();
        if (request.getLocation() != null) this.location = request.getLocation();
        if (request.getCapacity() != null) this.capacity = request.getCapacity();
    }
}
