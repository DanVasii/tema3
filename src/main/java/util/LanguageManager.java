package util;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static final String BUNDLE_BASE_NAME = "i18n.messages";
    private static ResourceBundle resourceBundle;
    private static Locale currentLocale;

    public static void setLocale(Locale locale) {
        System.out.println("LOCALE SET");
        System.out.println(locale.getLanguage());
        currentLocale = locale;
        resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);

    }

    public static void setLocale(String language) {
        setLocale(new Locale(language));
    }

    public static ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            System.out.println("LOCALE NULL");
            // Default to English
            setLocale(Locale.ENGLISH);
        }
        return resourceBundle;
    }

    public static String getString(String key) {
        try {
            return getResourceBundle().getString(key);
        } catch (Exception e) {
            System.err.println("Missing resource key: " + key);
            return key;
        }
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static Locale[] getAvailableLocales() {
        return new Locale[] {
                Locale.ENGLISH,
                Locale.FRENCH,
                new Locale("es")
        };
    }
}
