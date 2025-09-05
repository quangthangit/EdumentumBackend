package com.EdumentumBackend.EdumentumBackend.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;
import java.util.regex.Pattern;

public class SlugUtil {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern SPECIAL_CHARS = Pattern.compile("[&+,;.']");
    private static final Pattern MULTIPLE_DASHES = Pattern.compile("-+");
    private static final Random RANDOM = new Random();
    private static final int MAX_RETRIES = 10;

    public static String toSlug(String input) {
        if (input == null) {
            return "";
        }

        String withSpecialChars = SPECIAL_CHARS.matcher(input).replaceAll("-");

        String nowhitespace = WHITESPACE.matcher(withSpecialChars).replaceAll("-");

        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);

        String slug = NONLATIN.matcher(normalized).replaceAll("");

        slug = MULTIPLE_DASHES.matcher(slug).replaceAll("-");

        slug = slug.replaceAll("^-|-$", "");

        return slug.toLowerCase(Locale.ENGLISH);
    }

    public static String toUniqueSlug(String input) {
        String baseSlug = toSlug(input);
        String randomSuffix = String.format("%04d", RANDOM.nextInt(10000));
        return baseSlug + "-" + randomSuffix;
    }

    /**
     * Generates a new unique slug with a different random suffix
     * @param baseTitle The title to convert to a slug
     * @return A new slug with a random suffix
     */
    public static String generateNewUniqueSlug(String baseTitle) {
        String baseSlug = toSlug(baseTitle);
        String randomSuffix = String.format("%04d", RANDOM.nextInt(10000));
        return baseSlug + "-" + randomSuffix;
    }

    /**
     * Generates a fallback slug when all retries fail
     * @param baseTitle The title to convert to a slug
     * @return A fallback slug with timestamp to ensure uniqueness
     */
    public static String generateFallbackSlug(String baseTitle) {
        String baseSlug = toSlug(baseTitle);
        long timestamp = System.currentTimeMillis();
        return baseSlug + "-" + timestamp;
    }

    /**
     * Generates a unique slug with a retry mechanism to handle potential duplicates
     * This method ensures a non-null, unique slug is always returned
     * @param title The title to convert to a slug
     * @param existsChecker Function to check if a slug already exists
     * @param maxRetries Maximum number of retries before using fallback mechanism  
     * @return A guaranteed unique slug
     */
    public static String generateUniqueSlugWithRetry(String title, Function<String, Boolean> existsChecker, int maxRetries) {
        if (title == null || title.trim().isEmpty()) {
            title = "item-" + System.currentTimeMillis(); // Fallback for empty titles
        }

        // First attempt with the standard method
        String slug = toUniqueSlug(title);

        // If it doesn't exist, we can use it
        if (!existsChecker.apply(slug)) {
            return slug;
        }

        // Otherwise, try with different random suffixes up to maxRetries times
        for (int i = 0; i < maxRetries; i++) {
            slug = generateNewUniqueSlug(title);
            if (!existsChecker.apply(slug)) {
                return slug;
            }
        }

        // If all retries failed, use timestamp-based approach which should guarantee uniqueness
        return generateFallbackSlug(title);
    }
}
