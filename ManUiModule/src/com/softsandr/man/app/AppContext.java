/*******************************************************************************
 * Created by o.drachuk on 12/06/2014. 
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

import com.softsandr.man.db.ManSQLiteOpenHelper;

/**
 * This class declare public string variables that determine names for specific local services,
 * that are available from application class by getSystemService() method. Also class represent
 * application's global setup variables.
 */
public class AppContext {
    private static final String CLASS_NAME = AppContext.class.getSimpleName();

    /**
     * Determine or needs to apply logging in app logic.
     * If you want to disable logging in app just set false in this global variable.
     */
    public static boolean APP_LOGGING = true;

    /**
     * Single (unique) instance of common entry point to SQLite database
     * @see ManSQLiteOpenHelper
     */
    public static final String MAN_DATABASE_HELPER_SERVICE = CLASS_NAME + ".MAN_DATABASE_HELPER_SERVICE";
}
