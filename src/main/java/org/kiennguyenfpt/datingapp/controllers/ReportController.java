package org.kiennguyenfpt.datingapp.controllers;

import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.services.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/report")
@CrossOrigin

public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/user")
    public ResponseEntity<CommonResponse<String>> reportUser(@RequestParam Long userId, @RequestParam Long reportedUserId, @RequestParam String reason) {
        CommonResponse<String> response = new CommonResponse<>();
        try {
            reportService.reportUser(userId, reportedUserId, reason);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("User reported successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error reporting user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/block")
    public ResponseEntity<CommonResponse<String>> blockUser(@RequestParam Long userId, @RequestParam Long blockedUserId) {
        CommonResponse<String> response = new CommonResponse<>();
        try {
            reportService.blockUser(userId, blockedUserId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("User blocked successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error blocking user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
