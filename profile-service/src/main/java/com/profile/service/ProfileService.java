package com.profile.service;

import com.profile.dto.ProfileDTO;
import com.profile.entity.ProfileEntity;
import com.profile.exception.ProfileNotFoundException;
import com.profile.repository.ProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final ProfileRepository repository;

    public ProfileService(ProfileRepository repository) {
        this.repository = repository;
    }

    public ProfileDTO getProfileByUserId(String userId) {
        ProfileEntity entity = repository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found for userId: " + userId));
        return toDTO(entity);
    }

    public ProfileDTO createProfile(ProfileDTO dto) {
        if (repository.existsByUserId(dto.getUserId())) {
            throw new IllegalArgumentException("Profile already exists for userId: " + dto.getUserId());
        }
        ProfileEntity entity = toEntity(dto);
        ProfileEntity saved = repository.save(entity);
        return toDTO(saved);
    }

    public ProfileDTO updateAvatar(String userId, String avatar) {
        ProfileEntity entity = repository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found for userId: " + userId));
        entity.setAvatar(avatar);
        ProfileEntity updated = repository.save(entity);
        return toDTO(updated);
    }

    private ProfileDTO toDTO(ProfileEntity entity) {
        return new ProfileDTO(entity.getUserId(), entity.getAvatar(), entity.getTheme(), entity.getLanguage());
    }

    private ProfileEntity toEntity(ProfileDTO dto) {
        ProfileEntity entity = new ProfileEntity();
        entity.setUserId(dto.getUserId());
        entity.setAvatar(dto.getAvatar());
        entity.setTheme(dto.getTheme());
        entity.setLanguage(dto.getLanguage());
        return entity;
    }
}
