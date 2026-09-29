package com.voxivoid.recipelab;

import java.util.ArrayList;
import java.util.List;

/** Pixel-width based wrapping for Latin words, CJK text, and long diagnostics. No Android dependency. */
final class TextFlow {
    private TextFlow() {}

    interface Measure {
        float width(String text);
    }

    /**
     * Prefer a whitespace boundary, but split an oversized word at a Unicode code point.
     * Explicit newlines are preserved. A glyph wider than the entire box gets its own line:
     * dropping it or retrying forever would be worse. Surrogate pairs are never split.
     */
    static List<String> wrap(String text, float width, Measure measure) {
        if (measure == null) {
            throw new IllegalArgumentException("measure must not be null");
        }

        List<String> lines = new ArrayList<String>();
        String normalized = text == null ? "" : text.replace("\r\n", "\n").replace('\r', '\n');
        for (String paragraph : normalized.split("\n", -1)) {
            String value = paragraph.trim();
            if (value.length() == 0) {
                lines.add("");
                continue;
            }

            while (value.length() > 0) {
                int end = fittingEnd(value, width, measure);
                if (end == value.length()) {
                    lines.add(value);
                    break;
                }

                // Always make progress, including at zero width or with an oversized glyph.
                if (end == 0) {
                    end = Character.charCount(value.codePointAt(0));
                }

                int cut = end;
                for (int pos = end; pos > 0;) {
                    int codePoint = value.codePointBefore(pos);
                    pos -= Character.charCount(codePoint);
                    if (Character.isWhitespace(codePoint) && pos > 0) {
                        cut = pos;
                        break;
                    }
                }

                // If the next character is whitespace, a complete word fits as it stands.
                if (end < value.length() && Character.isWhitespace(value.codePointAt(end))) {
                    cut = end;
                }

                lines.add(value.substring(0, cut).trim());
                value = value.substring(cut).trim();
            }
        }
        return lines;
    }

    /** A bounded single line. ASCII dots are used because some PMCA fonts lack the ellipsis glyph. */
    static String fit(String text, float width, Measure measure) {
        if (measure == null) {
            throw new IllegalArgumentException("measure must not be null");
        }

        String value = text == null ? "" : text;
        if (!(width > 0)) {
            return "";
        }
        if (measure.width(value) <= width) {
            return value;
        }

        String suffix = "...";
        while (suffix.length() > 0 && measure.width(suffix) > width) {
            suffix = suffix.substring(1);
        }
        if (suffix.length() == 0) {
            return "";
        }

        int end = fittingEnd(value, width - measure.width(suffix), measure);
        String result = value.substring(0, end).trim() + suffix;
        // Measure the complete rendered candidate too: kerning need not be additive.
        while (end > 0 && measure.width(result) > width) {
            end -= Character.charCount(value.codePointBefore(end));
            result = value.substring(0, end).trim() + suffix;
        }
        return result;
    }

    private static int fittingEnd(String text, float width, Measure measure) {
        int end = 0;
        for (int next = 0; next < text.length();) {
            next += Character.charCount(text.codePointAt(next));
            if (measure.width(text.substring(0, next)) > width) {
                break;
            }
            end = next;
        }
        return end;
    }
}
