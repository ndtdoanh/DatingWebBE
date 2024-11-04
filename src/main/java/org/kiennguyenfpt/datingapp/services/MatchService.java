package org.kiennguyenfpt.datingapp.services;

import org.kiennguyenfpt.datingapp.dtos.responses.AdminMatchResponse;
import org.kiennguyenfpt.datingapp.dtos.responses.MatchResponse;
import org.kiennguyenfpt.datingapp.entities.Match;
import org.kiennguyenfpt.datingapp.entities.User;

import java.util.List;

public interface MatchService {
    Match createMatch(User user1, User user2);

    List<Match> getMatchesForUser(Long userId);

    List<MatchResponse> getMatchResponsesForUser(Long userId);

    boolean hasMatched(Long userId, Long targetUserId); // Thêm phương thức kiểm tra match

    Long getReceiverIdFromMatch(Long matchId, Long senderId);

    Match getMatchById(Long matchId);

    List<AdminMatchResponse> getAllMatches();
}
