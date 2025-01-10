package api.giybat.uz.controller;

import api.giybat.uz.dto.AuthDTO;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.RegistrationDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.entity.ResultAsync;
import api.giybat.uz.exps.*;
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
            String result = authService.regVerification(profileId);
            String message = result != null ? result : "Verification completed successfully";

            String html = loadHtmlTemplate(message);
            return ResponseEntity.ok().body(html);

        } catch (AppBadException e) {
            String message = e.getMessage() != null ? e.getMessage() : "Verification failed";
            String html = loadHtmlTemplate(message);
            return ResponseEntity.badRequest().body(html);

        } catch (UnauthorizedException e) {
            String html = loadHtmlTemplate("You are not authorized to verify this registration");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(html);

        } catch (ForbiddenException e) {
            String html = loadHtmlTemplate("You are not permitted to verify this registration");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(html);

        } catch (NotFoundException e) {
            String html = loadHtmlTemplate("Profile not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(html);

        } catch (Exception e) {
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

            // Embed the CSS into the HTML template's <head> section
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
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ResultAsync.success(registration, "Registration successful"));
            }

            return ResponseEntity.ok(ResultAsync.success(null, "Registration successful"));

        } catch (AppBadException e) {
            return ResponseEntity.badRequest()
                    .body(ResultAsync.failure(e.getMessage() != null ? e.getMessage() : "Registration failed"));

        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResultAsync.failure("Invalid credentials provided"));

        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResultAsync.failure("You are not allowed to register"));

        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResultAsync.failure("User with this username already exists"));

        } catch (UnprocessableEntityException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ResultAsync.failure("Password must be at least 8 characters long"));

        } catch (TooManyRequestsException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ResultAsync.failure("Too many registration attempts. Please try again later"));

        } catch (CustomServiceUnavailableException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ResultAsync.failure("Service is temporarily unavailable. Please try again later"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultAsync.failure("An unexpected error occurred"));
        }
    }



    @PostMapping("/login")
    public ResponseEntity<ResultAsync<ProfileDTO>> login(@Valid @RequestBody AuthDTO dto) {
        try {
            ProfileDTO profile = authService.login(dto);

            if (profile != null) {
                return ResponseEntity.ok(ResultAsync.success(profile, "Login successful"));
            }

            return ResponseEntity.ok(ResultAsync.success(null, "Login successful"));

        } catch (AppBadException e) {
            return ResponseEntity.badRequest()
                    .body(ResultAsync.failure(e.getMessage() != null ? e.getMessage() : "Login failed"));

        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResultAsync.failure("Invalid credentials provided"));

        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResultAsync.failure("You are not authorized to log in"));

        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResultAsync.failure("Conflict during login attempt"));

        } catch (UnprocessableEntityException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ResultAsync.failure("Login failed due to invalid data"));

        } catch (TooManyRequestsException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ResultAsync.failure("Too many login attempts. Please try again later"));

        } catch (CustomServiceUnavailableException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ResultAsync.failure("Service is temporarily unavailable. Please try again later"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultAsync.failure("An unexpected error occurred"));
        }
    }

}
