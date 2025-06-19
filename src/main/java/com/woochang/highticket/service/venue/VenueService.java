package com.woochang.highticket.service.venue;

import com.woochang.highticket.domain.venue.Venue;
import com.woochang.highticket.dto.venue.VenueCreateRequest;
import com.woochang.highticket.dto.venue.VenueUpdateRequest;
import com.woochang.highticket.global.exception.BusinessException;
import com.woochang.highticket.global.exception.ErrorCode;
import com.woochang.highticket.repository.venue.VenueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;

    public void createVenue(VenueCreateRequest request) {
        venueRepository.save(Venue.of(request));
    }

    public List<Venue> findAll() {
        return venueRepository.findAll();
    }

    public Venue findVenue(Long id) {
        return venueRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.VENUE_NOT_FOUND));
    }

    @Transactional
    public void updateVenue(Long id, VenueUpdateRequest request) {
        Venue venue = venueRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.VENUE_NOT_FOUND));
        venue.updateWith(request);
    }

    public void deleteVenue(Long id) {
        venueRepository.deleteById(id);
    }
}
