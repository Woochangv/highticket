package com.woochang.highticket.dto.venue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VenueCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String location;

    @Positive
    private Integer capacity;

    public VenueCreateRequest(String name, String location, int capacity) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
    }
}
