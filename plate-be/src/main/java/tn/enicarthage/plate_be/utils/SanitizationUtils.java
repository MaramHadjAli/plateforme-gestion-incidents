package tn.enicarthage.plate_be.utils;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class SanitizationUtils {

    private static final PolicyFactory POLICY = new HtmlPolicyBuilder()
            .allowElements("b", "i", "em", "strong", "p", "br", "ul", "ol", "li")
            .allowAttributes("class").onElements("p", "div", "span")
            .toFactory();

    public static String sanitizeInput(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return POLICY.sanitize(input);
    }


    public static String escapeSQLCharacters(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("'", "''")
                   .replace("\"", "\\\"")
                   .replace("\\", "\\\\")
                   .replace("\0", "\\0")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\u001a", "\\Z");
    }


    public static boolean isValidInput(String input, int maxLength) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        if (input.length() > maxLength) {
            return false;
        }
        for (char c : input.toCharArray()) {
            if (Character.isISOControl(c) && c != '\n' && c != '\r' && c != '\t') {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex) && email.length() <= 254;
    }
}

