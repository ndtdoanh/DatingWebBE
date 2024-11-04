package org.kiennguyenfpt.datingapp.services.impl;

import org.kiennguyenfpt.datingapp.dtos.requests.CafeRequest;
import org.kiennguyenfpt.datingapp.dtos.responses.CafeResponse;
import org.kiennguyenfpt.datingapp.entities.Cafe;
import org.kiennguyenfpt.datingapp.repositories.CafeRepository;
import org.kiennguyenfpt.datingapp.services.CafeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CafeServiceImpl implements CafeService {
    private final CafeRepository cafeRepository;

    public CafeServiceImpl(CafeRepository cafeRepository) {
        this.cafeRepository = cafeRepository;
    }

    @Override
    public CafeResponse createCafe(CafeRequest cafeRequest) {
        if (cafeRepository.findByName(cafeRequest.getName()).isPresent() ||
                cafeRepository.findByAddress(cafeRequest.getAddress()).isPresent()) {
            throw new IllegalArgumentException("Cafe with the same name or address already exists");
        }
        Cafe cafe = new Cafe();
        cafe.setName(cafeRequest.getName());
        cafe.setAddress(cafeRequest.getAddress());
        cafe.setBio(cafeRequest.getBio());
        cafe.setPriceRangeMin(cafeRequest.getPriceRangeMin());
        cafe.setPriceRangeMax(cafeRequest.getPriceRangeMax());
        cafe.setImageUrl(cafeRequest.getImageUrl());
        cafe.setStatus(cafeRequest.getStatus() == null ? "ACTIVE" : cafeRequest.getStatus());
        Cafe savedCafe = cafeRepository.save(cafe);
        return new CafeResponse(
                savedCafe.getCafeId(),
                savedCafe.getName(),
                savedCafe.getAddress(),
                savedCafe.getBio(),
                savedCafe.getPriceRangeMin(),
                savedCafe.getPriceRangeMax(),
                savedCafe.getImageUrl(),
                savedCafe.getStatus()
        );
    }

    @Override
    public CafeResponse updateCafe(Long id, CafeRequest cafeRequest) {
        Optional<Cafe> existingCafe = cafeRepository.findById(id);
        if (existingCafe.isPresent()) {
            Cafe updatedCafe = existingCafe.get();
            updatedCafe.setName(cafeRequest.getName());
            updatedCafe.setAddress(cafeRequest.getAddress());
            updatedCafe.setBio(cafeRequest.getBio());
            updatedCafe.setPriceRangeMin(cafeRequest.getPriceRangeMin());
            updatedCafe.setPriceRangeMax(cafeRequest.getPriceRangeMax());
            updatedCafe.setImageUrl(cafeRequest.getImageUrl());
            updatedCafe.setStatus(cafeRequest.getStatus() == null ? "ACTIVE" : cafeRequest.getStatus());

            Cafe savedCafe = cafeRepository.save(updatedCafe);
            return new CafeResponse(
                    savedCafe.getCafeId(),
                    savedCafe.getName(),
                    savedCafe.getAddress(),
                    savedCafe.getBio(),
                    savedCafe.getPriceRangeMin(),
                    savedCafe.getPriceRangeMax(),
                    savedCafe.getImageUrl(),
                    savedCafe.getStatus()
            );
        } else {
            throw new RuntimeException("Cafe not found");
        }
    }

    @Override
    public int lockOrUnLockCafe(Long id) {
        return cafeRepository.lockOrUnLockCafe(id);
    }

    @Override
    public List<CafeResponse> getAllCafes() {
        return cafeRepository.findAll().stream().map(cafe ->
                new CafeResponse(
                        cafe.getCafeId(),
                        cafe.getName(),
                        cafe.getAddress(),
                        cafe.getBio(),
                        cafe.getPriceRangeMin(),
                        cafe.getPriceRangeMax(),
                        cafe.getImageUrl(),
                        cafe.getStatus()
                )).collect(Collectors.toList());
    }

    @Override
    public List<CafeResponse> getActiveCafes() {
        return cafeRepository.findAll().stream()
                .filter(cafe -> cafe.getStatus().equals("ACTIVE")) // Filter by ACTIVE status
                .map(cafe -> new CafeResponse(
                        cafe.getCafeId(),
                        cafe.getName(),
                        cafe.getAddress(),
                        cafe.getBio(),
                        cafe.getPriceRangeMin(),
                        cafe.getPriceRangeMax(),
                        cafe.getImageUrl(),
                        cafe.getStatus()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public CafeResponse getCafeById(Long id) {
        Cafe cafe = cafeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cafe not found"));
        return new CafeResponse(
                cafe.getCafeId(),
                cafe.getName(),
                cafe.getAddress(),
                cafe.getBio(),
                cafe.getPriceRangeMin(),
                cafe.getPriceRangeMax(),
                cafe.getImageUrl(),
                cafe.getStatus()
        );
    }

    @Override
    public List<CafeResponse> searchCafesByName(String name) {
        return cafeRepository.findByNameContainingIgnoreCase(name).stream()
                .map(cafe -> new CafeResponse(
                        cafe.getCafeId(),
                        cafe.getName(),
                        cafe.getAddress(),
                        cafe.getBio(),
                        cafe.getPriceRangeMin(),
                        cafe.getPriceRangeMax(),
                        cafe.getImageUrl(),
                        cafe.getStatus()
                ))
                .collect(Collectors.toList());
    }

}
