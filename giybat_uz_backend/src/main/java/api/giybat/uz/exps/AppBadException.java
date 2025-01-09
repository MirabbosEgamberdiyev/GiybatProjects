package api.giybat.uz.exps;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AppBadException extends RuntimeException {
    public AppBadException(String message) {
        super(message);
    }
}

