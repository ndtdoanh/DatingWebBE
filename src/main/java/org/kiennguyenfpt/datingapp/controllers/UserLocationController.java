package org.kiennguyenfpt.datingapp.controllers;

import org.kiennguyenfpt.datingapp.dtos.requests.UserLocationRequest;
import org.kiennguyenfpt.datingapp.dtos.responses.NearlyUserResponse;
import org.kiennguyenfpt.datingapp.dtos.responses.UserLocationResponse;
import org.kiennguyenfpt.datingapp.entities.UserLocation;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.services.UserLocationService;
import org.kiennguyenfpt.datingapp.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/location")
@CrossOrigin
public class UserLocationController {
    private final UserLocationService userLocationService;

    private final UserService userService;

    public UserLocationController(
            final UserLocationService userLocationService,
            final UserService userService
    ) {
        this.userLocationService = userLocationService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<CommonResponse> saveLocation(@RequestBody UserLocationRequest userLocationRequest) {
        CommonResponse<UserLocation> response = new CommonResponse<>();
        try {
            userLocationService.saveLocation(userLocationRequest);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Location of user created successfully!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/isConfigLocation/{userId}")
    public ResponseEntity checkUserLocation(@PathVariable Long userId) {

        CommonResponse response = new CommonResponse<>();
        try {
            boolean isLocationSet = userLocationService.isLocationSetForUser(userId);
            response.setStatus(HttpStatus.OK.value());
            if (isLocationSet) {
                response.setMessage("Configured location for user");
            } else {
                response.setMessage("Location not configured for user");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/nearby")
    public ResponseEntity getNearbyUsers(@RequestParam Long userId, @RequestParam double range) {
        UserLocation currentLocation = userLocationService.getUserLocation(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not set"));
        CommonResponse response = new CommonResponse<>();

        try {
            List<NearlyUserResponse> nearbyUsers = userService.findNearbyUsers(currentLocation, range);
            response.setStatus(HttpStatus.OK.value());
            response.setData(nearbyUsers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // GET endpoint to retrieve user location by userId
    @GetMapping("/{userId}")
    public ResponseEntity getUserLocation(@PathVariable Long userId) {
        CommonResponse response = new CommonResponse<>();

        try {
            Optional<UserLocation> optional = userLocationService.getUserLocation(userId);
            response.setStatus(HttpStatus.OK.value());
            if (optional.isEmpty()) {
                response.setData(null);
            } else {
                response.setData(new UserLocationResponse(
                        optional.get().getLatitude(),
                        optional.get().getLongitude(),
                        optional.get().getAddress()
                ));
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    // DELETE endpoint to delete user location by userId
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUserLocation(@PathVariable Long userId) {
        boolean deleted = userLocationService.deleteUserLocationByUserId(userId);
        if (deleted) {
            return ResponseEntity.ok("Location deleted successfully");
        } else {
            return ResponseEntity.status(404).body("Location not found");
        }
    }

}
