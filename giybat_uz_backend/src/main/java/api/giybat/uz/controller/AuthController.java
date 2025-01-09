package api.giybat.uz.controller;

import api.giybat.uz.dto.AuthDTO;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.RegistrationDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.entity.ResultAsync;
import api.giybat.uz.exps.AppBadException;
import api.giybat.uz.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    @Value("classpath:templates/html/registration-success.html")
    private Resource emailTemplate;

    @Value("classpath:templates/css/registration-success.css")
    private Resource cssTemplate;

    @GetMapping("/registration/verification/{profileId}")
    public ResponseEntity<String> regVerification(@PathVariable("profileId") Integer profileId) {
        try {
            // Verify the registration status
            String result = authService.regVerification(profileId);
            String message = result != null ? result : "Verification completed successfully";

            // Load the HTML template and embed the CSS
            String html = loadHtmlTemplate(message);
            return ResponseEntity.ok().body(html);
        } catch (AppBadException e) {
            // Handle application-specific exception
            String message = e.getMessage() != null ? e.getMessage() : "Verification failed";
            String html = loadHtmlTemplate(message);
            return ResponseEntity.badRequest().body(html);
        } catch (Exception e) {
            // Handle general exceptions
            String html = loadHtmlTemplate("An unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(html);
        }
    }

    private String loadHtmlTemplate(String message) {
        try {
            // Read the HTML template content
            String templateContent = Files.readString(emailTemplate.getFile().toPath(), StandardCharsets.UTF_8);

            // Read the CSS content
            String cssContent = Files.readString(cssTemplate.getFile().toPath(), StandardCharsets.UTF_8);

            // Embed CSS into the HTML template's <head> section
            templateContent = templateContent.replace("<head>", "<head><style>" + cssContent + "</style>");

            // Replace the placeholder with the dynamic message
            templateContent = templateContent.replace("${message}", message);

            return templateContent;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load HTML or CSS template", e);
        }
    }


    @PostMapping("/registration")
    public ResponseEntity<ResultAsync<ProfileEntity>> registration(@Valid @RequestBody RegistrationDTO dto) {
        try {
            ProfileEntity registration = authService.registration(dto);
            if (registration != null) {
                return ResponseEntity.ok(ResultAsync.success(registration, "Registration successfully"));
            }
            return ResponseEntity.ok(ResultAsync.success(null, "Registration successfully"));

        } catch (AppBadException e) {
            return ResponseEntity.badRequest().body(ResultAsync.failure(e.getMessage() != null ? e.getMessage() : "Registration failed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResultAsync.failure("An unexpected error occurred"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResultAsync<ProfileDTO>> login(@Valid @RequestBody AuthDTO dto) {
        try {
            ProfileDTO registration = authService.login(dto);
            if (registration != null) {
                return ResponseEntity.ok(ResultAsync.success(registration, "Login successfully"));
            }
            return ResponseEntity.ok(ResultAsync.success(null, "Login successfully"));

        } catch (AppBadException e) {
            return ResponseEntity.badRequest().body(ResultAsync.failure(e.getMessage() != null ? e.getMessage() : "Login failed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResultAsync.failure("An unexpected error occurred"));
        }
    }
}
