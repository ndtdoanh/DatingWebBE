package org.kiennguyenfpt.datingapp.dtos.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.kiennguyenfpt.datingapp.dtos.responses.ProfileResponse;
import org.kiennguyenfpt.datingapp.dtos.responses.SimpleProfileResponse;
import org.kiennguyenfpt.datingapp.entities.Photo;
import org.kiennguyenfpt.datingapp.entities.Profile;
import org.kiennguyenfpt.datingapp.entities.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-28T23:49:28+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)"
)
@Component
public class ProfileMapperImpl implements ProfileMapper {

    @Override
    public ProfileResponse profileToProfileResponse(Profile profile) {
        if ( profile == null ) {
            return null;
        }

        ProfileResponse profileResponse = new ProfileResponse();

        profileResponse.setUserId( profileUserUserId( profile ) );
        profileResponse.setAvatar( profile.getAvatar() );
        profileResponse.setPhone( profile.getPhone() );
        profileResponse.setProfileId( profile.getProfileId() );
        profileResponse.setName( profile.getName() );
        profileResponse.setAge( profile.getAge() );
        profileResponse.setGender( profile.getGender() );
        profileResponse.setBio( profile.getBio() );
        profileResponse.setCreatedAt( profile.getCreatedAt() );
        profileResponse.setUpdatedAt( profile.getUpdatedAt() );
        List<Photo> list = profile.getPhotos();
        if ( list != null ) {
            profileResponse.setPhotos( new ArrayList<Photo>( list ) );
        }

        return profileResponse;
    }

    @Override
    public SimpleProfileResponse profileToSimpleProfileResponse(Profile profile) {
        if ( profile == null ) {
            return null;
        }

        SimpleProfileResponse simpleProfileResponse = new SimpleProfileResponse();

        simpleProfileResponse.setProfileId( profile.getProfileId() );
        simpleProfileResponse.setUserId( profileUserUserId1( profile ) );
        simpleProfileResponse.setAvatar( profile.getAvatar() );
        simpleProfileResponse.setName( profile.getName() );
        simpleProfileResponse.setAge( profile.getAge() );
        simpleProfileResponse.setBio( profile.getBio() );
        simpleProfileResponse.setPhotos( mapPhotosToUrls( profile.getPhotos() ) );

        return simpleProfileResponse;
    }

    private long profileUserUserId(Profile profile) {
        if ( profile == null ) {
            return 0L;
        }
        User user = profile.getUser();
        if ( user == null ) {
            return 0L;
        }
        long userId = user.getUserId();
        return userId;
    }

    private Long profileUserUserId1(Profile profile) {
        if ( profile == null ) {
            return null;
        }
        User user = profile.getUser();
        if ( user == null ) {
            return null;
        }
        long userId = user.getUserId();
        return userId;
    }
}
