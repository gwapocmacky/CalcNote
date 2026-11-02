package com.example.calcnote;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;

public class UiUtils {
    public static boolean isDarkMode(Context context) {
        return new Prefs(context).darkMode();
    }

    public static int getColor(Context context, int resId) {
        boolean dark = isDarkMode(context);
        if (dark) {
            switch (resId) {
                case R.color.background: return 0xff475569;
                case R.color.surface: return 0xff586579;
                case R.color.surface_2: return 0xff738293;
                case R.color.foreground: return 0xffe2e8f0;
                case R.color.foreground_soft: return 0xffa8b2c1;
                case R.color.muted: return 0xff7a889a;
                case R.color.accent: return new Prefs(context).appColor();
                case R.color.accent_dark: return new Prefs(context).appColor();
                case R.color.accent_soft: return 0xff4c51bf;
                case R.color.success: return 0xff34d399;
                case R.color.danger: return 0xfff87171;
                case R.color.white: return 0xffffffff;
                case R.color.divider: return 0xff475569;
                case R.color.inline_result: return 0xff94a3b8;
            }
        }
        if (resId == R.color.accent || resId == R.color.accent_dark) {
            return new Prefs(context).appColor();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(resId);
        }
        return context.getResources().getColor(resId);
    }

    public static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static GradientDrawable roundedBg(int color, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setColor(color);
        d.setCornerRadius(radiusDp);
        return d;
    }

    public static GradientDrawable roundedStrokeBg(int color, int radiusDp, int strokeWidth, int strokeColor) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setColor(color);
        d.setCornerRadius(radiusDp);
        d.setStroke(strokeWidth, strokeColor);
        return d;
    }

    public static int adjustAlpha(int color, float alpha) {
        return (color & 0x00ffffff) | ((int)(alpha * 255) << 24);
    }
}
