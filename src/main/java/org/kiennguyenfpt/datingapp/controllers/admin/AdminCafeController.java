package org.kiennguyenfpt.datingapp.controllers.admin;

import org.kiennguyenfpt.datingapp.dtos.requests.CafeRequest;
import org.kiennguyenfpt.datingapp.dtos.responses.CafeResponse;
import org.kiennguyenfpt.datingapp.responses.CommonResponse;
import org.kiennguyenfpt.datingapp.services.CafeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/cafes")
@CrossOrigin
@PreAuthorize("hasRole('Admin')")
public class AdminCafeController {
    private final CafeService cafeService;

    public AdminCafeController(CafeService cafeService) {
        this.cafeService = cafeService;
    }

    @GetMapping
    public ResponseEntity getAllCafes() {
        CommonResponse response = new CommonResponse<>();
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Get list cafe successfully!");
            response.setData(cafeService.getAllCafes());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity createCafe(@RequestBody CafeRequest cafeRequest) {
        CommonResponse response = new CommonResponse<>();
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Cafe created successfully!");
            response.setData(cafeService.createCafe(cafeRequest));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateCafe(@PathVariable Long id, @RequestBody CafeRequest cafeRequest) {
        CommonResponse response = new CommonResponse<>();
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Cafe created successfully!");
            response.setData(cafeService.updateCafe(id, cafeRequest));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CafeResponse> getCafeById(@PathVariable Long id) {
        return ResponseEntity.ok(cafeService.getCafeById(id));
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

}
