package com.voxivoid.recipelab;

import android.graphics.Paint;

import java.util.List;

/** Android adapter for the host-tested text layout; never changes the Paint's size or font. */
final class UiCanvas {
    private UiCanvas() {}

    static TextFlow.Measure measure(final Paint paint) {
        return new TextFlow.Measure() {
            public float width(String text) {
                return paint.measureText(text);
            }
        };
    }

    static String fit(String text, Paint paint, float width) {
        return TextFlow.fit(text, width, measure(paint));
    }

    static void wrap(String text, Paint paint, float width, List<String> into) {
        into.clear();
        into.addAll(TextFlow.wrap(text, width, measure(paint)));
    }
}
