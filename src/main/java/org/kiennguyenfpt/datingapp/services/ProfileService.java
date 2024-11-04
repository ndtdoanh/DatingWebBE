package org.kiennguyenfpt.datingapp.services;

import org.kiennguyenfpt.datingapp.entities.Profile;
import java.util.List;

public interface ProfileService {
    List<Profile> getAllProfiles();
    Profile getProfileByEmail(String email);
    Profile getProfileByUserId(Long userId);
    List<Profile> getAllProfilesExcludingCurrentUserAndSwiped(String email);
}
