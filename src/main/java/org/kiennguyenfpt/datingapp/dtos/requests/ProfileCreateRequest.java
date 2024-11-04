package org.kiennguyenfpt.datingapp.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kiennguyenfpt.datingapp.enums.Gender;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileCreateRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Age is mandatory")
    @Min(value = 18, message = "Age should not be less than 18")
    @Max(value = 100, message = "Age should not be more than 100")
    private Integer age;

    @Size(max = 500, message = "Bio should not exceed 500 characters")
    private String bio;

    @NotNull(message = "Gender is mandatory")
    private Gender gender;

    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Phone number is invalid")
    private String phone;

    private MultipartFile avatar;
    private List<MultipartFile> photos;
}
