package org.kiennguyenfpt.datingapp.services;

public interface ReportService {
    void reportUser(Long userId, Long reportedUserId, String reason);
    void blockUser(Long userId, Long blockedUserId);
}
