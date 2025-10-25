package com.rms.rms.repository;

import com.rms.rms.model.Application;
import com.rms.rms.model.Job;
import com.rms.rms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByJob(Job job);
    List<Application> findByApplicant(User applicant);
    Optional<Application> findByJobAndApplicant(Job job, User applicant);

    Optional<Object> findByApplicantIdAndJobId(Long userId, Long jobId);
}
