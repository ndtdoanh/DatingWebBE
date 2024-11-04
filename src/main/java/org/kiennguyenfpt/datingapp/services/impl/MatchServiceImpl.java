package org.kiennguyenfpt.datingapp.services.impl;

import org.kiennguyenfpt.datingapp.dtos.responses.AdminMatchResponse;
import org.kiennguyenfpt.datingapp.dtos.responses.MatchResponse;
import org.kiennguyenfpt.datingapp.entities.Match;
import org.kiennguyenfpt.datingapp.entities.User;
import org.kiennguyenfpt.datingapp.repositories.MatchRepository;
import org.kiennguyenfpt.datingapp.services.MatchService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchServiceImpl implements MatchService {
    private final MatchRepository matchRepository;

    public MatchServiceImpl(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Override
    public Match createMatch(User user1, User user2) {
        // Khởi tạo đối tượng Match mới
        Match match = new Match();
        match.setUser1(user1);
        match.setUser2(user2);

        // Lưu đối tượng Match vào database
        return matchRepository.save(match);
    }

    @Override
    public List<Match> getMatchesForUser(Long userId) {
        return matchRepository.findByUser1_UserIdOrUser2_UserId(userId, userId);
    }

    @Override
    public boolean hasMatched(Long userId, Long targetUserId) {
        return matchRepository.existsByUser1_UserIdAndUser2_UserId(userId, targetUserId)
                || matchRepository.existsByUser1_UserIdAndUser2_UserId(targetUserId, userId);
    }

    public List<MatchResponse> getMatchResponsesForUser(Long userId) {
        List<Match> matches = getMatchesForUser(userId);

        return matches.stream().map(match -> {
            User targetUser = (match.getUser1().getUserId() == userId) ? match.getUser2() : match.getUser1();
            String targetUserName = targetUser.getProfile().getName(); // Lấy tên từ Profile
            String targetUserAvatar = targetUser.getProfile().getAvatar() != null ? targetUser.getProfile().getAvatar() : ""; // Lấy avatar từ Profile
            return new MatchResponse(
                    match.getMatchId(),
                    userId,
                    targetUser.getUserId(),
                    targetUserName,
                    targetUserAvatar
            );
        }).collect(Collectors.toList());
    }

    @Override
    public Long getReceiverIdFromMatch(Long matchId, Long senderId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found")); // Kiểm tra match hợp lệ

        if (match.getUser1().getUserId() == senderId) {
            return match.getUser2().getUserId(); // Trả về receiverId nếu sender là user1
        } else if (match.getUser2().getUserId() == senderId) {
            return match.getUser1().getUserId(); // Trả về receiverId nếu sender là user2
        } else {
            throw new IllegalArgumentException("Sender is not part of the match."); // Nếu sender không thuộc về match
        }
    }

    @Override
    public Match getMatchById(Long matchId) {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + matchId));
    }

    @Override
    public List<AdminMatchResponse> getAllMatches() {
        return matchRepository.getAllMatchesWithUserDetails();
    }

}
