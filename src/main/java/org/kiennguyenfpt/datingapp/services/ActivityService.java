package org.kiennguyenfpt.datingapp.services;

import org.kiennguyenfpt.datingapp.entities.Activity;

import java.util.List;

public interface ActivityService {
    void logActivity(Long userId, String action);
    //List<Activity> getUserActivities(Long userId);
}
