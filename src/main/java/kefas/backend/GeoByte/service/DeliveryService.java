package kefas.backend.GeoByte.service;

import kefas.backend.GeoByte.dto.DeliveryRequestDto;
import kefas.backend.GeoByte.dto.DeliveryResponseDto;
import kefas.backend.GeoByte.entity.DeliveryRoute;
import org.springframework.data.domain.Page;

public interface DeliveryService {
    Page<DeliveryResponseDto> getAllDeliveryLocations(int page, int size);

    DeliveryResponseDto addDeliveryLocation(DeliveryRequestDto requestDTO);

    void removeLocation(Long id);

    DeliveryResponseDto updateDeliveryLocation(Long id, DeliveryRequestDto requestDTO);

    DeliveryRoute getOptimalRoute(Long originId, Long destinationId);
}
