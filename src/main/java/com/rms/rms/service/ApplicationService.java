package com.rms.rms.service;

import com.rms.rms.model.Application;
import com.rms.rms.model.Job;
import com.rms.rms.model.User;
import com.rms.rms.repository.ApplicationRepository;
import com.rms.rms.repository.JobRepository;
import com.rms.rms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public Application applyToJob(Long userId, Long jobId) {
        User applicant = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Optional: prevent duplicate applications
        applicationRepository.findByApplicantIdAndJobId(userId, jobId)
                .ifPresent(a -> { throw new RuntimeException("Already applied"); });

        Application application = new Application();
        application.setApplicant(applicant);
        application.setJob(job);

        // Increment total applications
        job.setTotalApplications(job.getTotalApplications() + 1);
        jobRepository.save(job);

        return applicationRepository.save(application);
    }

    public List<Application> getApplicationsByJob(Job job) {
        return applicationRepository.findByJob(job);
    }
}
