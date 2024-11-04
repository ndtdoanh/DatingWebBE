package org.kiennguyenfpt.datingapp.services;

import org.kiennguyenfpt.datingapp.entities.User;

import java.util.List;

public interface FriendService {
    void addFriend(Long userId, Long friendId);
    void removeFriend(Long userId, Long friendId);
    List<User> getFriends(Long userId);
}
