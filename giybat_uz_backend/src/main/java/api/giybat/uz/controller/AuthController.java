package api.giybat.uz.controller;

import api.giybat.uz.dto.AuthDTO;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.RegistrationDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.entity.ResultAsync;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.exps.*;
import api.giybat.uz.service.AuthService;
import api.giybat.uz.service.ResourceBundleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
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

    @Autowired
    private ResourceBundleService resourceBundleService;

    @PostMapping("/registration")
    public ResponseEntity<ResultAsync<ProfileEntity>> registration(
            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language,
            @Valid @RequestBody RegistrationDTO dto) {
        try {
            ProfileEntity registration = authService.registration(dto, language);

            if (registration != null) {
                String successMessage = resourceBundleService.getMessage("registration.success", language);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ResultAsync.success(registration, successMessage));
            }

            String defaultSuccessMessage = resourceBundleService.getMessage("registration.success", language);
            return ResponseEntity.ok(ResultAsync.success(null, defaultSuccessMessage));

        } catch (AppBadException e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() :
                    resourceBundleService.getMessage("registration.failed", language);
            return ResponseEntity.badRequest().body(ResultAsync.failure(errorMessage));

        } catch (UnauthorizedException e) {
            String errorMessage = resourceBundleService.getMessage("registration.unauthorized", language);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResultAsync.failure(errorMessage));

        } catch (ForbiddenException e) {
            String errorMessage = resourceBundleService.getMessage("registration.forbidden", language);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResultAsync.failure(errorMessage));

        } catch (ConflictException e) {
            String errorMessage = resourceBundleService.getMessage("registration.conflict", language);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResultAsync.failure(errorMessage));

        } catch (UnprocessableEntityException e) {
            String errorMessage = resourceBundleService.getMessage("registration.unprocessable", language);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ResultAsync.failure(errorMessage));

        } catch (TooManyRequestsException e) {
            String errorMessage = resourceBundleService.getMessage("registration.too_many_requests", language);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ResultAsync.failure(errorMessage));

        } catch (CustomServiceUnavailableException e) {
            String errorMessage = resourceBundleService.getMessage("registration.service_unavailable", language);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ResultAsync.failure(errorMessage));

        } catch (Exception e) {
            String errorMessage = resourceBundleService.getMessage("error.unexpected", language);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResultAsync.failure(errorMessage));
        }
    }


    @GetMapping("/registration/verification/{profileId}")
    public ResponseEntity<String> regVerification(
            @PathVariable("profileId") Integer profileId,
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage language) {
        try {
            String result = authService.regVerification(profileId, language);
            String message = result != null ? result :
                    resourceBundleService.getMessage("verification.success", language);

            String html = loadHtmlTemplate(message);
            return ResponseEntity.ok().body(html);

        } catch (AppBadException e) {
            String message = e.getMessage() != null ? e.getMessage() :
                    resourceBundleService.getMessage("verification.failed", language);
            String html = loadHtmlTemplate(message);
            return ResponseEntity.badRequest().body(html);

        } catch (UnauthorizedException e) {
            String message = resourceBundleService.getMessage("verification.unauthorized", language);
            String html = loadHtmlTemplate(message);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(html);

        } catch (ForbiddenException e) {
            String message = resourceBundleService.getMessage("verification.forbidden", language);
            String html = loadHtmlTemplate(message);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(html);

        } catch (NotFoundException e) {
            String message = resourceBundleService.getMessage("verification.not_found", language);
            String html = loadHtmlTemplate(message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(html);

        } catch (Exception e) {
            String message = resourceBundleService.getMessage("error.unexpected", language);
            String html = loadHtmlTemplate(message);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(html);
        }
    }

    private String loadHtmlTemplate(String message) {
        try {
            // HTML shablonini o‘qish
            String templateContent = Files.readString(emailTemplate.getFile().toPath(), StandardCharsets.UTF_8);

            // CSS shablonini o‘qish
            String cssContent = Files.readString(cssTemplate.getFile().toPath(), StandardCharsets.UTF_8);

            // CSSni HTML shabloniga qo‘shish
            templateContent = templateContent.replace("<head>", "<head><style>" + cssContent + "</style>");

            // Dinamik xabarni joylashtirish
            templateContent = templateContent.replace("${message}", message);

            return templateContent;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load HTML or CSS template", e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResultAsync<ProfileDTO>> login(
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage language,
            @Valid @RequestBody AuthDTO dto) {
        try {
            ProfileDTO login = authService.login(dto, language);

            if (login != null) {
                String successMessage = resourceBundleService.getMessage("login.success", language);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ResultAsync.success(login, successMessage));
            }

            String defaultSuccessMessage = resourceBundleService.getMessage("login.success", language);
            return ResponseEntity.ok(ResultAsync.success(null, defaultSuccessMessage));

        } catch (AppBadException e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() :
                    resourceBundleService.getMessage("login.failed", language);
            return ResponseEntity.badRequest().body(ResultAsync.failure(errorMessage));

        } catch (UnauthorizedException e) {
            String errorMessage = resourceBundleService.getMessage("login.unauthorized", language);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResultAsync.failure(errorMessage));

        } catch (ForbiddenException e) {
            String errorMessage = resourceBundleService.getMessage("login.forbidden", language);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResultAsync.failure(errorMessage));

        } catch (ConflictException e) {
            String errorMessage = resourceBundleService.getMessage("login.conflict", language);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResultAsync.failure(errorMessage));

        } catch (UnprocessableEntityException e) {
            String errorMessage = resourceBundleService.getMessage("logi n.unprocessable", language);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ResultAsync.failure(errorMessage));

        } catch (TooManyRequestsException e) {
            String errorMessage = resourceBundleService.getMessage("login.too_many_requests", language);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ResultAsync.failure(errorMessage));

        } catch (CustomServiceUnavailableException e) {
            String errorMessage = resourceBundleService.getMessage("login.service_unavailable", language);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ResultAsync.failure(errorMessage));

        } catch (Exception e) {
            String errorMessage = resourceBundleService.getMessage("error.unexpected", language);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResultAsync.failure(errorMessage));
        }
    }
}
