package org.kiennguyenfpt.datingapp.dtos.mapper;

import org.kiennguyenfpt.datingapp.dtos.responses.ProfileResponse;
import org.kiennguyenfpt.datingapp.dtos.responses.SimpleProfileResponse;
import org.kiennguyenfpt.datingapp.entities.Photo;
import org.kiennguyenfpt.datingapp.entities.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "phone", target = "phone")
    ProfileResponse profileToProfileResponse(Profile profile);


    @Mapping(source = "profileId", target = "profileId")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "age", target = "age")
    @Mapping(source = "bio", target = "bio")
    SimpleProfileResponse profileToSimpleProfileResponse(Profile profile);

    // solve mapper error
    default List<String> mapPhotosToUrls(List<Photo> photos) {
        if (photos == null) {
            return null;
        }
        return photos.stream()
                .map(Photo::getUrl)
                .collect(Collectors.toList());
    }
}
