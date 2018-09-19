/*******************************************************************************
 * Created by o.drachuk on 10/01/2014.
 *
 * Copyright Oleksandr Drachuk.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.softsandr.man.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.softsandr.man.R;

/**
 * This class make entry point for all {@link java.util.prefs.Preferences} used in App
 */
public final class PrefUtils {
    private static final String LOG_NAME = PrefUtils.class.getSimpleName();

    /**
     * This class used as utility class and can't be instantiated
     */
    private PrefUtils() {
    }

    /**
     * Init application preferences when start application or when needs restore to default settings
     *
     * @param preferences the application default {@link android.content.SharedPreferences}
     */
    public static void initDefault(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.apply();
    }

    public static String loadAppTheme(Context context, SharedPreferences preferences) {
        String[] themes = context.getResources().getStringArray(R.array.theme_pref_list_values);
        return preferences.getString(context.getString(R.string.pref_app_select_theme_key), themes[0]);
    }
}
