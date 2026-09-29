package com.voxivoid.recipelab;

/**
 * The app menu (MENU hold) and the developer menu under it, and the sample run it can start: the rows each menu
 * has, the About and key-logger pages, the settle delay the run waits between applying a recipe and firing the
 * shutter, the progress lines it shows, and the manifest it writes so the frames can be matched to recipes afterwards.
 *
 * The run itself is a timed loop in MainActivity (stage a recipe → wait → shutter → wait → next); everything it
 * decides without the camera is here. No android.* import may appear in this class (tools/test.sh).
 */
final class DevTools {
    private DevTools() {}

    /** the two menu levels: the app menu a MENU hold opens, and the developer menu under it */
    static final int LEVEL_APP = 0, LEVEL_DEV = 1;

    /** app menu rows, in display order */
    static final int APP_BROWSE = 0, APP_PANEL = 1, APP_RESET = 2, APP_ABOUT = 3, APP_DEV = 4, APP_ROWS = 5;

    /** developer menu rows, in display order */
    static final int ROW_SNAPSHOT = 0, ROW_LOCKS = 1, ROW_SAMPLES = 2, ROW_SETTLE = 3, ROW_KEYS = 4, ROWS = 5;

    /**
     * Settle delays to pick from, in ms: how long the preview pipeline gets after a recipe is applied before the
     * shutter fires. The right one is a property of the camera, not of this table — a frame that still carries the
     * previous look means the delay is too short, which is why the row exists instead of a constant.
     */
    static final int[] SETTLE_MS = { 800, 1200, 2000, 3000, 5000 };
    /** the delay a fresh install starts on */
    static final int SETTLE_DEFAULT = 1;
    /** what the run gives a capture before it stages the next recipe, in ms (the shutter key's press → release, automated) */
    static final int SHUTTER_MS = 2500;

    /** the frame list the run writes into the app's files dir; the frames themselves are named by the camera */
    static final String MANIFEST = "samples.txt";
    static final String SOURCE_URL = "github.com/voxivoid/recipe-lab-sony-pmca";
    /** field separator of a manifest line; no recipe name contains it (RecipesTest) */
    static final String SEP = "|";

    // ------------------------------------------------------------ the app menu
    /** how many rows a level has */
    static int rows(int level) { return level == LEVEL_APP ? APP_ROWS : ROWS; }

    /** the row above / below on a level, wrapping */
    static int nextRow(int level, int row, int dir) { int n = rows(level); return (row + n + dir) % n; }

    /** the panel state left / right lands on: full → label → hidden, wrapping; the browser is never one of them */
    static int nextPanel(int overlay, int dir) {
        int o = overlay >= Params.OV_FULL && overlay <= Params.OV_HIDDEN ? overlay : Params.OV_FULL;
        return (o + 3 + dir) % 3;
    }

    // ------------------------------------------------------------ the reset question (hold trash, or Reset settings)
    /** Cancel is highlighted when the reset question opens, so a stray centre press changes nothing. */
    static final int RESET_DEFAULT = 1;

    /**
     * What the key probe found, as one line: "has Fn AEL C1  ·  lacks DISP  ·  unknown ZOOM_T". {@code has} lines up
     * with {@code scans}; a null entry is a key the camera would not answer for.
     */
    static String keysFound(int[] scans, Boolean[] has) {
        StringBuilder yes = new StringBuilder(), no = new StringBuilder(), unk = new StringBuilder();
        for (int i = 0; i < scans.length; i++) {
            StringBuilder b = has[i] == null ? unk : has[i] ? yes : no;
            b.append(b.length() == 0 ? "" : " ").append(Keys.name(scans[i]));
        }
        if (yes.length() == 0 && no.length() == 0) return "the camera would not say";
        StringBuilder out = new StringBuilder();
        if (yes.length() > 0) out.append("has ").append(yes);
        if (no.length() > 0) out.append(out.length() == 0 ? "" : "  ·  ").append("lacks ").append(no);
        if (unk.length() > 0) out.append(out.length() == 0 ? "" : "  ·  ").append("unknown ").append(unk);
        return out.toString();
    }

    private static String orUnknown(String s) { return s == null || s.isEmpty() ? "unknown" : s; }

    // ------------------------------------------------------------ the key logger
    /** how many events the logger keeps on screen, newest first */
    static final int LOG_LINES = 10;
    /** the file the logger appends to in the app's files dir */
    static final String KEY_LOG = "keys.txt";

    /** the logger page title: the body it runs on */
    static String logTitle(String model, String platform) { return "KEY LOGGER  ·  " + orUnknown(model) + "  ·  " + orUnknown(platform); }

    /**
     * One key event: {"down 595", "DELETE  ·  repeat 0  ·  logic 1103"}. The scan code is what a compatibility report
     * needs; the name, the repeat count and Sony's logic code (null before platform API 3) are what make sense of it.
     */
    static String[] logLine(boolean down, int scan, int repeat, Integer logic) {
        String name = Keys.name(scan);
        return new String[] { (down ? "down " : "up   ") + scan,
                (name.isEmpty() ? "?" : name) + "  ·  repeat " + repeat + (logic == null ? "" : "  ·  logic " + logic) };
    }

    // ------------------------------------------------------------ the developer menu
    /** the row above / below, wrapping */
    static int nextRow(int row, int dir) { return nextRow(LEVEL_DEV, row, dir); }

    /** the value a developer menu row shows at its right edge, which left / right change in place; null for none */
    static String rowValue(int row, int settle) { return row == ROW_SETTLE ? settleLabel(settle) : null; }

    // ------------------------------------------------------------ the settle delay
    /** a stored delay index brought back into the table */
    static int clampSettle(int idx) { return idx >= 0 && idx < SETTLE_MS.length ? idx : SETTLE_DEFAULT; }

    /** the next / previous delay, wrapping */
    static int nextSettle(int idx, int dir) { return (clampSettle(idx) + SETTLE_MS.length + dir) % SETTLE_MS.length; }

    /** a delay as the menu shows it: "1.2 s" (built by hand — String.format would follow the camera's locale) */
    static String settleLabel(int idx) {
        int ms = SETTLE_MS[clampSettle(idx)];
        return (ms / 1000) + "." + (ms % 1000) / 100 + " s";
    }

    // ------------------------------------------------------------ the sample run
    // ------------------------------------------------------------ the manifest
    /**
     * The first line of a run: what the columns are, and the delay it was shot with. Appended to, so a file can
     * hold several runs and each one says how it was made.
     */
    static String manifestHeader(int total, int settleMs) {
        return "# recipe-lab samples  ·  " + total + " frames in recipe order  ·  settle " + settleMs + " ms"
                + "  ·  frame" + SEP + "recipe" + SEP + "brand" + SEP + "values";
    }

    /** one frame: its number in the run, and the recipe that was applied for it */
    static String manifestLine(int frame, int recipeIndex) {
        Recipes.Recipe r = Recipes.ALL[recipeIndex];
        return pad2(frame) + SEP + r.name + SEP + Recipes.GROUPS[r.group] + SEP + r.summary();
    }

    private static String pad2(int n) { return n < 10 ? "0" + n : String.valueOf(n); }
}
