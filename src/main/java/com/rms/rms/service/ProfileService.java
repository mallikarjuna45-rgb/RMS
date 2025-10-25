package com.rms.rms.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rms.rms.model.Profile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rms.rms.model.User;
import com.rms.rms.repository.ProfileRepository;
import com.rms.rms.repository.UserRepository;
import com.rms.rms.utils.ResumeApiRateLimitException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String UPLOAD_DIR = "C:/RMS/uploads/resumes/";
    private final String API_URL = "https://api.apilayer.com/resume_parser/upload";
    private final String API_KEY = "0bWeisRWoLj3UdXt3MXMSMWptYFIpQfS";

    public Profile uploadResume(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            profile.setApplicant(user);
        }

        // Ensure upload directory exists
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();

        // Save the file locally
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) throw new RuntimeException("Invalid file name");

        String sanitizedFileName = System.currentTimeMillis() + "_" +
                originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        File destFile = new File(dir, sanitizedFileName);
        file.transferTo(destFile);

        profile.setResumeFileAddress("resumes/" + sanitizedFileName);

        // Call 3rd-party API to extract resume details
        extractResumeDetails(destFile, profile);

        return profileRepository.save(profile);
    }

    private void extractResumeDetails(File resumeFile, Profile profile) throws IOException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set("apikey", API_KEY);

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(
                    java.nio.file.Files.readAllBytes(resumeFile.toPath()), headers
            );

            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL, HttpMethod.POST, requestEntity, String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                // Extract education
                if (jsonNode.has("education")) {
                    List<java.util.Map<String, Object>> educationList = objectMapper.convertValue(
                            jsonNode.get("education"),
                            new TypeReference<List<java.util.Map<String, Object>>>() {}
                    );
                    profile.setEducation(
                            educationList.stream()
                                    .map(obj -> obj.get("name").toString())
                                    .collect(Collectors.joining(", "))
                    );
                }

                // Extract experience
                if (jsonNode.has("experience")) {
                    List<java.util.Map<String, Object>> experienceList = objectMapper.convertValue(
                            jsonNode.get("experience"),
                            new TypeReference<List<java.util.Map<String, Object>>>() {}
                    );
                    profile.setExperience(
                            experienceList.stream()
                                    .map(obj -> obj.get("name").toString())
                                    .collect(Collectors.joining(", "))
                    );
                }

                // Extract skills
                if (jsonNode.has("skills")) {
                    List<String> skillsList = objectMapper.convertValue(
                            jsonNode.get("skills"),
                            new TypeReference<List<String>>() {}
                    );
                    profile.setSkills(String.join(", ", skillsList));
                }

                // Extract phone
                profile.setPhone(jsonNode.has("phone") ? jsonNode.get("phone").asText() : null);
            }
        } catch (Exception e) {
            // For **any exception**, throw your custom exception
            throw new ResumeApiRateLimitException();
        }
    }


    public Profile getProfileByUserId(Long userId) {
        return profileRepository.findByApplicantId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }
}


