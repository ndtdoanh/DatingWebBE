package org.kiennguyenfpt.datingapp.dtos.requests;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SwipeRequest {
//    @NotNull(message = "User ID cannot be null")
//    private Long userId;

    @NotNull(message = "Target User ID cannot be null")
    private Long targetUserId;

    @NotNull(message = "Like status cannot be null")
    private Boolean isLike;

}
