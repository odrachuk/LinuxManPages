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
public class PrefHistoryUtils {
    private static final String CLASS_NAME = PrefHistoryUtils.class.getName();
    private static final String HISTORY_PREFERENCES = CLASS_NAME + ".HISTORY_PREFERENCES";
    private static final String HISTORY_COMMANDS_PREF_KEY = CLASS_NAME + ".HISTORY_COMMANDS_PREF_KEY";
    private static final int MAX_ENTRIES =70;

    private PrefHistoryUtils() {}

    public static void saveHistoryCommand(Context context, List<ManTableRecord> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(HISTORY_PREFERENCES, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(HISTORY_COMMANDS_PREF_KEY, jsonFavorites);

        editor.apply();
    }

    public static void addHistoryCommand(Context context, ManTableRecord record) {
        List<ManTableRecord> histories = getHistoryCommands(context);
        if (histories == null) {
            histories = new ArrayList<ManTableRecord>();
        }
        if (!histories.contains(record)) {
            if (histories.size() == MAX_ENTRIES) {
                histories.remove(0);
            }
            histories.add(record);
            saveHistoryCommand(context, histories);
        }
    }

    public static void clearHistoryCommands(Context context) {
        saveHistoryCommand(context, new ArrayList<ManTableRecord>());
    }

    public static void removeHistoryCommand(Context context, ManTableRecord record) {
        ArrayList<ManTableRecord> histories = getHistoryCommands(context);
        if (histories != null) {
            histories.remove(record);
            saveHistoryCommand(context, histories);
        }
    }

    public static ArrayList<ManTableRecord> getHistoryCommands(Context context) {
        SharedPreferences settings;
        List<ManTableRecord> histories;

        settings = context.getSharedPreferences(HISTORY_PREFERENCES, Context.MODE_PRIVATE);

        if (settings.contains(HISTORY_COMMANDS_PREF_KEY)) {
            String jsonHistories = settings.getString(HISTORY_COMMANDS_PREF_KEY, null);
            Gson gson = new Gson();
            ManTableRecord[] historyItems = gson.fromJson(jsonHistories, ManTableRecord[].class);

            histories = Arrays.asList(historyItems);
            histories = new ArrayList<ManTableRecord>(histories);
        } else {
            histories = new ArrayList<ManTableRecord>();
        }

        return (ArrayList<ManTableRecord>) histories;
    }
}
