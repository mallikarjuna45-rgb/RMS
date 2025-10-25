package com.rms.rms.controller;

import com.rms.rms.auth.UserPrinciple;
import com.rms.rms.model.Application;
import com.rms.rms.model.Job;
import com.rms.rms.model.User;
import com.rms.rms.service.ApplicationService;
import com.rms.rms.service.JobService;
import com.rms.rms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/apply")
    @PreAuthorize("hasRole('APPLICANT')")
    public ResponseEntity<String> applyToJob(
            @RequestParam Long job_id,
            @AuthenticationPrincipal UserPrinciple userPrinciple
    ) {
        applicationService.applyToJob(userPrinciple.getUserId(), job_id);
        return ResponseEntity.ok("Applied successfully");
    }
}

