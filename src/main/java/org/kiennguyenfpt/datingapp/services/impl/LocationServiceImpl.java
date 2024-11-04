package org.kiennguyenfpt.datingapp.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kiennguyenfpt.datingapp.dtos.responses.LocationResponse;
import org.kiennguyenfpt.datingapp.entities.Location;
import org.kiennguyenfpt.datingapp.repositories.LocationRepository;
import org.kiennguyenfpt.datingapp.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LocationServiceImpl implements LocationService {

    private final RestTemplate restTemplate;

    public LocationServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public LocationResponse getCurrentLocation(double latitude, double longitude) {
        String apiKey = "d9a3940071bf49f292a2b88432274bbb";
        String url = String.format("https://api.geoapify.com/v2/place-details?lat=%f&lon=%f&apiKey=%s", latitude, longitude, apiKey);

        // Thực hiện yêu cầu GET và nhận kết quả
        String response = restTemplate.getForObject(url, String.class);

        try {
            return parseLocationResponse(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private LocationRepository locationRepository;

    public Location saveLocation(Location location) {
        return locationRepository.save(location);
    }

    public LocationResponse parseLocationResponse(String jsonResponse) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);

        // Truy cập vào node features
        JsonNode featuresNode = rootNode.path("features").get(0);
        JsonNode propertiesNode = featuresNode.path("properties");

        // Lấy thông tin cần thiết
        String address = propertiesNode.path("formatted").asText();
        double latitude = propertiesNode.path("lat").asDouble();
        double longitude = propertiesNode.path("lon").asDouble();

        return new LocationResponse(address, latitude, longitude);
    }
}
