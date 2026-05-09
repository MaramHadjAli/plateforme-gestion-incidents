package tn.enicarthage.plate_be.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Recover;
import org.springframework.http.HttpStatus;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

/**
 * Service pour interagir avec l'API Gemini de Google.
 * Gère les tentatives de reconnexion et le passage automatique à un modèle de secours.
 */
@Service
@RequiredArgsConstructor
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    /**
     * Nom du modèle principal à utiliser (ex: models/gemini-2.5-flash).
     */
    @Value("${gemini.model:models/gemini-2.5-flash}")
    private String modelName;

    /**
     * Modèle de secours si le modèle principal n'existe pas (404) ou n'est pas supporté.
     */
    @Value("${gemini.model.fallback:models/gemini-2.5-pro}")
    private String fallbackModelName;

    private final RestTemplate restTemplate;

    /**
     * Génère du contenu à partir d'un prompt en utilisant l'API Gemini.
     * 
     * @param prompt Le texte à envoyer à l'IA.
     * @return La réponse générée par l'IA.
     */
    @Retryable(
        retryFor = { HttpStatusCodeException.class }, 
        maxAttempts = 5, 
        backoff = @Backoff(delay = 5000, multiplier = 2.0, maxDelay = 60000)
    )
    public String generateContent(String prompt) {
        try {
            // Construction de l'URL dynamiquement
            String url = buildGenerateContentUrl(modelName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = prepareRequestBody(prompt);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            String maskedUrl = url.replace(apiKey, "REDACTED");
            logger.info("Envoi de la requête à l'API Gemini. URL: {} | Prompt: {}", maskedUrl, prompt);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return extractTextFromResponse(response.getBody());

        } catch (HttpStatusCodeException e) {
            // Gestion du fallback si le modèle principal est introuvable (404)
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Modèle principal introuvable (404). Tentative avec le modèle de secours: {}", fallbackModelName);
                return generateContentWithExplicitUrl(buildGenerateContentUrl(fallbackModelName), prompt);
            }

            // Relancer pour déclencher le @Retryable pour les erreurs temporaires
            if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE || e.getStatusCode().value() == 429) {
                logger.warn("Service indisponible ou quota dépassé ({}). Tentative de réessai...", e.getStatusCode());
                throw e;
            }

            logger.error("Erreur HTTP Gemini ({}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            return "Désolé, une erreur technique est survenue avec l'IA : " + e.getStatusCode();

        } catch (Exception e) {
            logger.error("Erreur inattendue dans GeminiService", e);
            return "Une erreur interne est survenue lors du traitement de votre demande.";
        }
    }

    private Map<String, Object> prepareRequestBody(String prompt) {
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(part));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(content));
        return requestBody;
    }

    private String extractTextFromResponse(Map<String, Object> body) {
        if (body != null && body.containsKey("candidates")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> firstCandidate = candidates.get(0);
                @SuppressWarnings("unchecked")
                Map<String, Object> resContent = (Map<String, Object>) firstCandidate.get("content");
                if (resContent != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) resContent.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }
        }
        logger.warn("Réponse Gemini vide ou malformée.");
        return "Désolé, je n'ai pas pu générer une réponse claire.";
    }

    private String buildGenerateContentUrl(String model) {
        if (model != null && !model.isBlank()) {
            // Utilisation de v1 pour les modèles stables de 2026
            return "https://generativelanguage.googleapis.com/v1/" + model + ":generateContent?key=" + apiKey;
        }
        return apiUrl + (apiUrl.contains("?") ? "&" : "?") + "key=" + apiKey;
    }

    private String generateContentWithExplicitUrl(String url, String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(prepareRequestBody(prompt), headers);

            String maskedUrl = url.replace(apiKey, "REDACTED");
            logger.info("Envoi de la requête au modèle de secours. URL: {}", maskedUrl);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return extractTextFromResponse(response.getBody());
        } catch (Exception ex) {
            logger.error("Échec du modèle de secours Gemini", ex);
            return "Désolé, même le modèle de secours n'a pas pu répondre.";
        }
    }

    /**
     * Méthode de secours appelée après épuisement des tentatives de @Retryable.
     */
    @Recover
    public String recover(HttpStatusCodeException e, String prompt) {
        logger.error("Épuisement des tentatives Gemini pour le prompt: {}. Erreur: {}", prompt, e.getMessage());
        return "L'IA est actuellement saturée. Veuillez réessayer dans quelques minutes. (Erreur " + e.getStatusCode() + ")";
    }
}
