package tn.enicarthage.plate_be.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import tn.enicarthage.plate_be.annotations.Loggable;
import tn.enicarthage.plate_be.services.LoggingService;


@Aspect
@Component
public class LoggingAspect {

    private final LoggingService loggingService;

    public LoggingAspect(LoggingService loggingService) {
        this.loggingService = loggingService;
    }


    @After("@annotation(loggable)")
    public void logSuccess(JoinPoint joinPoint, Loggable loggable) {
        try {
            String email = extractEmailFromArguments(joinPoint.getArgs());
            loggingService.logAction(
                    email,
                    loggable.action(),
                    "SUCCESS",
                    loggable.description()
            );
        } catch (Exception e) {
            System.err.println("Erreur lors du logging success: " + e.getMessage());
        }
    }


    @AfterThrowing(pointcut = "@annotation(loggable)", throwing = "exception")
    public void logError(JoinPoint joinPoint, Loggable loggable, Exception exception) {
        try {
            String email = extractEmailFromArguments(joinPoint.getArgs());
            loggingService.logAction(
                    email,
                    loggable.action() + "_FAILED",
                    "FAILED",
                    "Erreur: " + exception.getMessage()
            );
        } catch (Exception e) {
            System.err.println("Erreur lors du logging error: " + e.getMessage());
        }
    }


    private String extractEmailFromArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return "UNKNOWN";
        }

        for (Object arg : args) {
            if (arg instanceof String && arg.toString().contains("@")) {
                return (String) arg;
            }
            try {
                if (arg.getClass().getMethod("getEmail") != null) {
                    Object email = arg.getClass().getMethod("getEmail").invoke(arg);
                    if (email instanceof String) {
                        return (String) email;
                    }
                }
            } catch (Exception e) {
            }
        }

        return "UNKNOWN";
    }
}

