package api.giybat.uz.service;

import api.giybat.uz.enums.AppLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ResourceBundleService {
    @Autowired
    private MessageSource messageSource;

    public String getMessage(String code, AppLanguage language) {
        return messageSource.getMessage(code, null, new Locale(language.getLanguageCode()));
    }
}