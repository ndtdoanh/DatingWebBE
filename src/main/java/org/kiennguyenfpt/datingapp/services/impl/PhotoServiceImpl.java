package org.kiennguyenfpt.datingapp.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.kiennguyenfpt.datingapp.entities.Photo;
import org.kiennguyenfpt.datingapp.entities.User;
import org.kiennguyenfpt.datingapp.repositories.PhotoRepository;
import org.kiennguyenfpt.datingapp.repositories.UserRepository;
import org.kiennguyenfpt.datingapp.services.PhotoService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;

@Service
public class PhotoServiceImpl implements PhotoService {

    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;

    public PhotoServiceImpl(UserRepository userRepository, PhotoRepository photoRepository) {
        this.userRepository = userRepository;
        this.photoRepository = photoRepository;
    }

    public List<String> uploadPhotos(String email, List<MultipartFile> files) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        Bucket bucket = StorageClient.getInstance().bucket();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        for (MultipartFile file : files) {
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());
            String imageUrl = blob.getMediaLink();

            // Kiểm tra xem ảnh đã tồn tại trong DB chưa
            if (photoRepository.findByUrl(imageUrl) == null) {
                Photo photo = new Photo();
                photo.setUrl(imageUrl);
                photo.setProfile(user.getProfile()); // Đảm bảo trường profile được thiết lập
                photoRepository.save(photo);
                imageUrls.add(imageUrl);
            }
        }

        return imageUrls;
    }


    @Override
    public void savePhoto(Photo photo) {
        photoRepository.save(photo);
    }

    @Override
    public List<Photo> getPhotos(Long profileId) {
        return photoRepository.findByProfile_ProfileId(profileId);
    }

    @Override
    public boolean deletePhoto(Long photoId) {
        if (photoRepository.existsById(photoId)) {
            photoRepository.deleteById(photoId);
            return true;
        }
        return false;
    }
    @Override
    public Photo findById(Long photoId) {
        return photoRepository.findById(photoId).orElse(null);  // Trả về null nếu không tìm thấy ảnh
    }
}
