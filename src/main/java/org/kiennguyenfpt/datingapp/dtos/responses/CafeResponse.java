package org.kiennguyenfpt.datingapp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CafeResponse {
    private long cafeId;

    private String name;

    private String address;

    private String bio;

    private double priceRangeMin;

    private double priceRangeMax;

    private String imageUrl;

    private String status;
}
