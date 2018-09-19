/*******************************************************************************
 * Created by o.drachuk on 11/06/2014. 
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
package com.softsandr.man.app;

import android.app.Application;
import android.preference.PreferenceManager;
import com.softsandr.man.R;
import com.softsandr.man.db.ManSQLiteOpenHelper;
import com.softsandr.man.util.PrefUtils;
import com.softsandr.man.util.FixedAndroidHandler;
import com.softsandr.man.util.LoggingUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class used for...
 */
public class ManApplication extends Application {
    private static final String LOG_TAG = ManApplication.class.getName();
    private static final Logger LOGGER = Logger.getLogger(ManApplication.class.getName());

    /**
     * Set up static context of app.
     */
    static {
        if (AppContext.APP_LOGGING) {
            // enabling logging by fixing the logging integration between java.util.logging and Android internal logging
            LoggingUtil.resetRootHandler(new FixedAndroidHandler());
            Logger.getLogger("").setLevel(Level.WARNING);
            Logger.getLogger("com.softsandr.man").setLevel(Level.ALL);
        } else {
            // disable all application's logging
            Logger.getLogger("").setLevel(Level.OFF);
            Logger.getAnonymousLogger().setLevel(Level.OFF);
        }
    }

    private final ConcurrentMap<String, Object> localServices = new ConcurrentHashMap<String, Object>();

    @Override
    public void onCreate() {
        LOGGER.entering(LOG_TAG, "onCreate");
    }

    @Override
    public Object getSystemService(String name) {
        if (localServices.containsKey(name)) {
            return localServices.get(name);
        } else {
            return super.getSystemService(name);
        }
    }

    /**
     * Should be call for configuration specific aspects of app on start
     */
    public void onApplicationStart() {
        LOGGER.entering(LOG_TAG, "onApplicationStart");
        initDatabase();
        initDefaultPreference();
    }

    /**
     * Should be call for erasing all configuration's aspects
     */
    public void onApplicationStop() {
        LOGGER.entering(LOG_TAG, "onApplicationStop");
        ManSQLiteOpenHelper databaseHelper = (ManSQLiteOpenHelper) getSystemService(AppContext.MAN_DATABASE_HELPER_SERVICE);
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    /**
     * This calls copy SQLite database to project directory
     */
    private void initDatabase() {
        localServices.putIfAbsent(AppContext.MAN_DATABASE_HELPER_SERVICE, new ManSQLiteOpenHelper(this));
    }

    /**
     * This populates the default values from the preferences XML file and set custom predefined values
     */
    private void initDefaultPreference() {
        PreferenceManager.setDefaultValues(this, R.xml.application_preferences, false);
        PrefUtils.initDefault(PreferenceManager.getDefaultSharedPreferences(this));
    }
}
