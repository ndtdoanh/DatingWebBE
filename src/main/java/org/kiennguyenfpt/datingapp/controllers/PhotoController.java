package org.kiennguyenfpt.datingapp.controllers;

import org.kiennguyenfpt.datingapp.entities.Photo;
import org.kiennguyenfpt.datingapp.repositories.UserRepository;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.services.PhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/photos")
@CrossOrigin
public class PhotoController {
    private final PhotoService photoService;
    private final UserRepository userRepository;

    public PhotoController(PhotoService photoService, UserRepository userRepository) {
        this.photoService = photoService;
        this.userRepository = userRepository;
    }

    @GetMapping("/list")
    public ResponseEntity<CommonResponse<List<Photo>>> getPhotos(@RequestParam Long userId) {
        CommonResponse<List<Photo>> response = new CommonResponse<>();
        try {
            List<Photo> photos = photoService.getPhotos(userId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Photos retrieved successfully.");
            response.setData(photos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error retrieving photos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<CommonResponse<String>> deletePhoto(@PathVariable Long photoId) {
        CommonResponse<String> response = new CommonResponse<>();
        try {
            // Lấy thông tin user từ JWT token (Authentication)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();  // Username từ JWT
            Long userId = userRepository.findByEmail(username).getUserId();  // Lấy userId từ repository qua username (email)

            // Kiểm tra xem ảnh có thuộc về người dùng hiện tại hay không
            Photo photo = photoService.findById(photoId);
            // Cần kiểm tra cả 3 điều kiện: photo không null, profile không null, và userId trùng khớp
            if (photo == null || photo.getProfile() == null || photo.getProfile().getUser().getUserId() != userId) {
                // Trả về thông báo nếu người dùng không có quyền xóa ảnh này
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setMessage("You do not have permission to delete this photo.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            boolean deleted = photoService.deletePhoto(photoId);
            if (deleted) {
                response.setStatus(HttpStatus.OK.value());
                response.setMessage("Photo deleted successfully.");
            } else {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setMessage("Photo not found.");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error deleting photo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
}
