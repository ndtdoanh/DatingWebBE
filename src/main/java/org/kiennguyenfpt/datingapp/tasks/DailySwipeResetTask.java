package org.kiennguyenfpt.datingapp.tasks; // Thay đổi package nếu cần

import org.kiennguyenfpt.datingapp.entities.User;
import org.kiennguyenfpt.datingapp.repositories.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DailySwipeResetTask {

    private final UserRepository userRepository;

    public DailySwipeResetTask(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Reset vào 0h mỗi ngày
    public void resetDailySwipeCount() {
        System.out.println("Resetting daily swipe count...");
        List<User> users = userRepository.findAll();
        users.forEach(user -> {
            user.setDailySwipeCount(0);
            user.setLastSwipeReset(LocalDate.now());
        });
        userRepository.saveAll(users);
    }
}
