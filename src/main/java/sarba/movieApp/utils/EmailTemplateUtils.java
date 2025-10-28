package sarba.movieApp.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class EmailTemplateUtils {

    // Cache the template content if your application sends many emails
    private static String templateContent = null;

    private static String loadTemplate() throws IOException {
        // Read the file from the resources folder
        ClassPathResource resource = new ClassPathResource("templates/" + "password-reset-otp-template.html");

        // Use StreamUtils to easily read the whole resource content into a String
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    public static String getPasswordResetHtml(int otp, String companyName, String supportEmail, String resetLink) throws IOException {
        // Load once and cache (simple mechanism)
        if (templateContent == null) {
            templateContent = loadTemplate();
        }

        // 1. Start with the full template content
        String htmlBody = templateContent;

        // 2. Replace the placeholders with actual values
        htmlBody = htmlBody.replace("${otp}", String.valueOf(otp));
        htmlBody = htmlBody.replace("${companyName}", companyName);
        htmlBody = htmlBody.replace("${supportEmail}", supportEmail);
        htmlBody = htmlBody.replace("${resetLink}", resetLink);

        return htmlBody;
    }
}