package org.kiennguyenfpt.datingapp.controllers;

import org.kiennguyenfpt.datingapp.dtos.responses.MatchResponse;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.services.MatchService;
import org.kiennguyenfpt.datingapp.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
@CrossOrigin
public class MatchController {
    private final MatchService matchService;
    private final UserService userService;

    public MatchController(final MatchService matchService, UserService userService) {
        this.matchService = matchService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<MatchResponse>>> getMatches(Authentication authentication) {
        CommonResponse<List<MatchResponse>> response = new CommonResponse<>();
        try {
            // Lấy email từ Authentication
            String email = authentication.getName();

            // Tìm user dựa vào email từ JWT
            Long userId = userService.findByEmail(email).getUserId();

            // Lấy danh sách các match cho người dùng
            List<MatchResponse> matchResponses = matchService.getMatchResponsesForUser(userId);

            // Thiết lập các thuộc tính phản hồi
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Matches retrieved successfully.");
            response.setData(matchResponses);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error retrieving matches: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
