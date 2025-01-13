package api.giybat.uz.enums;

import org.springframework.core.convert.converter.Converter;

public class AppLanguageConverter implements Converter<String, AppLanguage> {
    @Override
    public AppLanguage convert(String source) {
        try {
            return AppLanguage.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AppLanguage.UZ; // Default to 'UZ' if the value is invalid
        }
    }
}
