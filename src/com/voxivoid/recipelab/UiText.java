package com.voxivoid.recipelab;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Localized display formatting only.
 *
 * CameraEx keys, persisted recipe identity, settings IDs, codecs and diagnostic file formats stay
 * in the camera-free core classes. This class is part of the Android presentation layer and uses
 * compile-time resource IDs directly.
 */
final class UiText {
    private static final String SEGMENT_SEPARATOR = "  \u00b7  ";
    private static final String LIST_SEPARATOR = ", ";
    private static final String EMPTY_VALUE = "-";
    private static final String UNKNOWN_PREFIX = "?";
    private static final String KELVIN_SUFFIX = "K";
    private static final String AMBER_PREFIX = "A";
    private static final String BLUE_PREFIX = "B";
    private static final String GREEN_PREFIX = "G";
    private static final String MAGENTA_PREFIX = "M";
    private static final String SLOT_ID_FORMAT = "%08x";

    private static final class DisplayNameSpec {
        final String canonical;
        final int resourceId;

        DisplayNameSpec(String canonical, int resourceId) {
            this.canonical = canonical;
            this.resourceId = resourceId;
        }
    }

    private static final DisplayNameSpec[] GROUP_NAMES = {
        new DisplayNameSpec("Sony", R.string.group_sony),
        new DisplayNameSpec("Fuji Sim", R.string.group_fuji_sim),
        new DisplayNameSpec("Fuji Film", R.string.group_fuji_film),
        new DisplayNameSpec("Kodak", R.string.group_kodak),
        new DisplayNameSpec("Cine", R.string.group_cine),
        new DisplayNameSpec("Ricoh GR", R.string.group_ricoh_gr),
        new DisplayNameSpec("Leica", R.string.group_leica),
        new DisplayNameSpec("Hasselblad", R.string.group_hasselblad),
        new DisplayNameSpec("Canon / Nikon", R.string.group_canon_nikon),
        new DisplayNameSpec("Pana / Olympus", R.string.group_pana_olympus),
        new DisplayNameSpec("Other Stocks", R.string.group_other_stocks),
        new DisplayNameSpec("Ilford", R.string.group_ilford)
    };

    // Keep this table aligned with Recipes.ALL. The static verifier makes an omitted/reordered
    // entry fail immediately instead of silently displaying the wrong localized recipe name.
    private static final DisplayNameSpec[] RECIPE_NAMES = {
        new DisplayNameSpec("FACTORY (ST)", R.string.recipe_factory_st),
        new DisplayNameSpec("Sony PT (portrait)", R.string.recipe_sony_pt_portrait),
        new DisplayNameSpec("Sony NT (neutral)", R.string.recipe_sony_nt_neutral),
        new DisplayNameSpec("Sony VV (vivid)", R.string.recipe_sony_vv_vivid),
        new DisplayNameSpec("Sony VV2", R.string.recipe_sony_vv2),
        new DisplayNameSpec("Sony FL (film-like)", R.string.recipe_sony_fl_film_like),
        new DisplayNameSpec("Sony IN (instant)", R.string.recipe_sony_in_instant),
        new DisplayNameSpec("Sony SH (soft high-key)", R.string.recipe_sony_sh_soft_high_key),
        new DisplayNameSpec("Provia", R.string.recipe_provia),
        new DisplayNameSpec("Velvia", R.string.recipe_velvia),
        new DisplayNameSpec("Astia", R.string.recipe_astia),
        new DisplayNameSpec("Classic Chrome", R.string.recipe_classic_chrome),
        new DisplayNameSpec("Classic Negative", R.string.recipe_classic_negative),
        new DisplayNameSpec("Nostalgic Neg", R.string.recipe_nostalgic_neg),
        new DisplayNameSpec("Reala Ace", R.string.recipe_reala_ace),
        new DisplayNameSpec("Pro Neg Std", R.string.recipe_pro_neg_std),
        new DisplayNameSpec("Pro Neg Hi", R.string.recipe_pro_neg_hi),
        new DisplayNameSpec("Eterna", R.string.recipe_eterna),
        new DisplayNameSpec("Eterna Bleach Bypass", R.string.recipe_eterna_bleach_bypass),
        new DisplayNameSpec("Acros", R.string.recipe_acros),
        new DisplayNameSpec("Acros +Ye (yellow filter)", R.string.recipe_acros_plus_ye_yellow_filter),
        new DisplayNameSpec("Acros +R (red filter)", R.string.recipe_acros_plus_r_red_filter),
        new DisplayNameSpec("Acros +G (green filter)", R.string.recipe_acros_plus_g_green_filter),
        new DisplayNameSpec("Sepia", R.string.recipe_sepia),
        new DisplayNameSpec("Fuji Pro 400H", R.string.recipe_fuji_pro_400h),
        new DisplayNameSpec("Fuji Fortia 50", R.string.recipe_fuji_fortia_50),
        new DisplayNameSpec("Fuji Superia 400", R.string.recipe_fuji_superia_400),
        new DisplayNameSpec("Fuji C200", R.string.recipe_fuji_c200),
        new DisplayNameSpec("Fuji Natura 1600", R.string.recipe_fuji_natura_1600),
        new DisplayNameSpec("Kodak Portra 160", R.string.recipe_kodak_portra_160),
        new DisplayNameSpec("Kodak Portra 400", R.string.recipe_kodak_portra_400),
        new DisplayNameSpec("Kodak Portra 800", R.string.recipe_kodak_portra_800),
        new DisplayNameSpec("Kodak Gold 200", R.string.recipe_kodak_gold_200),
        new DisplayNameSpec("Kodak Ultra Max 400", R.string.recipe_kodak_ultra_max_400),
        new DisplayNameSpec("Kodak Color Plus 200", R.string.recipe_kodak_color_plus_200),
        new DisplayNameSpec("Kodak Ektar 100", R.string.recipe_kodak_ektar_100),
        new DisplayNameSpec("Kodak Ektachrome E100", R.string.recipe_kodak_ektachrome_e100),
        new DisplayNameSpec("Kodachrome 64", R.string.recipe_kodachrome_64),
        new DisplayNameSpec("Kodak Vision3 500T (daylight)", R.string.recipe_kodak_vision3_500t_daylight),
        new DisplayNameSpec("Kodak Vision 200T (Asteroid City)", R.string.recipe_kodak_vision_200t_asteroid_city),
        new DisplayNameSpec("Kodak Tri-X 400", R.string.recipe_kodak_tri_x_400),
        new DisplayNameSpec("Kodak T-Max", R.string.recipe_kodak_t_max),
        new DisplayNameSpec("Kodak Tri-X 1600 (pushed)", R.string.recipe_kodak_tri_x_1600_pushed),
        new DisplayNameSpec("Cinestill 50D (Blue Velvet)", R.string.recipe_cinestill_50d_blue_velvet),
        new DisplayNameSpec("Cinestill 800T", R.string.recipe_cinestill_800t),
        new DisplayNameSpec("Classic Cinema", R.string.recipe_classic_cinema),
        new DisplayNameSpec("Rec709 Video (flat-ish)", R.string.recipe_rec709_video_flat_ish),
        new DisplayNameSpec("GR Positive Film", R.string.recipe_gr_positive_film),
        new DisplayNameSpec("GR Negative Film", R.string.recipe_gr_negative_film),
        new DisplayNameSpec("GR Bleach Bypass", R.string.recipe_gr_bleach_bypass),
        new DisplayNameSpec("GR Retro", R.string.recipe_gr_retro),
        new DisplayNameSpec("GR Cross Process", R.string.recipe_gr_cross_process),
        new DisplayNameSpec("GR Hi-Contrast B&W", R.string.recipe_gr_hi_contrast_b_and_w),
        new DisplayNameSpec("GR Hard Monotone", R.string.recipe_gr_hard_monotone),
        new DisplayNameSpec("GR Soft Monotone", R.string.recipe_gr_soft_monotone),
        new DisplayNameSpec("Leica Contemporary", R.string.recipe_leica_contemporary),
        new DisplayNameSpec("Leica Classic", R.string.recipe_leica_classic),
        new DisplayNameSpec("Leica Eternal", R.string.recipe_leica_eternal),
        new DisplayNameSpec("Leica Monochrom", R.string.recipe_leica_monochrom),
        new DisplayNameSpec("Hasselblad HNCS Natural", R.string.recipe_hasselblad_hncs_natural),
        new DisplayNameSpec("Canon Standard", R.string.recipe_canon_standard),
        new DisplayNameSpec("Canon Portrait", R.string.recipe_canon_portrait),
        new DisplayNameSpec("Canon Faithful", R.string.recipe_canon_faithful),
        new DisplayNameSpec("Nikon Flat", R.string.recipe_nikon_flat),
        new DisplayNameSpec("Nikon Vivid", R.string.recipe_nikon_vivid),
        new DisplayNameSpec("Pana L.Monochrome D", R.string.recipe_pana_l_monochrome_d),
        new DisplayNameSpec("Pana L.ClassicNeo", R.string.recipe_pana_l_classicneo),
        new DisplayNameSpec("Olympus Pop Art", R.string.recipe_olympus_pop_art),
        new DisplayNameSpec("Olympus Pale & Light", R.string.recipe_olympus_pale_and_light),
        new DisplayNameSpec("Agfa Vista 200", R.string.recipe_agfa_vista_200),
        new DisplayNameSpec("Agfa Ultra 100", R.string.recipe_agfa_ultra_100),
        new DisplayNameSpec("Polaroid / Instax", R.string.recipe_polaroid_instax),
        new DisplayNameSpec("Ilford HP5", R.string.recipe_ilford_hp5),
        new DisplayNameSpec("Ilford FP4", R.string.recipe_ilford_fp4),
        new DisplayNameSpec("Ilford Delta 100", R.string.recipe_ilford_delta_100),
        new DisplayNameSpec("Ilford Delta 3200", R.string.recipe_ilford_delta_3200),
        new DisplayNameSpec("Ilford Pan F 50", R.string.recipe_ilford_pan_f_50)
    };

    static {
        verifyDisplayNames();
    }

    private static void verifyDisplayNames() {
        if (GROUP_NAMES.length != Recipes.GROUPS.length) {
            throw new IllegalStateException("group display-name table does not match Recipes.GROUPS");
        }
        for (int i = 0; i < GROUP_NAMES.length; i++) {
            if (!GROUP_NAMES[i].canonical.equals(Recipes.GROUPS[i])) {
                throw new IllegalStateException("group display-name table is out of order at " + i);
            }
        }
        if (RECIPE_NAMES.length != Recipes.ALL.length) {
            throw new IllegalStateException("recipe display-name table does not match Recipes.ALL");
        }
        for (int i = 0; i < RECIPE_NAMES.length; i++) {
            if (!RECIPE_NAMES[i].canonical.equals(Recipes.ALL[i].name)) {
                throw new IllegalStateException("recipe display-name table is out of order at " + i);
            }
        }
    }

    private final Resources resources;

    UiText(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        resources = context.getResources();
        verifyResourceTables();
    }

    private void verifyResourceTables() {
        if (resources.getStringArray(R.array.ui_row_labels).length != Params.N
                || resources.getStringArray(R.array.ui_style_labels).length != Recipes.STYLE_LABEL.length
                || resources.getStringArray(R.array.ui_effect_labels).length != Recipes.PE_LABEL.length
                || resources.getStringArray(R.array.ui_quality_labels).length != Params.Q_LABEL.length) {
            throw new IllegalStateException("UI resource tables do not match the core tables");
        }
    }

    String text(int resourceId) {
        return resources.getString(resourceId);
    }

    String text(int resourceId, Object... args) {
        return resources.getString(resourceId, args);
    }

    private String quantity(int resourceId, int quantity, Object... args) {
        return resources.getQuantityString(resourceId, quantity, args);
    }

    private String label(int arrayResourceId, int index, String fallback) {
        String[] labels = resources.getStringArray(arrayResourceId);
        return index >= 0 && index < labels.length ? labels[index] : fallback;
    }

    String picked(int count) {
        return quantity(R.plurals.picked_values, count, count);
    }

    String rowLabel(int row) {
        return label(R.array.ui_row_labels, row, UNKNOWN_PREFIX + row);
    }

    String style(int index) {
        if (!Recipes.styleKnown(index)) {
            return Recipes.styleLabel(index);
        }
        return label(R.array.ui_style_labels, index, Recipes.styleLabel(index));
    }

    String effect(int index) {
        return label(R.array.ui_effect_labels, index, Recipes.peLabel(index));
    }

    String quality(int index) {
        return label(R.array.ui_quality_labels, index, UNKNOWN_PREFIX + index);
    }

    String group(int index) {
        if (index == Favourites.GROUP) {
            return text(R.string.favourites);
        }
        if (index < 0 || index >= GROUP_NAMES.length) {
            return UNKNOWN_PREFIX + index;
        }
        return text(GROUP_NAMES[index].resourceId);
    }

    String groupTitle(int index) {
        return group(index).toUpperCase(Locale.getDefault());
    }

    String recipeName(int index) {
        if (index < 0 || index >= RECIPE_NAMES.length) {
            return UNKNOWN_PREFIX + index;
        }
        return text(RECIPE_NAMES[index].resourceId);
    }

    String position(int index) {
        return index == Recipes.FACTORY ? text(R.string.position_factory) : Recipes.position(index);
    }

    String sub(int effect, int value) {
        String fallback = Recipes.subLabel(effect, value);
        if (fallback == null) {
            return null;
        }
        switch (effect) {
            case Recipes.PE_HIGHKEY:
                return label(R.array.ui_highkey_labels, value, fallback);
            case Recipes.PE_TOY:
                return label(R.array.ui_toy_labels, value, fallback);
            case Recipes.PE_PARTIAL:
                return label(R.array.ui_partial_labels, value, fallback);
            case Recipes.PE_POSTER:
                return label(R.array.ui_poster_labels, value, fallback);
            default:
                return fallback;
        }
    }

    String dro(int value) {
        if (value == Recipes.DRO_AUTO) {
            return text(R.string.auto);
        }
        if (value == Recipes.DRO_OFF) {
            return text(R.string.off);
        }
        return text(R.string.dro_level, value);
    }

    String fmt(int row, int value, int[] edit) {
        switch (row) {
            case Params.R_STYLE:
                return style(value);
            case Params.R_PP:
                return value == 0 ? text(R.string.off) : text(R.string.on);
            case Params.R_WBMODE:
                if (value == Params.WB_AUTO) {
                    return text(R.string.auto);
                }
                if (value == Params.WB_KELVIN) {
                    return text(R.string.kelvin);
                }
                return String.valueOf(value);
            case Params.R_PE:
                return effect(value);
            case Params.R_SUB:
                String sub = sub(edit[Params.R_PE], value);
                return sub == null ? EMPTY_VALUE : sub;
            case Params.R_DRO:
                return dro(value);
            case Params.R_QUAL:
                return quality(value);
            default:
                return Params.fmt(row, value, edit);
        }
    }

    private static String appendSegment(String first, String next) {
        return first.length() == 0 ? next : first + SEGMENT_SEPARATOR + next;
    }

    String metaLine(int[] current, int[] edit, String error) {
        String result;
        if (edit[Params.R_PE] != Recipes.PE_OFF) {
            String sub = sub(edit[Params.R_PE], edit[Params.R_SUB]);
            String effectName = effect(edit[Params.R_PE]) + (sub == null ? "" : " " + sub);
            result = text(R.string.effect_note, effectName);
        } else {
            result = style(edit[Params.R_STYLE]);
        }

        String whiteBalance;
        if (edit[Params.R_WBMODE] == Params.WB_KELVIN) {
            whiteBalance = (edit[Params.R_KELVIN] * 100) + KELVIN_SUFFIX;
        } else if (edit[Params.R_WBMODE] == Params.WB_AUTO) {
            whiteBalance = text(R.string.auto);
        } else {
            whiteBalance = text(R.string.wb_mode, edit[Params.R_WBMODE]);
        }
        result = appendSegment(result, text(R.string.wb_note, whiteBalance));

        if (edit[Params.R_EV] != 0) {
            result = appendSegment(result, text(R.string.ev_note, Recipes.evLabel(edit[Params.R_EV])));
        }
        if (edit[Params.R_DRO] != Recipes.DRO_AUTO) {
            result = appendSegment(result, text(R.string.dro_note, dro(edit[Params.R_DRO])));
        }
        if (edit[Params.R_QUAL] != current[Params.R_QUAL]) {
            result = appendSegment(
                    result,
                    text(
                            R.string.quality_change_note,
                            quality(edit[Params.R_QUAL]),
                            quality(current[Params.R_QUAL])));
        }
        if (edit[Params.R_PE] != Recipes.PE_OFF && edit[Params.R_QUAL] <= Params.Q_RAWJPG) {
            result = appendSegment(result, text(R.string.raw_effect_ignored));
        }
        if (error != null) {
            result = appendSegment(result, text(R.string.preview_failed, error));
        }
        return result;
    }

    String miniLine(int recipe, int[] current, int[] edit, boolean dirty) {
        String mode = text(
                edit[Params.R_PE] == Recipes.PE_OFF
                        ? R.string.token_creative_style
                        : R.string.token_picture_effect);
        String result = text(
                R.string.mini_line,
                mode,
                recipeName(recipe),
                recipe + 1,
                Recipes.ALL.length,
                text(dirty ? R.string.mini_preview : R.string.mini_active));
        if (edit[Params.R_QUAL] != current[Params.R_QUAL]) {
            result = appendSegment(
                    result,
                    text(R.string.mini_quality_note, quality(edit[Params.R_QUAL])));
        }
        return result;
    }

    String summary(Recipes.Recipe recipe) {
        String result;
        if (recipe.pe != Recipes.PE_OFF) {
            String sub = sub(recipe.pe, recipe.sub);
            result = effect(recipe.pe) + (sub == null ? "" : " " + sub);
        } else {
            result = style(recipe.style)
                    + "  "
                    + (recipe.sat > 0 ? "+" : "")
                    + recipe.sat
                    + "/"
                    + (recipe.con > 0 ? "+" : "")
                    + recipe.con;
        }
        if (recipe.ev != 0) {
            result += "  " + Recipes.evLabel(recipe.ev);
        }
        if (recipe.dro != Recipes.DRO_AUTO) {
            result += "  DRO " + dro(recipe.dro);
        }
        if (recipe.wbMode == Params.WB_KELVIN) {
            result += "  " + recipe.kelvin + KELVIN_SUFFIX;
        }
        if (recipe.ab != 0) {
            result += "  " + (recipe.ab > 0 ? AMBER_PREFIX + recipe.ab : BLUE_PREFIX + (-recipe.ab));
        }
        if (recipe.gm != 0) {
            result += "  " + (recipe.gm > 0 ? GREEN_PREFIX + recipe.gm : MAGENTA_PREFIX + (-recipe.gm));
        }
        return result;
    }

    String[] qualityPrompt(int[] current, int[] edit) {
        return new String[] {
            text(
                    R.string.quality_title,
                    quality(current[Params.R_QUAL]),
                    quality(edit[Params.R_QUAL])),
            text(
                    edit[Params.R_PE] != Recipes.PE_OFF
                            ? R.string.quality_effect_body
                            : R.string.quality_style_body)
        };
    }

    String[] qualityOptions() {
        return new String[] {text(R.string.accept), text(R.string.cancel)};
    }

    String[] resetOptions() {
        return new String[] {text(R.string.reset), text(R.string.cancel)};
    }

    String favourite(int recipe, boolean on) {
        return text(
                on ? R.string.favourite_added : R.string.favourite_removed,
                recipeName(recipe));
    }

    String appLabel(int row) {
        switch (row) {
            case DevTools.APP_BROWSE:
                return text(R.string.menu_browse);
            case DevTools.APP_PANEL:
                return text(R.string.menu_panel);
            case DevTools.APP_RESET:
                return text(R.string.menu_reset);
            case DevTools.APP_ABOUT:
                return text(R.string.menu_about);
            case DevTools.APP_DEV:
                return text(R.string.menu_developer);
            default:
                return UNKNOWN_PREFIX + row;
        }
    }

    String appDetail(int row) {
        switch (row) {
            case DevTools.APP_BROWSE:
                return text(R.string.detail_browse);
            case DevTools.APP_PANEL:
                return text(R.string.detail_panel);
            case DevTools.APP_RESET:
                return text(R.string.detail_reset);
            case DevTools.APP_ABOUT:
                return text(R.string.detail_about);
            case DevTools.APP_DEV:
                return text(R.string.detail_developer);
            default:
                return "";
        }
    }

    String appValue(int row, int overlay) {
        if (row != DevTools.APP_PANEL) {
            return null;
        }
        switch (overlay) {
            case Params.OV_FULL:
                return text(R.string.panel_full);
            case Params.OV_PILL:
                return text(R.string.panel_label);
            case Params.OV_HIDDEN:
                return text(R.string.panel_hidden);
            default:
                return UNKNOWN_PREFIX;
        }
    }

    String developerLabel(int row, boolean snapshotTaken) {
        switch (row) {
            case DevTools.ROW_SNAPSHOT:
                return text(snapshotTaken ? R.string.settings_diff : R.string.settings_snapshot);
            case DevTools.ROW_LOCKS:
                return text(R.string.read_only_check, Params.allSlots().size());
            case DevTools.ROW_SAMPLES:
                return text(R.string.shoot_samples, Recipes.ALL.length);
            case DevTools.ROW_SETTLE:
                return text(R.string.settle_delay);
            case DevTools.ROW_KEYS:
                return text(R.string.key_logger);
            default:
                return UNKNOWN_PREFIX + row;
        }
    }

    String developerDetail(int row, boolean snapshotTaken) {
        switch (row) {
            case DevTools.ROW_SNAPSHOT:
                return text(snapshotTaken ? R.string.detail_diff : R.string.detail_snapshot);
            case DevTools.ROW_LOCKS:
                return text(R.string.detail_read_only);
            case DevTools.ROW_SAMPLES:
                return text(R.string.detail_samples);
            case DevTools.ROW_SETTLE:
                return text(R.string.detail_settle);
            case DevTools.ROW_KEYS:
                return text(R.string.detail_keys);
            default:
                return "";
        }
    }

    String[][] about(String version, String model, String platform) {
        return new String[][] {
            {text(R.string.about_version), orUnknown(version)},
            {text(R.string.about_camera), orUnknown(model)},
            {text(R.string.about_platform), orUnknown(platform)},
            {text(R.string.about_source), DevTools.SOURCE_URL}
        };
    }

    private String orUnknown(String value) {
        return value == null || value.length() == 0 ? text(R.string.unknown) : value;
    }

    String logTitle(String model, String platform) {
        return text(R.string.logger_title, orUnknown(model), orUnknown(platform));
    }

    String progress(int frame, int total, int recipe) {
        return text(R.string.sample_progress, frame, total, recipeName(recipe));
    }

    String doneMessage(int shot, int total) {
        return text(R.string.sample_done, shot, total, DevTools.MANIFEST);
    }

    String stoppedMessage(int shot, int total) {
        return shot == 0
                ? text(R.string.sample_stopped_empty)
                : text(R.string.sample_stopped, shot, total, DevTools.MANIFEST);
    }

    String shootFailed(int frame, int shot, String error) {
        return text(R.string.sample_failed, frame, error, shot, DevTools.MANIFEST);
    }

    String slotName(int id) {
        int row = Params.rowForSlot(id);
        if (row < 0) {
            return String.format(Locale.US, SLOT_ID_FORMAT, id);
        }
        if (row != Params.R_SUB) {
            return rowLabel(row);
        }

        int effect = Params.subEffectForSlot(id);
        return effect >= 0
                ? text(R.string.sub_slot, effect(effect))
                : rowLabel(Params.R_SUB);
    }

    private String slotNames(List<Integer> ids) {
        Set<String> names = new LinkedHashSet<String>();
        for (int id : ids) {
            names.add(slotName(id));
        }
        StringBuilder result = new StringBuilder();
        for (String name : names) {
            if (result.length() > 0) {
                result.append(LIST_SEPARATOR);
            }
            result.append(name);
        }
        return result.toString();
    }

    String lockedMessage(List<Integer> ids) {
        return quantity(R.plurals.locked_message, ids.size(), slotNames(ids));
    }

    String writeFailedMessage(int id, String error, int written) {
        String hex = String.format(Locale.US, SLOT_ID_FORMAT, id);
        return written == 0
                ? text(R.string.write_failed_empty, slotName(id), hex, error)
                : quantity(
                        R.plurals.write_failed,
                        written,
                        slotName(id),
                        hex,
                        error,
                        written);
    }

    String lockReport(List<Integer> ids, int[] attrs) {
        List<Integer> locked = new ArrayList<Integer>();
        int unreadable = 0;
        for (int i = 0; i < ids.size(); i++) {
            if (attrs[i] < 0) {
                unreadable++;
            } else if (Params.slotLocked(attrs[i])) {
                locked.add(ids.get(i));
            }
        }

        String result = locked.isEmpty()
                ? text(R.string.locks_clear, ids.size())
                : text(R.string.locks_found, ids.size(), locked.size(), slotNames(locked));
        return unreadable == 0
                ? result
                : appendSegment(result, text(R.string.locks_unreadable, unreadable));
    }

    String hint(int action) {
        switch (action) {
            case Keys.HINT_PICK:
                return text(R.string.hint_pick);
            case Keys.HINT_BROWSE:
                return text(R.string.hint_browse);
            case Keys.HINT_FAVOURITE_HOLD:
                return text(R.string.hint_favourite_hold);
            case Keys.HINT_MENU_HOLD:
                return text(R.string.hint_menu_hold);
            case Keys.HINT_HIDE:
                return text(R.string.hint_hide);
            case Keys.HINT_EXIT:
                return text(R.string.hint_exit);
            case Keys.HINT_EDIT:
                return text(R.string.hint_edit);
            case Keys.HINT_DONE:
                return text(R.string.hint_done);
            case Keys.HINT_RECIPES:
                return text(R.string.hint_recipes);
            case Keys.HINT_CLOSE:
                return text(R.string.hint_close);
            case Keys.HINT_MOVE:
                return text(R.string.hint_move);
            case Keys.HINT_SELECT:
                return text(R.string.hint_select);
            case Keys.HINT_BACK:
                return text(R.string.hint_back);
            case Keys.HINT_CHANGE:
                return text(R.string.hint_change);
            case Keys.HINT_EXIT_HOLD:
                return text(R.string.hint_exit_hold);
            case Keys.HINT_CONFIRM:
                return text(R.string.hint_confirm);
            case Keys.HINT_CANCEL:
                return text(R.string.hint_cancel);
            default:
                throw new AssertionError("Unhandled hint action: " + action);
        }
    }
}
