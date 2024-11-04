package org.kiennguyenfpt.datingapp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminMatchResponse {
    private Long matchId;

    private Long user1Id;

    private Long user2Id;

    private String user1Email;
    private String user2Email;
    private String user1Name;
    private String user2Name;

    private String user1Location;

    private String user2Location;

    private LocalDateTime createdAt;

}
