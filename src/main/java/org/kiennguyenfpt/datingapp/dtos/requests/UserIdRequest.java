package org.kiennguyenfpt.datingapp.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserIdRequest {
    @NotNull(message = "User ID must be provided.")
    private Long userId;
}
