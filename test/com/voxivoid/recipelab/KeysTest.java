package com.voxivoid.recipelab;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/** The keys (issue #18): the press / hold gesture, the trash-hold guard, and a legend that only names keys the body has. */
class KeysTest {
    private static final Keys.Caps NONE = new Keys.Caps(false), FN = new Keys.Caps(true);
    private static final int[] EVERY_MODE = { Keys.H_RECIPE, Keys.H_CHIPS, Keys.H_EDIT, Keys.H_BRANDS, Keys.H_RECIPES,
            Keys.H_MENU_TOP, Keys.H_MENU_SUB, Keys.H_PAGE, Keys.H_LOGGER, Keys.H_MENU_TOP_VALUE, Keys.H_MENU_SUB_VALUE };

    // ---- press / hold
    @Test void aPressReleasedBeforeTheHoldIsShort() {
        Keys.Hold h = new Keys.Hold();
        assertEquals(Keys.Hold.ARM, h.down(0));
        assertEquals(Keys.Hold.SHORT, h.up());
    }

    @Test void aHoldThatFiredSwallowsItsRelease() {
        Keys.Hold h = new Keys.Hold();
        h.down(0);
        assertEquals(Keys.Hold.HOLD, h.fire());
        assertEquals(Keys.Hold.NONE, h.up(), "the release of a hold must not also run the press action");
    }

    @Test void aHoldFiresOncePerPress() {
        Keys.Hold h = new Keys.Hold();
        h.down(0);
        assertEquals(Keys.Hold.HOLD, h.fire());
        assertEquals(Keys.Hold.NONE, h.fire());
    }

    @Test void repeatsAndSecondDownsWhileHeldAreIgnored() {
        Keys.Hold h = new Keys.Hold();
        assertEquals(Keys.Hold.IGNORE, h.down(1), "a key-repeat is never a press of its own");
        assertEquals(Keys.Hold.ARM, h.down(0));
        assertEquals(Keys.Hold.IGNORE, h.down(0), "a repeat the firmware sends without a repeat count");
        assertEquals(Keys.Hold.SHORT, h.up());
    }

    @Test void aReleaseOrATimerWithoutAPressDoesNothing() {
        Keys.Hold h = new Keys.Hold();
        assertEquals(Keys.Hold.NONE, h.up(), "a release whose press went to another screen");
        assertEquals(Keys.Hold.NONE, h.fire(), "a timer left over from a press that was already released");
    }

    @Test void resetForgetsAPressWhoseReleaseNeverCame() {
        Keys.Hold h = new Keys.Hold();
        h.down(0);
        h.reset();
        assertFalse(h.isDown());
        assertEquals(Keys.Hold.ARM, h.down(0), "the next press is a press again");
    }

    // ---- a fired trash timer is a hold, unless the camera says the key is already up
    @Test void theProbeDecidesWhenItCanAnswer() {
        assertTrue(Keys.trashHoldActs(true), "still down per the camera: a hold");
        assertFalse(Keys.trashHoldActs(false), "released per the camera: the key-up got lost, this was a press");
    }

    @Test void withoutTheProbeAFiredTimerIsAHold() {
        assertTrue(Keys.trashHoldActs(null), "the release would have cancelled the timer; the worst case is a question defaulting to Cancel");
    }

    // ---- the legend
    private static List<Integer> iconsOf(Keys.Hints hints) {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < hints.icons.length; i++) {
            result.add(hints.icons[i]);
            if (hints.alts[i] != Keys.I_NONE) {
                result.add(hints.alts[i]);
            }
        }
        return result;
    }

    private static List<Integer> actionsOf(Keys.Hints hints) {
        List<Integer> result = new ArrayList<Integer>();
        for (int action : hints.actions) {
            result.add(action);
        }
        return result;
    }

    @Test void everyRowIsWellFormed() {
        for (Keys.Caps caps : new Keys.Caps[] { Keys.Caps.UNKNOWN, NONE, FN }) {
            for (int mode : EVERY_MODE) {
                Keys.Hints h = Keys.hints(mode, caps);
                assertEquals(h.icons.length, h.alts.length, "mode " + mode);
                assertEquals(h.icons.length, h.actions.length, "mode " + mode);
                assertTrue(h.icons.length > 0, "mode " + mode);
                for (int i = 0; i < h.icons.length; i++) assertNotEquals(Keys.I_NONE, h.icons[i], "mode " + mode + " item " + i);
            }
        }
    }

    @Test void anUnknownOrBareBodyIsNeverShownFn() {
        for (Keys.Caps caps : new Keys.Caps[] { Keys.Caps.UNKNOWN, NONE }) {
            for (int mode : EVERY_MODE) assertFalse(iconsOf(Keys.hints(mode, caps)).contains(Keys.I_FN), "mode " + mode);
        }
    }

    @Test void theMainPanelNamesEveryFunctionOnUniversalKeysWithExitLast() {
        for (int mode : new int[] { Keys.H_RECIPE, Keys.H_CHIPS }) {
            for (Keys.Caps caps : new Keys.Caps[] { NONE, FN }) {
                Keys.Hints h = Keys.hints(mode, caps);
                List<Integer> labels = actionsOf(h);
                for (int action : new int[] { Keys.HINT_HIDE, Keys.HINT_MENU_HOLD, Keys.HINT_EXIT }) assertTrue(labels.contains(action), mode + ": " + action);
                assertEquals(Keys.I_TRASH, h.icons[labels.indexOf(Keys.HINT_HIDE)]);
                assertEquals(Keys.I_NONE, h.alts[labels.indexOf(Keys.HINT_HIDE)], "trash alone hides: AEL is not bound");
                assertEquals(Keys.I_MENU, h.icons[labels.indexOf(Keys.HINT_MENU_HOLD)]);
                assertEquals(Keys.HINT_EXIT, h.actions[h.actions.length - 1], mode + ": exit sits at the right end");
                assertEquals(Keys.I_MENU, h.icons[h.icons.length - 1]);
            }
        }
        List<Integer> recipe = actionsOf(Keys.hints(Keys.H_RECIPE, NONE));
        assertTrue(recipe.contains(Keys.HINT_PICK));
        assertTrue(recipe.contains(Keys.HINT_FAVOURITE_HOLD));
    }

    @Test void theRecipeLineReadsPickBrowseFavMenuHideExit() {
        assertEquals(Arrays.asList(Keys.HINT_PICK, Keys.HINT_BROWSE, Keys.HINT_FAVOURITE_HOLD, Keys.HINT_MENU_HOLD, Keys.HINT_HIDE, Keys.HINT_EXIT), actionsOf(Keys.hints(Keys.H_RECIPE, FN)));
        assertEquals(Arrays.asList(Keys.HINT_PICK, Keys.HINT_FAVOURITE_HOLD, Keys.HINT_MENU_HOLD, Keys.HINT_HIDE, Keys.HINT_EXIT), actionsOf(Keys.hints(Keys.H_RECIPE, NONE)),
                "without Fn, browse is in the app menu, and the rest keep their order");
        assertEquals(Arrays.asList(Keys.HINT_EDIT, Keys.HINT_BROWSE, Keys.HINT_MENU_HOLD, Keys.HINT_HIDE, Keys.HINT_EXIT), actionsOf(Keys.hints(Keys.H_CHIPS, FN)));
    }

    @Test void fnJoinsTheLegendWhenTheBodyHasIt() {
        for (int mode : new int[] { Keys.H_RECIPE, Keys.H_CHIPS }) {
            Keys.Hints h = Keys.hints(mode, FN);
            assertEquals(Keys.I_FN, h.icons[actionsOf(h).indexOf(Keys.HINT_BROWSE)]);
            assertFalse(actionsOf(Keys.hints(mode, NONE)).contains(Keys.HINT_BROWSE), "browse is in the app menu without Fn");
        }
    }

    @Test void menuLegendsSayWhereMenuGoesAndWhenLeftRightChangeAValue() {
        List<Integer> top = actionsOf(Keys.hints(Keys.H_MENU_TOP, NONE)), sub = actionsOf(Keys.hints(Keys.H_MENU_SUB, NONE));
        assertTrue(top.contains(Keys.HINT_CLOSE), "MENU closes the app menu");
        assertTrue(sub.contains(Keys.HINT_BACK), "MENU goes back from the developer menu to the app menu");
        assertFalse(sub.contains(Keys.HINT_CLOSE));
        assertFalse(top.contains(Keys.HINT_CHANGE), "no value on the row, nothing for left / right to change");
        for (int mode : new int[] { Keys.H_MENU_TOP_VALUE, Keys.H_MENU_SUB_VALUE }) {
            Keys.Hints h = Keys.hints(mode, NONE);
            int change = actionsOf(h).indexOf(Keys.HINT_CHANGE);
            assertEquals(Keys.I_LEFTRIGHT, h.icons[change], "mode " + mode);
        }
        assertTrue(actionsOf(Keys.hints(Keys.H_MENU_TOP_VALUE, NONE)).contains(Keys.HINT_CLOSE));
        assertTrue(actionsOf(Keys.hints(Keys.H_MENU_SUB_VALUE, NONE)).contains(Keys.HINT_BACK));
        assertEquals(Arrays.asList(Keys.HINT_BACK), actionsOf(Keys.hints(Keys.H_PAGE, NONE)));
        assertEquals(Arrays.asList(Keys.HINT_EXIT_HOLD), actionsOf(Keys.hints(Keys.H_LOGGER, NONE)), "a short MENU is logged, not obeyed");
    }

    @Test void theBrowserClosesOnMenuWithFnBesideItWhenPresent() {
        for (int mode : new int[] { Keys.H_BRANDS, Keys.H_RECIPES }) {
            Keys.Hints bare = Keys.hints(mode, NONE), full = Keys.hints(mode, FN);
            int close = actionsOf(bare).indexOf(Keys.HINT_CLOSE);
            assertEquals(Keys.I_MENU, bare.icons[close]);
            assertEquals(Keys.I_NONE, bare.alts[close]);
            assertEquals(Keys.I_FN, full.alts[close]);
        }
    }

    // ---- scan codes
    @Test void theGesturesKeysAreOneShotAndTheWalkingKeysAreNot() {
        for (int k : new int[] { Keys.K_ENTER, Keys.K_MENU, Keys.K_SK1, Keys.K_DELETE, Keys.K_SK2, Keys.K_FN }) assertTrue(Keys.oneShot(k), "" + k);
        for (int k : new int[] { Keys.K_UP, Keys.K_DOWN, Keys.K_LEFT, Keys.K_RIGHT, Keys.K_WHEEL_CW, Keys.K_DIAL_CCW }) assertFalse(Keys.oneShot(k), "" + k);
    }

    @Test void theSoftKeysStandForMenuAndTrash() {
        assertTrue(Keys.isMenu(Keys.K_SK1));
        assertTrue(Keys.isTrash(Keys.K_SK2));
        assertFalse(Keys.isTrash(Keys.K_MENU));
    }

    @Test void theLoggerNamesTheCodesAReportWillAskAbout() {
        assertEquals("DELETE", Keys.name(595));
        assertEquals("DISP", Keys.name(608));
        assertEquals("MOVIE", Keys.name(515));
        assertEquals("ZOOM_T", Keys.name(610));
        assertEquals("AEL_AFMF", Keys.name(638));
        assertEquals("", Keys.name(9999), "an unknown code has no name; the logger shows its number");
    }

}
