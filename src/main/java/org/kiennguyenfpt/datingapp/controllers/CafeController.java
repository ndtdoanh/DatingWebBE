package org.kiennguyenfpt.datingapp.controllers;

import org.kiennguyenfpt.datingapp.dtos.requests.CafeRequest;
import org.kiennguyenfpt.datingapp.dtos.responses.CafeResponse;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.services.CafeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cafes")
@CrossOrigin
public class CafeController {
    private final CafeService cafeService;

    public CafeController(CafeService cafeService) {
        this.cafeService = cafeService;
    }

    @PostMapping
    public ResponseEntity<CafeResponse> createCafe(@RequestBody CafeRequest cafeRequest) {
        return ResponseEntity.ok(cafeService.createCafe(cafeRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CafeResponse> updateCafe(@PathVariable Long id, @RequestBody CafeRequest cafeRequest) {
        return ResponseEntity.ok(cafeService.updateCafe(id, cafeRequest));
    }

    @PutMapping("/{id}/lockOrUnLock")
    public ResponseEntity lockOrUnLockCafe(@PathVariable Long id) {
        CommonResponse response = new CommonResponse<>();
        try {
            int result = cafeService.lockOrUnLockCafe(id);
            if (result == 1) {
                response.setStatus(HttpStatus.OK.value());
                response.setMessage("Lock Or UnLock successfully");
                return ResponseEntity.ok(response);
            } else {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.setMessage("Failed to update user status");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<CafeResponse>> getAllCafes() {
        return ResponseEntity.ok(cafeService.getActiveCafes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CafeResponse> getCafeById(@PathVariable Long id) {
        return ResponseEntity.ok(cafeService.getCafeById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<CommonResponse<List<CafeResponse>>> searchCafes(@RequestParam String name) {
        CommonResponse<List<CafeResponse>> response = new CommonResponse<>();
        List<CafeResponse> cafes = cafeService.searchCafesByName(name);

        if (cafes.isEmpty()) {
            // Nếu không tìm thấy quán, trả về 200 và thông điệp "Không tìm thấy"
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Không tìm thấy quán cà phê với tên: " + name);
            response.setData(cafes);
            return ResponseEntity.ok(response);
        }

        // Nếu tìm thấy, trả về danh sách quán
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Tìm kiếm thành công.");
        response.setData(cafes);
        return ResponseEntity.ok(response);
    }

}
