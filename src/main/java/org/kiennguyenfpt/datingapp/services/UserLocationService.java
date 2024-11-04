package org.kiennguyenfpt.datingapp.services;

import org.kiennguyenfpt.datingapp.dtos.requests.UserLocationRequest;
import org.kiennguyenfpt.datingapp.dtos.responses.LocationResponse;
import org.kiennguyenfpt.datingapp.entities.UserLocation;

import java.util.Optional;

public interface UserLocationService {

    LocationResponse getCurrentLocation(double latitude, double longitude);

    void saveLocation(UserLocationRequest userLocationRequest);

    boolean isLocationSetForUser(Long userId);

    Optional<UserLocation> getUserLocation(Long userId);

    boolean deleteUserLocationByUserId(Long userId);
}
