package api.giybat.uz.enums;

public enum AppLanguage {
    UZ("Uzbek"),
    EN("English"),
    RU("Russian");

    private final String languageName;

    AppLanguage(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageName() {
        return languageName;
    }

    public String getLanguageCode() {
        return this.name().toLowerCase();
    }

    public static AppLanguage fromString(String languageCode) {
        if (languageCode != null) {
            for (AppLanguage language : AppLanguage.values()) {
                if (language.name().equalsIgnoreCase(languageCode)) {
                    return language;
                }
            }
        }
        return UZ; // Default language
    }
}
