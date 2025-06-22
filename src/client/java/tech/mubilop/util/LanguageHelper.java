package tech.mubilop.util;

import net.minecraft.client.MinecraftClient;
import tech.mubilop.ColorMessage;

import java.util.Locale;

public class LanguageHelper {
    
    /**
     * Checks if the current game language is Brazilian Portuguese
     * @return true if the language is pt_br
     */
    public static boolean isPortugueseBR() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.getLanguageManager() != null) {
                String currentLanguage = client.getLanguageManager().getLanguage().toLowerCase(Locale.ROOT);
                return currentLanguage.equals("pt_br");
            }
        } catch (Exception e) {
            ColorMessage.LOGGER.error("Error detecting language", e);
        }
        return false;
    }
    
    /**
     * Gets a localized message based on the current language
     * @param englishText The English text
     * @param portugueseText The Portuguese text
     * @return The appropriate text for the current language
     */
    public static String getLocalizedText(String englishText, String portugueseText) {
        return isPortugueseBR() ? portugueseText : englishText;
    }
}
