package org.kiennguyenfpt.datingapp.services.impl;

import org.kiennguyenfpt.datingapp.entities.User;
import org.kiennguyenfpt.datingapp.repositories.UserRepository;
import org.kiennguyenfpt.datingapp.services.ReportService;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {
    private final UserRepository userRepository;

    public ReportServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void reportUser(Long userId, Long reportedUserId, String reason) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        User reportedUser = userRepository.findById(reportedUserId).orElseThrow(() -> new IllegalArgumentException("Reported user not found"));
        // Logic to handle reporting the user (e.g., save report to database, notify admin)
        System.out.println("User " + user.getEmail() + " reported user " + reportedUser.getEmail() + " for: " + reason);
    }

    @Override
    public void blockUser(Long userId, Long blockedUserId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        User blockedUser = userRepository.findById(blockedUserId).orElseThrow(() -> new IllegalArgumentException("Blocked user not found"));
        user.getBlockedUsers().add(blockedUser);
        userRepository.save(user);
    }
}
