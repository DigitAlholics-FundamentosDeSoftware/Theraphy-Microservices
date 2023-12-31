package com.digitalholics.profileservice.Profile.service;


import com.digitalholics.profileservice.Profile.domain.model.entity.External.User;
import com.digitalholics.profileservice.Profile.domain.model.entity.Patient;
import com.digitalholics.profileservice.Profile.domain.model.entity.Physiotherapist;
import com.digitalholics.profileservice.Profile.domain.persistence.PatientRepository;
import com.digitalholics.profileservice.Profile.domain.persistence.PhysiotherapistRepository;
import com.digitalholics.profileservice.Profile.domain.persistence.External.UserRepository;
import com.digitalholics.profileservice.Profile.domain.service.PhysiotherapistService;
import com.digitalholics.profileservice.Profile.resource.Patient.CreatePatientResource;
import com.digitalholics.profileservice.Profile.resource.Physiotherapist.CreatePhysiotherapistResource;
import com.digitalholics.profileservice.Profile.resource.Physiotherapist.UpdatePhysiotherapistResource;
import com.digitalholics.profileservice.Shared.Exception.ResourceNotFoundException;
import com.digitalholics.profileservice.Shared.Exception.ResourceValidationException;


import com.digitalholics.profileservice.Shared.JwtValidation.JwtValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class PhysiotherapistServiceImpl implements PhysiotherapistService {
    private static final String ENTITY = "Physiotherapist";

    private final PhysiotherapistRepository physiotherapistRepository;
    private final PatientRepository patientRepository;

    private final JwtValidator jwtValidator;

    private final Validator validator;


    public PhysiotherapistServiceImpl(PhysiotherapistRepository physiotherapistRepository, PatientRepository patientRepository, JwtValidator jwtValidator, Validator validator) {
        this.physiotherapistRepository = physiotherapistRepository;
        this.patientRepository = patientRepository;
        this.jwtValidator = jwtValidator;
        this.validator = validator;
    }

    @Override
    public List<Physiotherapist> getAll() {

        return physiotherapistRepository.findAll();
    }

    @Override
    public Page<Physiotherapist> getAll(Pageable pageable) {
        return physiotherapistRepository.findAll(pageable);
    }

    @Override
    public Physiotherapist getById( Integer patientId) {
        return physiotherapistRepository.findById(patientId)
                .orElseThrow(()-> new ResourceNotFoundException(ENTITY, patientId));    }

    @Override
    public Physiotherapist getByUserId( Integer userId) {
        return physiotherapistRepository.findByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException(ENTITY, userId));    }

    @Override
    public Physiotherapist getLoggedInPhysiotherapist(String jwt) {

        User user = jwtValidator.validateJwtAndGetUser(jwt,"PHYSIOTHERAPIST");

        return physiotherapistRepository.findPhysiotherapistByUserUsername(user.getUsername());
    }

    @Override
    public Physiotherapist create(String jwt, CreatePhysiotherapistResource physiotherapistResource) {

        Set<ConstraintViolation<CreatePhysiotherapistResource>> violations = validator.validate(physiotherapistResource);

        if (!violations.isEmpty())
            throw new ResourceValidationException(ENTITY, violations);

        User user = jwtValidator.validateJwtAndGetUser(jwt, "PHYSIOTHERAPIST");

        Physiotherapist physiotherapistWithDni = physiotherapistRepository.findPhysiotherapistByDni(physiotherapistResource.getDni());
        Patient patientWithDni = patientRepository.findPatientByDni(physiotherapistResource.getDni());

        if(physiotherapistWithDni != null || patientWithDni != null)
            throw new ResourceValidationException(ENTITY,
                    "A physiotherapist with the same Dni first name already exists.");

        Physiotherapist physiotherapist = new Physiotherapist();
        physiotherapist.setUser(user);
        physiotherapist.setAge(physiotherapistResource.getAge());
        physiotherapist.setDni(physiotherapistResource.getDni());
        physiotherapist.setLocation(physiotherapistResource.getLocation());
        physiotherapist.setBirthdayDate(physiotherapistResource.getBirthdayDate());
        physiotherapist.setPhotoUrl(physiotherapistResource.getPhotoUrl());
        physiotherapist.setConsultationQuantity(0);
        physiotherapist.setSpecialization(physiotherapistResource.getSpecialization());
        physiotherapist.setYearsExperience(physiotherapistResource.getYearsExperience());
        physiotherapist.setRating(0.0);
        physiotherapist.setPatientQuantity(0);
        physiotherapist.setFees(physiotherapistResource.getFees());

        return physiotherapistRepository.save(physiotherapist);    }

    @Override
    public Physiotherapist update(String jwt, Integer physiotherapistId, UpdatePhysiotherapistResource request) {
        User user = jwtValidator.validateJwtAndGetUser(jwt, "PHYSIOTHERAPIST");

        Physiotherapist physiotherapist = getById(physiotherapistId);

        if(Objects.equals(user.getUsername(), physiotherapist.getUser().getUsername()) || Objects.equals(String.valueOf(user.getRole()), "ADMIN")){

        physiotherapist.setDni(request.getDni() != null ? request.getDni() : physiotherapist.getDni());
        physiotherapist.setAge(request.getAge() != null ? request.getAge() : physiotherapist.getAge());
        physiotherapist.setPhotoUrl(request.getPhotoUrl() != null ? request.getPhotoUrl() : physiotherapist.getPhotoUrl());
        physiotherapist.setPatientQuantity(request.getPatientQuantity() != null ? request.getPatientQuantity() : physiotherapist.getPatientQuantity());
        physiotherapist.setLocation(request.getLocation() != null ? request.getLocation() : physiotherapist.getLocation());
        physiotherapist.setBirthdayDate(request.getBirthdayDate() != null ? request.getBirthdayDate() : physiotherapist.getBirthdayDate());
        physiotherapist.setRating(request.getRating() != null ? request.getRating() : physiotherapist.getRating());
        physiotherapist.setSpecialization(request.getSpecialization() != null ? request.getSpecialization() : physiotherapist.getSpecialization());
        physiotherapist.setYearsExperience(request.getYearsExperience() != null ? request.getYearsExperience() : physiotherapist.getYearsExperience());
        physiotherapist.setConsultationQuantity(request.getConsultationQuantity() != null ? request.getConsultationQuantity() : physiotherapist.getConsultationQuantity());
        physiotherapist.setFees(request.getFees() != null ? request.getFees() : physiotherapist.getFees());

        return physiotherapistRepository.save(physiotherapist);
        }
        throw new ResourceValidationException("JWT",
                "Invalid access.");
    }

    @Override
    public ResponseEntity<?> delete(String jwt, Integer physiotherapistId) {
        User user = jwtValidator.validateJwtAndGetUser(jwt, "PHYSIOTHERAPIST");

        return physiotherapistRepository.findById(physiotherapistId).map(physiotherapist -> {
            if(Objects.equals(user.getUsername(), physiotherapist.getUser().getUsername()) || Objects.equals(String.valueOf(user.getRole()), "ADMIN")){

                physiotherapistRepository.delete(physiotherapist);
                return ResponseEntity.ok().build();
            }
            throw new ResourceValidationException("JWT",
                    "Invalid access.");
        }).orElseThrow(()-> new ResourceNotFoundException(ENTITY,physiotherapistId));    }

    public User validateJwtAndGetUser(String jwt) {
        return jwtValidator.validateJwtAndGetUser(jwt, "PHYSIOTHERAPIST");
    }
}
