package org.kiennguyenfpt.datingapp.controllers;

import org.kiennguyenfpt.datingapp.entities.User;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.services.FriendService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/friend")
@CrossOrigin
public class FriendController {
    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PostMapping("/add")
    public ResponseEntity<CommonResponse<String>> addFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        CommonResponse<String> response = new CommonResponse<>();
        try {
            friendService.addFriend(userId, friendId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Friend added successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error adding friend: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<CommonResponse<String>> removeFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        CommonResponse<String> response = new CommonResponse<>();
        try {
            friendService.removeFriend(userId, friendId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Friend removed successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error removing friend: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<CommonResponse<List<User>>> getFriends(@RequestParam Long userId) {
        CommonResponse<List<User>> response = new CommonResponse<>();
        try {
            List<User> friends = friendService.getFriends(userId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Friends retrieved successfully.");
            response.setData(friends);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error retrieving friends: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
