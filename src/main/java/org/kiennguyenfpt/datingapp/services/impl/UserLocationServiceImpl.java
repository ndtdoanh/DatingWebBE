package org.kiennguyenfpt.datingapp.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kiennguyenfpt.datingapp.dtos.requests.UserLocationRequest;
import org.kiennguyenfpt.datingapp.dtos.responses.LocationResponse;
import org.kiennguyenfpt.datingapp.entities.User;
import org.kiennguyenfpt.datingapp.entities.UserLocation;
import org.kiennguyenfpt.datingapp.repositories.UserLocationRepository;
import org.kiennguyenfpt.datingapp.repositories.UserRepository;
import org.kiennguyenfpt.datingapp.services.UserLocationService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class UserLocationServiceImpl implements UserLocationService {

    private final RestTemplate restTemplate;

    private final UserRepository userRepository;

    private final UserLocationRepository userLocationRepository;

    public UserLocationServiceImpl(
            RestTemplate restTemplate,
            UserRepository userRepository,
            UserLocationRepository userLocationRepository
    ) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.userLocationRepository = userLocationRepository;
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

    @Override
    public void saveLocation(UserLocationRequest userLocationRequest) {
        // Tìm User entity dựa trên userId
        User user = userRepository.findById(userLocationRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tìm UserLocation entity dựa trên User
        Optional<UserLocation> existingLocation = userLocationRepository.findByUserId(user.getUserId());

        UserLocation userLocation;

        if (existingLocation.isPresent()) {
            // Nếu đã có thông tin location cho user thì cập nhật
            userLocation = existingLocation.get();
        } else {
            // Nếu chưa có thì tạo mới
            userLocation = new UserLocation();
            userLocation.setUser(user); // Set the User entity
        }

        // Cập nhật các thông tin vị trí từ request
        userLocation.setLatitude(userLocationRequest.getLatitude());
        userLocation.setLongitude(userLocationRequest.getLongitude());
        userLocation.setAddress(userLocationRequest.getAddress());

        // Lưu lại thông tin vị trí của user vào cơ sở dữ liệu
        userLocationRepository.save(userLocation);
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

    @Override
    public boolean isLocationSetForUser(Long userId) {
        // Query the database to see if the user has a location set
        Optional<UserLocation> userLocation = userLocationRepository.findByUserId(userId);
        return userLocation.isPresent();
    }

    @Override
    public Optional<UserLocation> getUserLocation(Long userId) {
        Optional<UserLocation> userLocation = userLocationRepository.findByUserId(userId);
        return userLocation;
    }

    @Override
    public boolean deleteUserLocationByUserId(Long userId) {
        if (userLocationRepository.findByUserId(userId).isPresent()) {
            userLocationRepository.deleteByUserId(userId);
            return true;
        }
        return false;
    }
}
