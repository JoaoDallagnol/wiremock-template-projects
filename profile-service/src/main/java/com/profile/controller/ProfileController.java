package com.profile.controller;

import com.profile.dto.ProfileDTO;
import com.profile.dto.UpdateAvatarDTO;
import com.profile.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/profiles")
public class ProfileController {
    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileDTO> getProfile(@PathVariable String userId) {
        ProfileDTO profile = service.getProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }

    @PostMapping
    public ResponseEntity<ProfileDTO> createProfile(@RequestBody ProfileDTO dto) {
        ProfileDTO created = service.createProfile(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{userId}")
                .buildAndExpand(created.getUserId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{userId}/avatar")
    public ResponseEntity<ProfileDTO> updateAvatar(@PathVariable String userId, @RequestBody UpdateAvatarDTO dto) {
        ProfileDTO updated = service.updateAvatar(userId, dto.getAvatar());
        return ResponseEntity.ok(updated);
    }
}
