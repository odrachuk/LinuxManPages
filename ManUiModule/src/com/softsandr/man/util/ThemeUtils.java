/*******************************************************************************
 * Created by Oleksandr Drachuk on 6/5/15.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.softsandr.man.util;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import com.softsandr.man.R;

/**
 * The class used for
 */
public final class ThemeUtils {

    private ThemeUtils() {
    }

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void changeTheme(Activity activity) {
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    /**
     * Set the theme of the activity, according to the configuration.
     */
    public static void onActivityCreateSetTheme(Activity activity) {
        String currentTheme = PrefUtils.loadAppTheme(activity, PreferenceManager.getDefaultSharedPreferences(activity));
        String[] allThemes = activity.getResources().getStringArray(R.array.theme_pref_list_values);
        if (currentTheme.equals(allThemes[0])) {
            activity.setTheme(R.style.Man_Dark);
        } else {
            activity.setTheme(R.style.Man_Light);
        }
    }
}
