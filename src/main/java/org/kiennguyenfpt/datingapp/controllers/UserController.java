package org.kiennguyenfpt.datingapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kiennguyenfpt.datingapp.dtos.mapper.ProfileMapper;
import org.kiennguyenfpt.datingapp.dtos.requests.UpdateProfileRequest;
import org.kiennguyenfpt.datingapp.entities.Photo;
import org.kiennguyenfpt.datingapp.entities.User;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.security.JwtUtil;
import org.kiennguyenfpt.datingapp.services.LocationService;
import org.kiennguyenfpt.datingapp.services.PhotoService;
import org.kiennguyenfpt.datingapp.services.UserLocationService;
import org.kiennguyenfpt.datingapp.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/v1/users")
@CrossOrigin

public class UserController {
    private final UserService userService;
    private final PhotoService photoService;
    private final JwtUtil jwtUtil;
    private final ProfileMapper profileMapper;

    private final UserLocationService userLocationService;

    public UserController(
            UserService userService,
            PhotoService photoService,
            JwtUtil jwtUtil,
            ProfileMapper profileMapper,
            UserLocationService userLocationService
    ) {
        this.userService = userService;
        this.photoService = photoService;
        this.jwtUtil = jwtUtil;
        this.profileMapper = profileMapper;
        this.userLocationService = userLocationService;
    }

    private ResponseEntity<CommonResponse<Map<String, Object>>> handleUpdateProfile(
            String updateProfileRequestJson, List<MultipartFile> files, String authorizationHeader) {

        CommonResponse<Map<String, Object>> response = new CommonResponse<>();

        try {
            // Parse the JSON string to UpdateProfileRequest object
            UpdateProfileRequest updateProfileRequest = new ObjectMapper().readValue(updateProfileRequestJson, UpdateProfileRequest.class);
            String email = validateJwt(authorizationHeader, response);
            if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

            User user = userService.updateProfile(email, updateProfileRequest, files);

            if (user != null) {
                List<String> imageUrls = photoService.uploadPhotos(email, files);
                updateProfileWithImages(user, imageUrls); // Cập nhật avatar ở đây

                Map<String, Object> responseData = createResponseData(user);
                response.setStatus(HttpStatus.OK.value());
                response.setMessage("Profile updated successfully.");
                response.setData(responseData);
                return ResponseEntity.ok(response);
            } else {
                return createErrorResponseMap(response, HttpStatus.NOT_FOUND, "User not found.");
            }
        } catch (Exception e) {
            return createErrorResponseMap(response, HttpStatus.INTERNAL_SERVER_ERROR, "Error updating profile: " + e.getMessage());
        }
    }

    @PostMapping(value = "/update-profile", consumes = "multipart/form-data")
    public ResponseEntity<CommonResponse<Map<String, Object>>> updateProfile(
            @RequestPart("updateProfileRequest") String updateProfileRequestJson,
            @RequestPart("files") List<MultipartFile> files,
            @RequestHeader("Authorization") String authorizationHeader) {
        return handleUpdateProfile(updateProfileRequestJson, files, authorizationHeader);
    }

    @PutMapping(value = "/update-profile", consumes = "multipart/form-data")
    public ResponseEntity<CommonResponse<Map<String, Object>>> updateProfilePut(
            @RequestPart("updateProfileRequest") String updateProfileRequestJson,
            @RequestPart("files") List<MultipartFile> files,
            @RequestHeader("Authorization") String authorizationHeader) {
        CommonResponse<Map<String, Object>> response = new CommonResponse<>();

        try {
            // Parse the JSON string to UpdateProfileRequest object
            UpdateProfileRequest updateProfileRequest = new ObjectMapper().readValue(updateProfileRequestJson, UpdateProfileRequest.class);
            String email = validateJwt(authorizationHeader, response);
            if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

            User user = userService.findByEmail(email);
            if (user == null || user.getProfile() == null) {
                // Nếu người dùng chưa cập nhật hồ sơ lần đầu, trả về 404
                return createErrorResponseMap(response, HttpStatus.NOT_FOUND, "User has not updated profile yet. Please complete your profile first.");
            }

            // Tiến hành cập nhật hồ sơ
            user = userService.updateProfile(email, updateProfileRequest, files);

            if (user != null) {
                Map<String, Object> responseData = createResponseData(user);
                response.setStatus(HttpStatus.OK.value());
                response.setMessage("Profile updated successfully.");
                response.setData(responseData);
                return ResponseEntity.ok(response);
            } else {
                return createErrorResponseMap(response, HttpStatus.NOT_FOUND, "User not found.");
            }
        } catch (Exception e) {
            return createErrorResponseMap(response, HttpStatus.INTERNAL_SERVER_ERROR, "Error updating profile: " + e.getMessage());
        }
    }

//    @PostMapping("/update-avatar")
//    public ResponseEntity<CommonResponse<String>> updateAvatar(
//            @RequestPart("file") MultipartFile file,
//            @RequestHeader("Authorization") String authorizationHeader) {
//
//        CommonResponse<String> response = new CommonResponse<>();
//
//        try {
//            String email = validateJwtString(authorizationHeader, response);
//            if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//
//            List<String> imageUrls = photoService.uploadPhotos(email, List.of(file));
//            if (!imageUrls.isEmpty()) {
//                User user = userService.findByEmail(email);
//                userService.updateAvatar(user, imageUrls.get(0));
//                response.setStatus(HttpStatus.OK.value());
//                response.setMessage("Avatar updated successfully.");
//                return ResponseEntity.ok(response);
//            } else {
//                return createErrorResponseString(response, HttpStatus.BAD_REQUEST, "Failed to upload image.");
//            }
//        } catch (Exception e) {
//            return createErrorResponseString(response, HttpStatus.INTERNAL_SERVER_ERROR, "Error updating avatar: " + e.getMessage());
//        }
//    }

    @GetMapping("/location")
    public ResponseEntity getCurrentLocation(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        CommonResponse response = new CommonResponse<>();
        try {
            String email = validateJwtString(authorizationHeader, response);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Get location successfully!");
            response.setData(userLocationService.getCurrentLocation(latitude, longitude));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private ResponseEntity<CommonResponse<String>> createErrorResponseString(CommonResponse<String> response, HttpStatus status, String message) {
        response.setStatus(status.value());
        response.setMessage(message);
        return ResponseEntity.status(status).body(response);
    }

    private Map<String, Object> createResponseData(User user) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("profile", profileMapper.profileToProfileResponse(user.getProfile()));
        return responseData;
    }

    private ResponseEntity<CommonResponse<Map<String, Object>>> createErrorResponseMap(CommonResponse<Map<String, Object>> response, HttpStatus status, String message) {
        response.setStatus(status.value());
        response.setMessage(message);
        return ResponseEntity.status(status).body(response);
    }

    private String validateJwt(String authorizationHeader, CommonResponse<Map<String, Object>> response) {
        String jwt = authorizationHeader.substring(7);
        if (!jwtUtil.validateToken(jwt)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("Invalid or expired JWT token.");
            return null;
        }
        return jwtUtil.extractEmail(jwt);
    }

    private String validateJwtString(String authorizationHeader, CommonResponse<String> response) {
        String jwt = authorizationHeader.substring(7);
        if (!jwtUtil.validateToken(jwt)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("Invalid or expired JWT token.");
            return null;
        }
        return jwtUtil.extractEmail(jwt);
    }

    private boolean validateImages(List<MultipartFile> files, CommonResponse<Map<String, Object>> response) {
        if (files == null || files.size() < 1 || files.size() > 9) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("You must upload between 1 and 9 images.");
            return false;
        }
        return true;
    }

    private void updateProfileWithImages(User user, List<String> imageUrls) {
        if (!imageUrls.isEmpty()) {
            // Gán ảnh đầu tiên làm avatar
            String newAvatarUrl = imageUrls.get(0);
            user.getProfile().setAvatar(newAvatarUrl);

            // Lấy danh sách ảnh hiện có cho profile
            List<Photo> existingPhotos = photoService.getPhotos(user.getProfile().getProfileId());

            // Sử dụng Set để tránh lặp lại
            Set<String> existingPhotoUrls = new HashSet<>();
            for (Photo photo : existingPhotos) {
                existingPhotoUrls.add(photo.getUrl());
            }

            // Danh sách để lưu trữ ảnh mới không bao gồm avatar và không bị lặp
            List<Photo> newPhotos = new ArrayList<>();
            for (String imageUrl : imageUrls) {
                // Chỉ thêm ảnh nếu không phải là avatar và chưa tồn tại trong danh sách hiện có
                if (!imageUrl.equals(newAvatarUrl) && !existingPhotoUrls.contains(imageUrl)) {
                    newPhotos.add(new Photo(0, user.getProfile(), imageUrl, new Timestamp(System.currentTimeMillis())));
                }
            }

            // Thêm ảnh mới vào danh sách hiện có
            existingPhotos.addAll(newPhotos);
            user.getProfile().setPhotos(existingPhotos);
        }
        userService.save(user); // Lưu người dùng để lưu các thay đổi
    }

}
