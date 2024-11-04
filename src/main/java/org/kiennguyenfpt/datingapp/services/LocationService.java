package org.kiennguyenfpt.datingapp.services;

import org.kiennguyenfpt.datingapp.dtos.responses.LocationResponse;
import org.kiennguyenfpt.datingapp.entities.Location;

public interface LocationService {

    LocationResponse getCurrentLocation(double latitude, double longitude);

    Location saveLocation(Location location);

}
