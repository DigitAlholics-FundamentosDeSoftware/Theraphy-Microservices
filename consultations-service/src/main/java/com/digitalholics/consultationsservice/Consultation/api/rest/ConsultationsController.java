package com.digitalholics.consultationsservice.Consultation.api.rest;

import com.digitalholics.consultationsservice.Consultation.domain.service.ConsultationService;
import com.digitalholics.consultationsservice.Consultation.mapping.ConsultationMapper;
import com.digitalholics.consultationsservice.Consultation.resource.ConsultationResource;
import com.digitalholics.consultationsservice.Consultation.resource.CreateConsultationResource;
import com.digitalholics.consultationsservice.Consultation.resource.UpdateConsultationResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/consultations", produces = "application/json")
@Tag(name = "Consultations", description = "Consultations operations: listing, retrieval, creation, update, and deletion")
public class ConsultationsController {

    private final ConsultationService consultationService;
    private final ConsultationMapper mapper;

    public ConsultationsController(ConsultationService consultationService, ConsultationMapper mapper) {
        this.consultationService = consultationService;
        this.mapper = mapper;
    }

    @Operation(summary = "Get all consultations", description = "Returns consultations' list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully got"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) })

    })
    @GetMapping
    public Page<ConsultationResource> getAllConsultations( @Parameter(hidden = true) @RequestHeader("Authorization") String jwt, Pageable pageable) {
        return mapper.modelListPage(consultationService.getAll(jwt), pageable);
    }

    @Operation(summary = "Get consultation by id", description = "Returns consultation with a provided id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully got"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) })

    })
    @GetMapping("{consultationId}")
    public ConsultationResource getConsultationById(
            @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
            @Parameter(description = "Consultation id", required = true, examples = @ExampleObject(name = "consultationId", value = "1")) @PathVariable Integer consultationId
    ) {
        return mapper.toResource(consultationService.getById(jwt, consultationId));
    }

    @Operation(summary = "Get consultation by patient id", description = "Returns consultation with a provide patient id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully got"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) })

    })
    @GetMapping("byPatientId/{patientId}")
    public Page<ConsultationResource> getConsultationsByPatientId(
            @Parameter(hidden = true) @RequestHeader("Authorization") String jwt, @Parameter(description = "Patient Id", required = true, examples = @ExampleObject(name = "patientId", value = "1")) @PathVariable Integer patientId, Pageable pageable) {
        return mapper.modelListPage(consultationService.getByPatientId(patientId), pageable);
    }

    @Operation(summary = "Get consultations by physiotherapist id", description = "Returns consultations with a provided physiotherapist id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully got"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) })

    })
    @GetMapping("byPhysiotherapistId/{physiotherapistId}")
    public Page<ConsultationResource> getConsultationsByPhysiotherapistId(
            @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
            @Parameter(description = "Physiotherapist Id", required = true, examples = @ExampleObject(name = "physiotherapistId", value = "1")) @PathVariable Integer physiotherapistId, Pageable pageable
    ) {
        return mapper.modelListPage(consultationService.getByPhysiotherapistId(physiotherapistId), pageable);
    }

    @Operation(summary = "Get consultation by physiotherapist id", description = "Returns consultation with a provided physiotherapist id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully got"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) })

    })
    @GetMapping("consultationByPhysiotherapistId/{physiotherapistId}")
    public ConsultationResource getConsultationByPhysiotherapistId(
            @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
            @Parameter(description = "Physiotherapist Id", required = true, examples = @ExampleObject(name = "physiotherapistId", value = "1")) @PathVariable Integer physiotherapistId
    ) {
        return mapper.toResource(consultationService.getConsultationByPhysiotherapistId(physiotherapistId));
    }

    @Operation(summary = "Create consultation", description = "Register a consultation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = { @Content(schema = @Schema()) })
    })
    @PostMapping
    public ResponseEntity<ConsultationResource> createConsultation(
            @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
            @RequestBody CreateConsultationResource resource
    ) {
        return new ResponseEntity<>(mapper.toResource(consultationService.create(jwt, resource)), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a consultation partially", description = "Updates a consultation partially based on the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = { @Content(schema = @Schema()) })
    })
    @PatchMapping("{consultationId}")
    public ResponseEntity<ConsultationResource> patchConsultation(
            @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
            @Parameter(description = "Consultation Id", required = true, examples = @ExampleObject(name = "consultationId", value = "1")) @PathVariable Integer consultationId,
            @RequestBody UpdateConsultationResource request) {

        return new  ResponseEntity<>(mapper.toResource(consultationService.update(jwt, consultationId,request)), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete a consultation", description = "Delete a consultation with a provided id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) })
    })
    @DeleteMapping("{consultationId}")
    public ResponseEntity<?> deleteConsultation(
            @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
            @Parameter(description = "Consultation Id", required = true, examples = @ExampleObject(name = "consultationId", value = "1"))  @PathVariable Integer consultationId
    ) {
        return consultationService.delete(jwt, consultationId);
    }

    @Operation(summary = "Update a consultation's diagnosis", description = "Updates a consultation's diagnosis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = { @Content(schema = @Schema()) })
    })
    @PatchMapping("updateDiagnosis/{consultationId}")
    public ResponseEntity<ConsultationResource> updateConsultationDiagnosis(
            @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
            @Parameter(description = "Consultation Id", required = true, examples = @ExampleObject(name = "consultationId", value = "1")) @PathVariable Integer consultationId,
            @RequestBody String diagnosis
    ) {

        return new  ResponseEntity<>(mapper.toResource(consultationService.updateDiagnosis(jwt, consultationId,diagnosis)), HttpStatus.CREATED);
    }
}
