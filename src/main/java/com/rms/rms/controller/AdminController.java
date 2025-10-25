package com.rms.rms.controller;

import com.rms.rms.model.Application;
import com.rms.rms.model.Job;
import com.rms.rms.model.Profile;
import com.rms.rms.model.User;
import com.rms.rms.service.ApplicationService;
import com.rms.rms.service.JobService;
import com.rms.rms.service.ProfileService;
import com.rms.rms.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final ProfileService profileService;

    // List all users
    @GetMapping("/applicants")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Get applicant by ID
    @GetMapping("/applicant/{applicant_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Profile> getApplicant(@PathVariable Long applicant_id) {
        Profile profile = profileService.getProfileByUserId(applicant_id);
        return ResponseEntity.ok(profile);
    }

    // Get job details + applicants
    @GetMapping("/job/{job_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JobDetailsResponse> getJobDetails(@PathVariable Long job_id) {
        Job job = jobService.getJobById(job_id);
        List<Application> applications = applicationService.getApplicationsByJob(job);
        return ResponseEntity.ok(new JobDetailsResponse(job, applications));
    }

    @Data
    @AllArgsConstructor
    static class JobDetailsResponse {
        private Job job;
        private List<Application> applications;
    }
}

