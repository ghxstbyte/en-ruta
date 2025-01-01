package com.arr.enruta.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

public class Theme {

    public static boolean isDarkModeEnabled(Resources resources) {
        int nightModeFlags = resources.getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
}
