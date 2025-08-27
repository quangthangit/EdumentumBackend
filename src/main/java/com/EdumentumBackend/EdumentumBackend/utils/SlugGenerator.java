package com.EdumentumBackend.EdumentumBackend.utils;

import org.springframework.stereotype.Component;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class SlugGenerator {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern MULTIPLE_DASHES = Pattern.compile("-+");

    /**
     * Convert a string to a URL-friendly slug
     */
    public String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input for slug generation cannot be empty");
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .toLowerCase(Locale.ENGLISH);

        // Remove diacritics
        normalized = normalized.replaceAll("\\p{M}", "");

        // Replace whitespace with hyphens
        String noWhitespace = WHITESPACE.matcher(normalized).replaceAll("-");

        // Remove non-Latin characters
        String slug = NONLATIN.matcher(noWhitespace).replaceAll("");

        // Ensure no multiple consecutive dashes
        slug = MULTIPLE_DASHES.matcher(slug).replaceAll("-");

        // Remove leading/trailing dashes
        slug = slug.replaceAll("^-|-$", "");

        return slug;
    }
}