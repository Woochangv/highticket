package com.woochang.highticket.dto.venue;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VenueUpdateRequest {

    private String name;
    private String location;
    private Integer capacity;

    public VenueUpdateRequest(String name, String location, int capacity) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
    }
}
