package com.digitalholics.healthexpertiseservice.Shared.JwtValidation;

import com.digitalholics.healthexpertiseservice.HealthExpertise.domain.model.entity.External.User;
import com.digitalholics.healthexpertiseservice.HealthExpertise.domain.persistence.External.UserRepository;
import com.digitalholics.healthexpertiseservice.Shared.Exception.ResourceValidationException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;

@Component
public class JwtValidator {
    private final RestTemplate restTemplate;

    private final UserRepository userRepository;

    public JwtValidator(RestTemplate restTemplate, UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
    }

    public User validateJwtAndGetUser(String jwt, String admittedRole) {
        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7); // Quita los primeros 7 caracteres ("Bearer ")
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwt);

        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        String validationEndpointUrl = "http://security-service/api/v1/security/auth/validate-jwt";
        ResponseEntity<String> responseEntity = restTemplate.exchange(validationEndpointUrl, HttpMethod.GET, requestEntity, String.class);

        Optional<User> userOptional = userRepository.findByUsername(responseEntity.getBody());

        User user = userOptional.orElseThrow(() -> new NotFoundException("User not found for username: " + responseEntity.getBody()));

        if (Objects.equals(String.valueOf(user.getRole()), admittedRole) || Objects.equals(String.valueOf(user.getRole()), "ADMIN")) {
            return user;
        } else {
            throw new ResourceValidationException("JWT", "Invalid rol.");
        }
    }

    public User validateJwtAndGetUserNoRol(String jwt) {
        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7); // Quita los primeros 7 caracteres ("Bearer ")
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwt);

        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        String validationEndpointUrl = "http://security-service/api/v1/security/auth/validate-jwt";
        ResponseEntity<String> responseEntity = restTemplate.exchange(validationEndpointUrl, HttpMethod.GET, requestEntity, String.class);

        Optional<User> userOptional = userRepository.findByUsername(responseEntity.getBody());

        return userOptional.orElseThrow(() -> new NotFoundException("User not found for username: " + responseEntity.getBody()));
    }



}