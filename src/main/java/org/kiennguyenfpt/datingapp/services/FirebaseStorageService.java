package org.kiennguyenfpt.datingapp.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FirebaseStorageService {
    List<String> uploadImages(List<MultipartFile> files) throws IOException;
}
