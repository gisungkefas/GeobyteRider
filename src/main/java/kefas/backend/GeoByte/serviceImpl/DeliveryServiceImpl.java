package kefas.backend.GeoByte.serviceImpl;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import kefas.backend.GeoByte.dto.DeliveryRequestDto;
import kefas.backend.GeoByte.dto.DeliveryResponseDto;
import kefas.backend.GeoByte.entity.DeliveryLocation;
import kefas.backend.GeoByte.entity.DeliveryRoute;
import kefas.backend.GeoByte.entity.Users;
import kefas.backend.GeoByte.exception.LocationNotFoundException;
import kefas.backend.GeoByte.exception.UserNotFoundException;
import kefas.backend.GeoByte.repository.DeliveryLocationRepository;
import kefas.backend.GeoByte.repository.UserRepository;
import kefas.backend.GeoByte.service.DeliveryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DeliveryServiceImpl implements DeliveryService {

        private final DeliveryLocationRepository locationRepository;
        private final UserRepository userRepository;

    @Autowired
    public DeliveryServiceImpl(DeliveryLocationRepository locationRepository, UserRepository userRepository) {
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<DeliveryResponseDto> getAllDeliveryLocations(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return locationRepository.findAll(pageRequest)
                .map(location -> {
                    DeliveryResponseDto responseDto = new DeliveryResponseDto();
                    BeanUtils.copyProperties(location, responseDto);
                    return responseDto;
                });
    }

    @Override
    public DeliveryResponseDto addDeliveryLocation(DeliveryRequestDto requestDTO) {

        Users user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found for email: " + requestDTO.getEmail()));

        DeliveryLocation deliveryLocation = new DeliveryLocation();
        deliveryLocation.setEmail(user.getEmail());
        deliveryLocation.setItem(requestDTO.getItem());
        deliveryLocation.setLongitude(requestDTO.getLongitude());
        deliveryLocation.setLatitude(requestDTO.getLatitude());
        deliveryLocation.setClearingCost(requestDTO.getClearingCost());
        deliveryLocation.setUser(user);

        DeliveryLocation savedDeliveryLocation = locationRepository.save(deliveryLocation);

        DeliveryResponseDto deliveryResponseDto = new DeliveryResponseDto();
        BeanUtils.copyProperties(savedDeliveryLocation, deliveryResponseDto);

        return deliveryResponseDto;
    }

    @Override
    public void removeLocation(Long id) {
        if (locationRepository.existsById(id)) {
            locationRepository.deleteById(id);
        } else {
            throw new LocationNotFoundException(id);
        }
    }

    @Override
    public DeliveryResponseDto updateDeliveryLocation(Long id, DeliveryRequestDto requestDTO) {

        Optional<Users> user = userRepository.findByEmail(requestDTO.getEmail());

        if (user.isEmpty()) {
            throw new UserNotFoundException("User with provided email was not found");
        }

        DeliveryLocation existingLocation = new DeliveryLocation();
        existingLocation.setItem(requestDTO.getItem());
        existingLocation.setLongitude(requestDTO.getLongitude());
        existingLocation.setLatitude(requestDTO.getLatitude());
        existingLocation.setClearingCost(requestDTO.getClearingCost());

        DeliveryLocation updatedDeliveryLocation = locationRepository.save(existingLocation);

        DeliveryResponseDto deliveryResponseDto = new DeliveryResponseDto();
        BeanUtils.copyProperties(updatedDeliveryLocation, deliveryResponseDto);

        return deliveryResponseDto;
    }

    @Override
    public DeliveryRoute getOptimalRoute(Long originId, Long destinationId) {
        List<DeliveryResponseDto> waypoints = getAllLocationsExceptOriginAndDestination(originId, destinationId);

        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey("your-google-api-key")
                .build();

        String[] waypointAddresses = waypoints.stream()
                .map(location -> location.getLatitude() + "," + location.getLongitude())
                .toArray(String[]::new);

        DistanceMatrixApiRequest distanceMatrixApiRequest = DistanceMatrixApi.newRequest(geoApiContext)
                .mode(TravelMode.DRIVING);

        try {
            DistanceMatrix distanceMatrix = distanceMatrixApiRequest.await();

            return new DeliveryRoute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while calculating optimal route.");
        }
    }

    private List<DeliveryResponseDto> getAllLocationsExceptOriginAndDestination(Long originId, Long destinationId) {
        List<DeliveryResponseDto> allLocations = getAllDeliveryLocations(0, Integer.MAX_VALUE).getContent();

        allLocations.removeIf(location -> location.getId().equals(originId) || location.getId().equals(destinationId));

        return allLocations;
    }


}
