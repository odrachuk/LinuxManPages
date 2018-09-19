/*******************************************************************************
 * Created by Oleksandr Drachuk on 6/7/15.
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

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.softsandr.man.db.table.ManTableRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The class used for
 */
public class PrefFavoritesUtils {
    private static final String CLASS_NAME = PrefFavoritesUtils.class.getName();
    private static final String FAVORITES_PREFERENCES = CLASS_NAME + ".FAVORITES_PREFERENCES";
    private static final String FAVORITES_COMMANDS_PREF_KEY = CLASS_NAME + ".FAVORITES_COMMANDS_PREF_KEY";
    private static final int MAX_ENTRIES = 70;

    private PrefFavoritesUtils() {}

    public static void saveFavorites(Context context, List<ManTableRecord> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(FAVORITES_PREFERENCES, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES_COMMANDS_PREF_KEY, jsonFavorites);

        editor.apply();
    }

    public static void addFavorite(Context context, ManTableRecord record) {
        List<ManTableRecord> favorites = getFavorites(context);
        if (favorites == null) {
            favorites = new ArrayList<ManTableRecord>();
        }
        if (!favorites.contains(record)) {
            if (favorites.size() == MAX_ENTRIES) {
                favorites.remove(0);
            }
            favorites.add(record);
            saveFavorites(context, favorites);
        }
    }

    public static void clearFavorites(Context context) {
        saveFavorites(context, new ArrayList<ManTableRecord>());
    }

    public static void removeFavorite(Context context, ManTableRecord record) {
        ArrayList<ManTableRecord> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(record);
            saveFavorites(context, favorites);
        }
    }

    public static ArrayList<ManTableRecord> getFavorites(Context context) {
        SharedPreferences settings;
        List<ManTableRecord> favorites;

        settings = context.getSharedPreferences(FAVORITES_PREFERENCES, Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES_COMMANDS_PREF_KEY)) {
            String jsonFavorites = settings.getString(FAVORITES_COMMANDS_PREF_KEY, null);
            Gson gson = new Gson();
            ManTableRecord[] favoriteItems = gson.fromJson(jsonFavorites, ManTableRecord[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<ManTableRecord>(favorites);
        } else {
            favorites = new ArrayList<ManTableRecord>();
        }

        return (ArrayList<ManTableRecord>) favorites;
    }
}
