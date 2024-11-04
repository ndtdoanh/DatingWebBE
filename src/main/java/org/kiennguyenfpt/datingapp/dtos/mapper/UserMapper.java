package org.kiennguyenfpt.datingapp.dtos.mapper;

import org.kiennguyenfpt.datingapp.dtos.responses.UserResponse;
import org.kiennguyenfpt.datingapp.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse userToUserResponse(User user);

}
