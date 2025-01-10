package api.giybat.uz.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Logger;
import org.springframework.scheduling.annotation.Async;

@Service
public class EmailSendingService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromAccount;

    @Value("classpath:templates/html/mail-test.html")
    private Resource emailTemplate;

    @Value("classpath:templates/css/mail-test.css")
    private Resource cssTemplate;

    private static final Logger logger = Logger.getLogger(EmailSendingService.class.getName());

    @Async
    public void sendRegistrationEmail(String email, Integer profileId) {
        String subject = "Registration Confirmation";
        String body = loadEmailTemplate(profileId);

        if (!body.isEmpty()) {
            sendEmail(email, subject, body);
        } else {
            logger.warning("Failed to load email template for profileId: " + profileId);
        }
    }

    private String loadEmailTemplate(Integer profileId) {
        try {
            String templateContent = Files.readString(emailTemplate.getFile().toPath(), StandardCharsets.UTF_8);
            String cssContent = Files.readString(cssTemplate.getFile().toPath(), StandardCharsets.UTF_8);

            templateContent = templateContent.replace("<head>", "<head><style>" + cssContent + "</style>");
            return templateContent.replace("{{profileId}}", profileId.toString());
        } catch (IOException e) {
            logger.severe("Error loading email template: " + e.getMessage());
            return "";
        }
    }

    private void sendEmail(String email, String subject, String body) {
        MimeMessage msg = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);  // 'true' => HTML email format
            helper.setFrom(fromAccount);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true);  // 'true' ensures HTML content
            javaMailSender.send(msg);
        } catch (MessagingException e) {
            logger.severe("Error sending email to " + email + ": " + e.getMessage());
        }
    }
}
