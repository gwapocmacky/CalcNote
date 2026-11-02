package com.example.calcnote;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static final String NAME = "calcnote_prefs";
    private final SharedPreferences prefs;

    public Prefs(Context context) {
        prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public boolean darkMode() { return prefs.getBoolean("dark_mode", false); }
    public void setDarkMode(boolean v) { prefs.edit().putBoolean("dark_mode", v).apply(); }

    public int appColor() { return prefs.getInt("app_color_hex", 0xff6366f1); }
    public void setAppColor(int v) { prefs.edit().putInt("app_color_hex", v).apply(); }

    public long lastDraftId() { return prefs.getLong("last_draft_id", -1); }
    public void setLastDraftId(long v) { prefs.edit().putLong("last_draft_id", v).apply(); }

    public boolean mrpShown() { return prefs.getBoolean("mrp_shown_session", false); }
    public void setMrpShown(boolean v) { prefs.edit().putBoolean("mrp_shown_session", v).apply(); }
}
