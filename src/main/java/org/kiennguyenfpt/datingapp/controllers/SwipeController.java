package org.kiennguyenfpt.datingapp.controllers;

import org.kiennguyenfpt.datingapp.dtos.requests.SwipeRequest;
import org.kiennguyenfpt.datingapp.dtos.responses.ProfileResponse;
import org.kiennguyenfpt.datingapp.dtos.responses.SwipeResponse;
import org.kiennguyenfpt.datingapp.entities.Profile;
import org.kiennguyenfpt.datingapp.exceptions.AccessDeniedException;
import org.kiennguyenfpt.datingapp.exceptions.AlreadyMatchedException;
import org.kiennguyenfpt.datingapp.repositories.UserRepository;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.services.SwipeService;
import org.kiennguyenfpt.datingapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/swipes")
@CrossOrigin
public class SwipeController {
    private final UserService userService;
    private final SwipeService swipeService;
    private final UserRepository userRepository;

    @Autowired
    public SwipeController(UserService userService, SwipeService swipeService, UserRepository userRepository) {
        this.userService = userService;
        this.swipeService = swipeService;
        this.userRepository = userRepository;
    }

    @PostMapping("/swipe")
    public ResponseEntity<CommonResponse<SwipeResponse>> swipe(@RequestBody @Valid SwipeRequest swipeRequest) {
        CommonResponse<SwipeResponse> response = new CommonResponse<>();
        try {
            if (swipeRequest.getIsLike() == null) {
                throw new IllegalArgumentException("Like status cannot be null");
            }

            // Lấy thông tin user từ JWT token (Authentication)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();  // Username từ JWT
            Long userId = userRepository.findByEmail(username).getUserId();  // Lấy userId từ repository qua username (email)

            SwipeResponse swipeResponse = swipeService.swipe(userId, swipeRequest.getTargetUserId(), swipeRequest.getIsLike());

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Swipe action completed successfully.");
            response.setData(swipeResponse);
            return ResponseEntity.ok(response);

        } catch (ResponseStatusException e) {
            // Kiểm tra nếu lỗi là TOO_MANY_REQUESTS (429)
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setMessage("You have exceeded the maximum number of likes for today.");
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
            }
            // Xử lý các ResponseStatusException khác (nếu có)
            response.setStatus(e.getStatusCode().value());
            response.setMessage(e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(response);

        } catch (AlreadyMatchedException e) { // Bắt ngoại lệ AlreadyMatchedException riêng
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error performing swipe action: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        CommonResponse<String> response = new CommonResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AlreadyMatchedException.class)
    public ResponseEntity<CommonResponse<String>> handleAlreadyMatchedException(AlreadyMatchedException e) {
        CommonResponse<String> response = new CommonResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<String>> handleAccessDeniedException(AccessDeniedException e) {
        CommonResponse<String> response = new CommonResponse<>();
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }


    @GetMapping("/likedMe")
    public ResponseEntity<CommonResponse<List<ProfileResponse>>> getAllLikedProfilesExcludingCurrentUser(Authentication authentication) {
        CommonResponse<List<ProfileResponse>> response = new CommonResponse<>();
        try {
            // Lấy email từ Authentication
            String email = authentication.getName();

            // Tìm user dựa vào email từ JWT
            Long userId = userService.findByEmail(email).getUserId();

            // Lấy danh sách các profile đã thích ngoại trừ user hiện tại
            List<ProfileResponse> profiles = swipeService.getAllLikedProfilesExcludingCurrentUser(userId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Get list of liked successfully!");
            response.setData(profiles);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
