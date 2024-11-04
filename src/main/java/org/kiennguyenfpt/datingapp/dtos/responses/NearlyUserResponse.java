package org.kiennguyenfpt.datingapp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kiennguyenfpt.datingapp.enums.Gender;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NearlyUserResponse {
    private long userId;

    private String email;

    private String phone;

    private Double latitude;

    private Double longitude;

    private String address;

    private String name;

    private Integer age;

    private Gender gender;

    private String bio;

    private String avatar;

    private List<String> photoUrls;

}
