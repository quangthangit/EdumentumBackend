package com.EdumentumBackend.EdumentumBackend.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

public class SlugUtil {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern SPECIAL_CHARS = Pattern.compile("[&+,;.']");
    private static final Pattern MULTIPLE_DASHES = Pattern.compile("-+");
    private static final Random RANDOM = new Random();

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
}
