package org.kiennguyenfpt.datingapp.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CafeRequest {
    private String name;

    private String address;

    private String bio;

    private double priceRangeMin;

    private double priceRangeMax;

    private String imageUrl;

    private String status;

}
