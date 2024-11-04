package org.kiennguyenfpt.datingapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "UserAnalytics")
public class UserAnalytic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long analyticsId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int likesReceived;
    private int matchesMade;
    private int messagesSent;
}
