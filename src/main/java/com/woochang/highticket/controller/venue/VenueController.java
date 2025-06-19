package com.woochang.highticket.controller.venue;

import com.woochang.highticket.domain.venue.Venue;
import com.woochang.highticket.dto.venue.VenueCreateRequest;
import com.woochang.highticket.dto.venue.VenueUpdateRequest;
import com.woochang.highticket.global.response.ApiResponse;
import com.woochang.highticket.service.venue.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Venue>>> getAllVenues() {
        List<Venue> venues = venueService.findAll();
        return ResponseEntity.ok(ApiResponse.success(venues));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Venue>> getVenue(@PathVariable Long id) {
        Venue venue = venueService.findVenue(id);
        return ResponseEntity.ok(ApiResponse.success(venue));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createVenue(@RequestBody @Valid VenueCreateRequest request) {
        venueService.createVenue(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateVenue(@PathVariable Long id, @RequestBody @Valid VenueUpdateRequest request) {
        venueService.updateVenue(id, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
