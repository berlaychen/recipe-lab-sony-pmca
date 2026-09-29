package com.voxivoid.recipelab;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Dependency-free host checks. This is not an Android or on-camera rendering test. */
public final class TextFlowChecks {
    private static int checks;
    private static final TextFlow.Measure MONO = new TextFlow.Measure() {
        public float width(String s) { return s.codePointCount(0, s.length()); }
    };
    private static void equal(Object expected, Object actual) {
        checks++;
        if (!expected.equals(actual)) throw new AssertionError("expected " + expected + ", got " + actual);
    }
    private static void validUnicode(String s) {
        checks++;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isHighSurrogate(c)) {
                if (++i >= s.length() || !Character.isLowSurrogate(s.charAt(i))) throw new AssertionError("split surrogate");
            } else if (Character.isLowSurrogate(c)) throw new AssertionError("orphan low surrogate");
        }
    }
    public static void main(String[] args) {
        equal(Arrays.asList(""), TextFlow.wrap("", 4, MONO));
        equal(Arrays.asList(""), TextFlow.wrap(null, 4, MONO));
        equal(Arrays.asList("alpha", "beta"), TextFlow.wrap("alpha beta", 5, MONO));
        equal(Arrays.asList("alpha beta"), TextFlow.wrap("alpha beta", 10, MONO));
        equal(Arrays.asList("abc", "def", "gh"), TextFlow.wrap("abcdefgh", 3, MONO));
        equal(Arrays.asList("a", "", "b", ""), TextFlow.wrap("a\r\n\nb\n", 4, MONO));
        equal(Arrays.asList("a", "b"), TextFlow.wrap("ab", 0, MONO));
        equal(Arrays.asList("a", "b"), TextFlow.wrap("ab", -1, MONO));
        String zh = "\u8a2d\u5b9a\u767d\u5e73\u8861\u4e26\u5957\u7528\u914d\u65b9";
        equal(Arrays.asList(zh.substring(0,4), zh.substring(4,8), zh.substring(8)), TextFlow.wrap(zh, 4, MONO));
        String face = "\ud83d\ude00";
        equal(Arrays.asList(face, face), TextFlow.wrap(face + face, 1, MONO));
        equal("ab...", TextFlow.fit("abcdefgh", 5, MONO));
        equal("..", TextFlow.fit("abcdefgh", 2, MONO));
        equal("", TextFlow.fit("abcdefgh", 0, MONO));
        equal("abc", TextFlow.fit("abc", 3, MONO));
        equal(face + "...", TextFlow.fit(face + face + face + face + face, 4, MONO));
        Random random = new Random(20260928L);
        String[] glyphs = {"a", "b", "\u767d", "\u5e73", "\u8861", face};
        for (int n = 0; n < 500; n++) {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < 1 + random.nextInt(60); i++) b.append(glyphs[random.nextInt(glyphs.length)]);
            String original = b.toString();
            float width = 1 + random.nextInt(12);
            List<String> lines = TextFlow.wrap(original, width, MONO);
            StringBuilder rejoined = new StringBuilder();
            for (String line : lines) {
                validUnicode(line); rejoined.append(line);
                equal(true, MONO.width(line) <= width);
            }
            equal(original, rejoined.toString());
            String fitted = TextFlow.fit(original, width, MONO);
            validUnicode(fitted); equal(true, MONO.width(fitted) <= width);
        }
        System.out.println("TextFlow: " + checks + " assertions passed (500 seeded randomized cases)");
    }
}
