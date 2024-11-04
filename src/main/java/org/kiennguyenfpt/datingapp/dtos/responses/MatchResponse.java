package org.kiennguyenfpt.datingapp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchResponse {
    private Long matchId; // ID của match
    private Long userId;  // ID của user
    private Long targetUserId; // ID của target user
    private String targetUserName;   // Thêm tên của target user
    private String targetUserAvatar; // Thêm avatar của target user
}


