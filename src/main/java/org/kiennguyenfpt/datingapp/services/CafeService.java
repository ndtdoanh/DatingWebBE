package org.kiennguyenfpt.datingapp.services;

import org.kiennguyenfpt.datingapp.dtos.requests.CafeRequest;
import org.kiennguyenfpt.datingapp.dtos.responses.CafeResponse;

import java.util.List;

public interface CafeService {
    CafeResponse createCafe(CafeRequest cafeRequest); // Cập nhật phương thức

    CafeResponse updateCafe(Long id, CafeRequest cafeRequest); // Cập nhật phương thức

    int lockOrUnLockCafe(Long id); // Cập nhật phương thức để trả về String

    List<CafeResponse> getAllCafes(); // Cập nhật phương thức

    List<CafeResponse> getActiveCafes();

    CafeResponse getCafeById(Long id);

    List<CafeResponse> searchCafesByName(String name);// Cập nhật phương thức
}
