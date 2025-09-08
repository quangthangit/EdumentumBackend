package com.EdumentumBackend.EdumentumBackend.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

public class SlugUtil {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern SPECIAL_CHARS = Pattern.compile("[&+,;.'\"]");
    private static final Pattern MULTIPLE_DASHES = Pattern.compile("-+");
    private static final Random RANDOM = new Random();

    // Markdown/HTML stripping
    private static final Pattern CODE_FENCE = Pattern.compile("(?s)```.+?```");
    private static final Pattern INLINE_CODE = Pattern.compile("`([^`]*)`");
    private static final Pattern HEADING = Pattern.compile("(?m)^\\s{0,3}#{1,6}\\s*");
    private static final Pattern BLOCKQUOTE = Pattern.compile("(?m)^\\s{0,3}>+\\s*");
    private static final Pattern EMPHASIS = Pattern.compile("(\\*\\*\\*|___|\\*\\*|__|\\*|_|~~)");
    private static final Pattern IMAGE = Pattern.compile("!\\[([^\\]]*)]\\([^)]*\\)");
    private static final Pattern LINK = Pattern.compile("\\[([^\\]]+)]\\([^)]*\\)");
    private static final Pattern HTML_TAGS = Pattern.compile("<[^>]+>"); // e.g., <u>, <b>, <i>, etc.

    private static String stripMarkdownAndHtml(String s) {
        if (s == null) return "";
        String out = s;

        out = CODE_FENCE.matcher(out).replaceAll(" ");
        out = INLINE_CODE.matcher(out).replaceAll("$1");

        out = HEADING.matcher(out).replaceAll("");
        out = BLOCKQUOTE.matcher(out).replaceAll("");

        out = IMAGE.matcher(out).replaceAll("$1");

        out = LINK.matcher(out).replaceAll("$1");

        out = EMPHASIS.matcher(out).replaceAll("");

        out = HTML_TAGS.matcher(out).replaceAll(" ");

        out = out.replaceAll("\\s+", " ").trim();
        return out;
    }

    public static String toSlug(String input) {
        if (input == null) {
            return "";
        }
        String cleaned = stripMarkdownAndHtml(input);

        // 2) Replace special punctuation with dashes
        String withSpecialChars = SPECIAL_CHARS.matcher(cleaned).replaceAll("-");

        // 3) Whitespace -> dashes
        String nowhitespace = WHITESPACE.matcher(withSpecialChars).replaceAll("-");

        // 4) Normalize to remove diacritics (e.g., tiếng Việt -> tieng Viet)
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);

        // 5) Remove non-word chars (after normalization)
        String slug = NONLATIN.matcher(normalized).replaceAll("");

        // 6) Collapse multiple dashes and trim leading/trailing dashes
        slug = MULTIPLE_DASHES.matcher(slug).replaceAll("-");
        slug = slug.replaceAll("^-|-$", "");

        return slug.toLowerCase(Locale.ENGLISH);
    }

    public static String toUniqueSlug(String input) {
        String baseSlug = toSlug(input);
        String randomSuffix = String.format("%04d", RANDOM.nextInt(10000));
        return baseSlug.isEmpty() ? randomSuffix : baseSlug + "-" + randomSuffix;
    }

    public static String toSlugNoRandom(String input) {
        return toSlug(input);
    }

    public static String generateNewUniqueSlug(String baseTitle) {
        String baseSlug = toSlug(baseTitle);
        String randomSuffix = String.format("%04d", RANDOM.nextInt(10000));
        return baseSlug.isEmpty() ? randomSuffix : baseSlug + "-" + randomSuffix;
    }

    public static String generateFallbackSlug(String baseTitle) {
        String baseSlug = toSlug(baseTitle);
        long timestamp = System.currentTimeMillis();
        return baseSlug.isEmpty() ? String.valueOf(timestamp) : baseSlug + "-" + timestamp;
    }

    public static String generateUniqueSlugWithRetry(String title) {
        if (title == null || title.trim().isEmpty()) {
            title = "item-" + System.currentTimeMillis();
        }
        return toSlug(title);
    }
}
