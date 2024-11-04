package org.kiennguyenfpt.datingapp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {

    private Long userId;

    private String name;

    private String email;

    private String address;

    private String status;

    private String roleName;

    private String bio;

    private String gender;

    private String phone;

    private String packageName;

    private List<String> photoUrls;

}
