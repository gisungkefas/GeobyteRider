package kefas.backend.GeoByte.controller;

import kefas.backend.GeoByte.dto.DeliveryRequestDto;
import kefas.backend.GeoByte.dto.DeliveryResponseDto;
import kefas.backend.GeoByte.entity.DeliveryLocation;
import kefas.backend.GeoByte.entity.DeliveryRoute;
import kefas.backend.GeoByte.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/getAllLocations")
    public ResponseEntity<Page<DeliveryResponseDto>> getAllPosts() {
        return new ResponseEntity<>(deliveryService.getAllDeliveryLocations(1, 10), HttpStatus.OK);
    }

    @PostMapping("/addLocation")
    public ResponseEntity<DeliveryResponseDto> addDeliveryLocation(@RequestBody DeliveryRequestDto requestDTO) {
        DeliveryResponseDto newLocation = deliveryService.addDeliveryLocation(requestDTO);
        return new ResponseEntity<>(newLocation, HttpStatus.CREATED);
    }

    @DeleteMapping("/DeliteLocations/{id}")
    public ResponseEntity<Void> removeLocation(@PathVariable Long id) {
        deliveryService.removeLocation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/updateLocations/{id}")
    public ResponseEntity<DeliveryResponseDto> updateLocation(@PathVariable Long id, @RequestBody DeliveryRequestDto locationDTO) {
        DeliveryResponseDto updatedLocation = deliveryService.updateDeliveryLocation(id, locationDTO);
        return new ResponseEntity<>(updatedLocation, HttpStatus.OK);
    }


    @GetMapping("/optimal-route/{originId}/{destinationId}")
    public ResponseEntity<DeliveryRoute> getOptimalRoute(@PathVariable Long originId, @PathVariable Long destinationId) {
        DeliveryRoute optimalRoute = deliveryService.getOptimalRoute(originId, destinationId);
        return new ResponseEntity<>(optimalRoute, HttpStatus.OK);
    }

    @GetMapping("/most-expensive-route/{originId}/{destinationId}")
    public ResponseEntity<DeliveryRoute> getMostExpensiveRoute(@PathVariable Long originId, @PathVariable Long destinationId) {
        DeliveryRoute mostExpensiveRoute = deliveryService.getOptimalRoute(originId, destinationId);
        return new ResponseEntity<>(mostExpensiveRoute, HttpStatus.OK);
    }

}
