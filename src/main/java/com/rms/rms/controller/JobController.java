package com.rms.rms.controller;

import com.rms.rms.auth.UserPrinciple;
import com.rms.rms.model.Job;
import com.rms.rms.model.User;
import com.rms.rms.service.JobService;
import com.rms.rms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping
public class JobController {

    private final JobService jobService;

    // Admin creates a job
    @PostMapping("/admin/job")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Job> createJob(
            @RequestBody Job job,
            @AuthenticationPrincipal UserPrinciple userPrinciple
    ) {
        Job savedJob = jobService.createJob(userPrinciple.getUserId(), job);
        return ResponseEntity.ok(savedJob);
    }

    // Get all jobs (all users)
    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> getJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }
}
