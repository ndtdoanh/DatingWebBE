package org.kiennguyenfpt.datingapp.services.impl;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.kiennguyenfpt.datingapp.services.FirebaseStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FirebaseStorageServiceImpl implements FirebaseStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseStorageServiceImpl.class);
    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    @Override
    public List<String> uploadImages(List<MultipartFile> files) throws IOException {
        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                logger.warn("File is empty: {}", file.getOriginalFilename());
                continue;
            }
            try {
                String fileName = file.getOriginalFilename();
                BlobInfo blobInfo = storage.create(
                        BlobInfo.newBuilder("your-bucket-name", fileName).build(),
                        file.getBytes()
                );
                urls.add(blobInfo.getMediaLink());
                logger.info("Successfully uploaded file: {}", fileName);
            } catch (IOException e) {
                logger.error("Failed to upload file: {}", file.getOriginalFilename(), e);
                throw e;
            }
        }
        return urls;
    }
}
