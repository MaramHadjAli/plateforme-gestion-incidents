package tn.enicarthage.plate_be.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import tn.enicarthage.plate_be.dtos.logs.LogsPageResponse;
import tn.enicarthage.plate_be.dtos.logs.TraceLoginDTO;
import tn.enicarthage.plate_be.entities.TraceLogin;
import tn.enicarthage.plate_be.services.LoggingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final LoggingService loggingService;

    public AdminController(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<TraceLogin> logsPage = loggingService.getAllLogs(pageable);
        
        List<TraceLoginDTO> logsDTOs = logsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        LogsPageResponse response = new LogsPageResponse(
                logsDTOs,
                logsPage.getNumber(),
                logsPage.getTotalPages(),
                logsPage.getTotalElements(),
                logsPage.hasNext(),
                logsPage.hasPrevious()
        );
        
        return ResponseEntity.ok(response);
    }


    @GetMapping("/logs/user/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserLogs(
            @PathVariable String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<TraceLogin> logsPage = loggingService.getUserLogs(email, pageable);
        
        List<TraceLoginDTO> logsDTOs = logsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        LogsPageResponse response = new LogsPageResponse(
                logsDTOs,
                logsPage.getNumber(),
                logsPage.getTotalPages(),
                logsPage.getTotalElements(),
                logsPage.hasNext(),
                logsPage.hasPrevious()
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logs/failed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getFailedAttempts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<TraceLogin> logsPage = loggingService.getFailedAttempts(pageable);
        
        List<TraceLoginDTO> logsDTOs = logsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        LogsPageResponse response = new LogsPageResponse(
                logsDTOs,
                logsPage.getNumber(),
                logsPage.getTotalPages(),
                logsPage.getTotalElements(),
                logsPage.hasNext(),
                logsPage.hasPrevious()
        );
        
        return ResponseEntity.ok(response);
    }


    @GetMapping("/logs/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getLogsByAction(@PathVariable String action) {
        List<TraceLogin> logs = loggingService.getLogsByAction(action);
        
        List<TraceLoginDTO> logsDTOs = logs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(logsDTOs);
    }


    @GetMapping("/logs/range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getLogsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
        LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
        
        Pageable pageable = PageRequest.of(page, size);
        Page<TraceLogin> logsPage = loggingService.getLogsByDateRange(start, end, pageable);
        
        List<TraceLoginDTO> logsDTOs = logsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        LogsPageResponse response = new LogsPageResponse(
                logsDTOs,
                logsPage.getNumber(),
                logsPage.getTotalPages(),
                logsPage.getTotalElements(),
                logsPage.hasNext(),
                logsPage.hasPrevious()
        );
        
        return ResponseEntity.ok(response);
    }


    @GetMapping("/logs/user/{email}/failed-attempts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRecentFailedAttempts(
            @PathVariable String email,
            @RequestParam(defaultValue = "15") int minutes) {
        
        int failedAttempts = loggingService.getRecentFailedAttempts(email, minutes);
        
        return ResponseEntity.ok(new Object() {
            public String userEmail = email;
            public int failedAttemptsLastMinutes = failedAttempts;
            public int minutesWindow = minutes;
        });
    }


    private TraceLoginDTO convertToDTO(TraceLogin log) {
        return new TraceLoginDTO(
                log.getId(),
                log.getEmail(),
                log.getAction(),
                log.getStatus(),
                log.getDate(),
                log.getIpAddress(),
                log.getUserAgent(),
                log.getDetails()
        );
    }
}

