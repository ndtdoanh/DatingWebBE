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
@Table(name = "MatchAnalytics")
public class MatchAnalytic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long analyticsId;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    private double successRate;
}
