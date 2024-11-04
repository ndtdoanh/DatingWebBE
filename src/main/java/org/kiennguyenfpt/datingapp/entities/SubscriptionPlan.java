package org.kiennguyenfpt.datingapp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SubscriptionPlans")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long planId;

    @Column(nullable = false)
    private String name;

    @Column
    private Integer duration; // Duration in days

    @Column(name = "description")
    private String description;

    @Column
    private double price;

    @Column
    private Boolean hasLikeLimit;

    @Column
    private Boolean hasWatchLike;

    @Column
    private Boolean hasShowPriority;

    @Column
    private Boolean hasViewProfile;

    @Column
    private Integer maxDailySwipes= 0;

    @Column
    private String imageUrl; // Chưa cần thiết

}
