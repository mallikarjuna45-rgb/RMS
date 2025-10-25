package com.rms.rms.controller;
import com.rms.rms.auth.UserPrinciple;
import com.rms.rms.model.Profile;
import com.rms.rms.model.User;
import com.rms.rms.service.ProfileService;
import com.rms.rms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/uploadResume")
    @PreAuthorize("hasRole('APPLICANT')")
    public ResponseEntity<String> uploadResume(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrinciple userPrinciple
    ) throws IOException {

        Profile profile = profileService.uploadResume(userPrinciple.getUserId(), file);
        return ResponseEntity.ok("Resume uploaded successfully: " + profile.getResumeFileAddress());
    }
}

