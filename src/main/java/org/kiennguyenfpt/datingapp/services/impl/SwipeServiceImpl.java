package org.kiennguyenfpt.datingapp.services.impl;

import jakarta.transaction.Transactional;
import org.kiennguyenfpt.datingapp.dtos.responses.ProfileResponse;
import org.kiennguyenfpt.datingapp.dtos.responses.SwipeResponse;
import org.kiennguyenfpt.datingapp.entities.Like;
import org.kiennguyenfpt.datingapp.entities.Swipe;
import org.kiennguyenfpt.datingapp.entities.User;
import org.kiennguyenfpt.datingapp.entities.UserSubscription;
import org.kiennguyenfpt.datingapp.entities.*;
import org.kiennguyenfpt.datingapp.enums.SubscriptionStatus;
import org.kiennguyenfpt.datingapp.exceptions.AccessDeniedException;
import org.kiennguyenfpt.datingapp.exceptions.AlreadyMatchedException;
import org.kiennguyenfpt.datingapp.repositories.LikeRepository;
import org.kiennguyenfpt.datingapp.repositories.SwipeRepository;
import org.kiennguyenfpt.datingapp.repositories.UserRepository;
import org.kiennguyenfpt.datingapp.repositories.UserSubscriptionRepository;
import org.kiennguyenfpt.datingapp.repositories.*;
import org.kiennguyenfpt.datingapp.services.MatchService;
import org.kiennguyenfpt.datingapp.services.SwipeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SwipeServiceImpl implements SwipeService {
    private final SwipeRepository swipeRepository;
    private final UserRepository userRepository;
    private final MatchService matchService;
    private final LikeRepository likeRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final ProfileRepository profileRepository;



    public SwipeServiceImpl(SwipeRepository swipeRepository, UserRepository userRepository, MatchService matchService, LikeRepository likeRepository, ProfileRepository profileRepository, UserSubscriptionRepository userSubscriptionRepository) {
        this.swipeRepository = swipeRepository;
        this.userRepository = userRepository;
        this.matchService = matchService;
        this.likeRepository = likeRepository;
        this.profileRepository = profileRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
    }

    @Override
    @Transactional
    public SwipeResponse swipe(Long userId, Long targetUserId, boolean isLike) {
        User user = userRepository.findById(userId).orElse(null);
        User targetUser = userRepository.findById(targetUserId).orElse(null);

        if (user == null || targetUser == null) {
            throw new IllegalArgumentException("User or Target User not found");
        }

        // Kiểm tra người dùng không thể like chính mình
        if (userId.equals(targetUserId)) {
            throw new IllegalArgumentException("You can't like yourself.");
        }

        // Kiểm tra xem targetUser có profile hay không
        if (targetUser.getProfile() == null) {
            throw new IllegalArgumentException("Target User does not have a profile.");
        }

        // Kiểm tra nếu hai người đã match trước đó
        boolean hasMatched = matchService.hasMatched(userId, targetUserId);
        if (hasMatched) {
            throw new AlreadyMatchedException("You have already matched with this user.");
        }

        // Lấy số lượt like tối đa cho gói của người dùng
        UserSubscription userSubscription = userSubscriptionRepository.findActiveSubscriptionByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User subscription not found"));
        SubscriptionPlan subscriptionPlan = userSubscription.getSubscriptionPlan();

        // Kiểm tra package của người dùng, giống API likedMe
        if (subscriptionPlan.getPlanId() == 1 && !Boolean.TRUE.equals(subscriptionPlan.getHasLikeLimit())) {
            throw new AccessDeniedException("Your subscription plan does not allow using the swipe feature.", HttpStatus.FORBIDDEN);
        }

        int dailySwipeMax = subscriptionPlan.getMaxDailySwipes() != null ? subscriptionPlan.getMaxDailySwipes() : Integer.MAX_VALUE;

        // Kiểm tra nếu đã vượt quá số lượt like tối đa cho gói
        if (user.getDailySwipeCount() >= dailySwipeMax) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "You have exceeded the maximum number of likes for today.");
        }

        // Logic to handle swipe action
        boolean isMatch = false;

        // Lưu hành động swipe (cả like và dislike) vào bảng Swipe
        Swipe swipe = new Swipe();
        swipe.setUser(user);
        swipe.setTargetUser(targetUser);
        swipe.setLike(isLike);
        swipeRepository.save(swipe);

        // Nếu là like, lưu vào bảng Like
        if (isLike) {
            user.setDailySwipeCount(user.getDailySwipeCount() + 1);
            userRepository.save(user);
            // Kiểm tra xem người dùng đã like targetUser trước đó chưa
            Like existingLike = likeRepository.findByUserAndProfile(user, targetUser.getProfile());
            if (existingLike == null) {
                // Nếu chưa like, lưu thông tin like vào bảng Like
                Like like = new Like();
                like.setUser(user);
                like.setProfile(targetUser.getProfile());
                likeRepository.save(like);
            }

            // Kiểm tra nếu target user đã like user trước đó (reciprocal like)
            Swipe reciprocalSwipe = swipeRepository.findByUser_UserIdAndTargetUser_UserId(targetUserId, userId);
            if (reciprocalSwipe != null && reciprocalSwipe.isLike()) {
                // Nếu reciprocal swipe là like, tạo match
                matchService.createMatch(user, targetUser);
                isMatch = true;
            }
        }
        return new SwipeResponse(isMatch);
    }


    @Override
    public List<ProfileResponse> getAllLikedProfilesExcludingCurrentUser(Long userId) throws AccessDeniedException {
        // Lấy hồ sơ của người dùng hiện tại dựa trên userId
        Profile currentUserProfile = profileRepository.findByUser_UserId(userId);
        if (currentUserProfile == null) {
            throw new IllegalArgumentException("User profile not found with userId " + userId);
        }

        // Kiểm tra package của người dùng hiện tại
        UserSubscription userSubscription = userSubscriptionRepository.findByUser_UserIdAndStatus(userId, SubscriptionStatus.ACTIVE);
        if (userSubscription != null && userSubscription.getSubscriptionPlan().getPlanId() == 1) {
            throw new AccessDeniedException("Users are not allowed to access the list of liked profiles!", null);
        }

        // Lấy danh sách các userId đã match với người dùng hiện tại
        List<Long> matchedUserIds = matchService.getMatchesForUser(userId)
                .stream()
                .map(match -> match.getUser2().getUserId())
                .collect(Collectors.toList());

        // Lấy tất cả các profile đã like, trừ những profile đã match
        return swipeRepository.findAllLikedProfilesExcludingCurrentUser(userId)
                .stream()
                .filter(profile -> !matchedUserIds.contains(profile.getUser().getUserId()))
                .map(ProfileResponse::new)
                .collect(Collectors.toList());
    }

}
