package tn.enicarthage.plate_be.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import tn.enicarthage.plate_be.entities.TraceLogin;
import tn.enicarthage.plate_be.repositories.TraceLoginRepository;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class LoggingService {

    private final TraceLoginRepository traceLoginRepository;

    public LoggingService(TraceLoginRepository traceLoginRepository) {
        this.traceLoginRepository = traceLoginRepository;
    }


    public void logAction(String email, String action, String status, String details) {
        try {
            HttpServletRequest request = null;
            try {
                request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
            } catch (Exception e) {
            }

            TraceLogin log = TraceLogin.builder()
                    .email(email)
                    .action(action)
                    .status(status)
                    .date(LocalDateTime.now())
                    .details(details)
                    .ipAddress(getClientIpAddress(request))
                    .userAgent(request != null ? request.getHeader("User-Agent") : null)
                    .build();

            traceLoginRepository.save(log);
        } catch (Exception e) {
            System.err.println("Erreur lors du logging: " + e.getMessage());
        }
    }


    public Page<TraceLogin> getAllLogs(Pageable pageable) {
        return traceLoginRepository.findAllByOrderByDateDesc(pageable);
    }

    public Page<TraceLogin> getUserLogs(String email, Pageable pageable) {
        return traceLoginRepository.findByEmailOrderByDateDesc(email, pageable);
    }


    public Page<TraceLogin> getFailedAttempts(Pageable pageable) {
        return traceLoginRepository.findFailedAttempts(pageable);
    }


    public int getRecentFailedAttempts(String email, int minutesBack) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(minutesBack);
        return (int) traceLoginRepository.countRecentFailedAttempts(email, since);
    }


    public Page<TraceLogin> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return traceLoginRepository.findByDateRange(startDate, endDate, pageable);
    }


    public java.util.List<TraceLogin> getLogsByAction(String action) {
        return traceLoginRepository.findByActionOrderByDateDesc(action);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String[] headers = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }
}

