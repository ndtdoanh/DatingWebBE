package org.kiennguyenfpt.datingapp.services.impl;

import org.kiennguyenfpt.datingapp.dtos.requests.UpdateProfileRequest;
import org.kiennguyenfpt.datingapp.dtos.responses.AdminUserResponse;
import org.kiennguyenfpt.datingapp.dtos.responses.NearlyUserResponse;
import org.kiennguyenfpt.datingapp.entities.Payment;
import org.kiennguyenfpt.datingapp.entities.Photo;
import org.kiennguyenfpt.datingapp.entities.Profile;
import org.kiennguyenfpt.datingapp.entities.SubscriptionPlan;
import org.kiennguyenfpt.datingapp.entities.User;
import org.kiennguyenfpt.datingapp.entities.UserLocation;
import org.kiennguyenfpt.datingapp.entities.UserSubscription;
import org.kiennguyenfpt.datingapp.enums.SubscriptionPlanType;
import org.kiennguyenfpt.datingapp.enums.SubscriptionStatus;
import org.kiennguyenfpt.datingapp.repositories.PaymentRepository;
import org.kiennguyenfpt.datingapp.repositories.SubscriptionPlanRepository;
import org.kiennguyenfpt.datingapp.repositories.UserRepository;
import org.kiennguyenfpt.datingapp.repositories.UserSubscriptionRepository;
import org.kiennguyenfpt.datingapp.services.PhotoService;
import org.kiennguyenfpt.datingapp.services.SseService;
import org.kiennguyenfpt.datingapp.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final UserSubscriptionRepository userSubscriptionRepository;

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    private final PhotoService photoService;

    private final PaymentRepository paymentRepository;

    private final SseService sseService;

    public UserServiceImpl(
            final UserRepository userRepository,
            final PhotoService photoService,
            final UserSubscriptionRepository userSubscriptionRepository,
            final SubscriptionPlanRepository subscriptionPlanRepository,
            final PaymentRepository paymentRepository,
            final SseService sseService
    ) {
        this.userRepository = userRepository;
        this.photoService = photoService;
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.paymentRepository = paymentRepository;
        this.sseService = sseService;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> searchUsers(String keyword) {
        return userRepository.searchUsersByKeyword(keyword);
    }

    @Override
    public List<AdminUserResponse> searchAdminUsers() {
        return userRepository.searchAdminUsers();
    }

    @Override
    public int lockOrUnLockUser(Long id) {
        return userRepository.lockOrUnLockUser(id);
    }

    @Override
    public User findById(Long id) {
        return userRepository.getReferenceById(id);
    }

    @Override
    public List<NearlyUserResponse> findNearbyUsers(UserLocation currentLocation, double rangeInMeters) {
        return userRepository.findNearbyUsers(currentLocation.getLatitude(), currentLocation.getLongitude(), rangeInMeters);
    }

    @Override
    public AdminUserResponse getUserById(Long id) {
        return userRepository.getUserById(id);
    }

    @Override
    public void changeUserPackage(Long userId, Long planId) {
        // 1. Update tất cả các bản ghi cũ của userId về EXPIRED
        userSubscriptionRepository.updateStatusToExpiredByUserId(userId, "EXPIRED");

        // 2. Tạo bản ghi mới cho UserSubscription
        UserSubscription newSubscription = new UserSubscription();
        newSubscription.setUser(userRepository.getOne(userId));
        newSubscription.setStatus(SubscriptionStatus.ACTIVE);

        // 3. Lấy đối tượng subscription plan dựa trên planId và set vào UserSubscription
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid plan ID"));
        newSubscription.setSubscriptionPlan(subscriptionPlan);

        // 4. Set ngày bắt đầu là ngày hiện tại
        LocalDateTime startDate = LocalDateTime.now();
        newSubscription.setStartDate(startDate);

        // 5. Xác định end_date dựa trên planId
        if (SubscriptionPlanType.TRIAL.value() == planId) {
            newSubscription.setEndDate(startDate.plusDays(7));
        } else if (SubscriptionPlanType.PREMIUM.value() == planId) {
            newSubscription.setEndDate(startDate.plusDays(30));
        } else if (SubscriptionPlanType.FREE.value() == planId) {
            newSubscription.setEndDate(null); // Không có end_date nếu planId là 1
        }
        if (SubscriptionPlanType.FREE.value() != planId) {
            Payment payment = new Payment();
            payment.setUser(userRepository.getOne(userId));
            payment.setDate(startDate);

            Optional<SubscriptionPlan> optional = subscriptionPlanRepository.findById(planId);
            optional.ifPresent(plan -> payment.setAmount(plan.getPrice()));
            paymentRepository.save(payment);
        }
        // 6. Lưu bản ghi mới xuống cơ sở dữ liệu
        userSubscriptionRepository.save(newSubscription);
        // Gửi thông báo tới client qua WebSocket
        // 6. Lưu bản ghi mới xuống cơ sở dữ liệu
        userSubscriptionRepository.save(newSubscription);

        // Tạo thông điệp với userID và tên gói đăng ký
        String message = String.format("{\"userId\": \"%s\", \"package\": \"%s\"}", userId, subscriptionPlan.getName());

        // Gửi thông báo tới client qua WebSocket với userId và message
        sseService.sendNotification(message);
    }

    /*
    @Override
    public User updateProfile(String email, UpdateProfileRequest updateProfileRequest, List<MultipartFile> files) throws IOException {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            Profile profile = user.getProfile();
            if (profile == null) {
                profile = new Profile();
                profile.setUser(user);
                user.setProfile(profile);
            }

            // Require full update on second login
            if (user.getLoginCount() == 1) {
                if (updateProfileRequest.getName() == null || updateProfileRequest.getAge() == null
                        || updateProfileRequest.getBio() == null || updateProfileRequest.getGender() == null
                        || updateProfileRequest.getPhone() == null) {
                    throw new IllegalArgumentException("All fields must be provided for the second login update.");
                }
            }

            // Update profile fields
            profile.setName(updateProfileRequest.getName());
            profile.setAge(updateProfileRequest.getAge());
            profile.setBio(updateProfileRequest.getBio());
            profile.setGender(updateProfileRequest.getGender());
            profile.setAvatar(updateProfileRequest.getAvatar());
            profile.setPhone(updateProfileRequest.getPhone());

            // Save the profile first to avoid transient property exception
            userRepository.save(user);

            // Handle photo uploads
            List<String> imageUrls = photoService.uploadPhotos(email, files);
            if (!imageUrls.isEmpty()) {
                profile.setAvatar(imageUrls.get(0));
            }
            List<Photo> photos = photoService.getPhotos(profile.getProfileId());
            for (Photo photo : photos) {
                photo.setProfile(profile); // Ensure the profile field is set
                photoService.savePhoto(photo); // Save each photo to persist changes
            }

            // Update the existing photos list instead of replacing it
            profile.getPhotos().clear();
            profile.getPhotos().addAll(photos);

            // Save the user again to persist changes
            userRepository.save(user);
            return user;
        }
        return null;
    }

     */
    @Override
    public User updateProfile(String email, UpdateProfileRequest updateProfileRequest, List<MultipartFile> files) throws IOException {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            Profile profile = user.getProfile();
            if (profile == null) {
                profile = new Profile();
                profile.setUser(user);
                user.setProfile(profile);
            }

            // Update profile fields
            profile.setName(updateProfileRequest.getName());
            profile.setAge(updateProfileRequest.getAge());
            profile.setBio(updateProfileRequest.getBio());
            profile.setGender(updateProfileRequest.getGender());
            profile.setAvatar(updateProfileRequest.getAvatar());
            profile.setPhone(updateProfileRequest.getPhone());

            // Save the profile first to avoid transient property exception
            userRepository.save(user);

            // Handle photo uploads
            List<String> imageUrls = photoService.uploadPhotos(email, files);
            if (!imageUrls.isEmpty()) {
                profile.setAvatar(imageUrls.get(0));
            }
            List<Photo> photos = photoService.getPhotos(profile.getProfileId());
            for (Photo photo : photos) {
                photo.setProfile(profile); // Ensure the profile field is set
                photoService.savePhoto(photo); // Save each photo to persist changes
            }
            if (profile.getPhotos() == null) {
                profile.setPhotos(new ArrayList<>());
            }

            // Update the existing photos list instead of replacing it
            profile.getPhotos().clear();
            profile.getPhotos().addAll(photos);

            // Save the user again to persist changes
            userRepository.save(user);
            return user;
        }
        return null;
    }


    @Override
    public void updateAvatar(User user, String imageUrl) {
        user.getProfile().setAvatar(imageUrl);
        userRepository.save(user);
    }

}