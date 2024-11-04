package org.kiennguyenfpt.datingapp.dtos.mapper;

import javax.annotation.processing.Generated;
import org.kiennguyenfpt.datingapp.dtos.responses.UserResponse;
import org.kiennguyenfpt.datingapp.entities.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-22T14:56:14+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse userToUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setUserId( user.getUserId() );
        userResponse.setEmail( user.getEmail() );
        userResponse.setCreatedAt( user.getCreatedAt() );
        userResponse.setFirstLogin( user.isFirstLogin() );
        userResponse.setLastLogin( user.getLastLogin() );
        if ( user.getStatus() != null ) {
            userResponse.setStatus( user.getStatus().name() );
        }

        return userResponse;
    }
}
