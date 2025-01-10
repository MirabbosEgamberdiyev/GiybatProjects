package api.giybat.uz.exps;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class CustomServiceUnavailableException extends RuntimeException {
    public CustomServiceUnavailableException(String message) {
        super(message);
    }
}
