/*******************************************************************************
 * Created by Oleksandr Drachuk on 15/05/2014.
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
package com.softsandr.man.db.utils;

import android.annotation.TargetApi;
import android.os.Build;
import com.softsandr.man.db.ManSQLiteOpenHelper;
import com.softsandr.man.db.ResponseListener;
import com.softsandr.man.db.table.ManTable;
import com.softsandr.man.db.table.ManTableRecord;
import com.softsandr.man.db.utils.tasks.DSAllCommandsTask;
import com.softsandr.man.db.utils.tasks.DSCommandsByStartSymbolTask;
import com.softsandr.man.db.utils.tasks.DSSearchCommandsByStartSymbolTask;

import java.util.logging.Logger;

/**
 * This class used for managements SQL operations that relate to specific man table
 */
public class ManSQLiteUtil {
    private static final String LOG_TAG = ManSQLiteUtil.class.getName();
    private static final Logger LOGGER = Logger.getLogger(LOG_TAG);
    private final ManSQLiteOpenHelper dbHelper;

    public ManSQLiteUtil(ManSQLiteOpenHelper databaseHelper) {
        dbHelper = databaseHelper;
    }

    /**
     * Should be executed in background task as this operation can be long executed.
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void getAllRecordsAsync(ResponseListener<ManTableRecord> responseListener, ManTable manTable) {
        LOGGER.info("Table: " + manTable);
        new DSAllCommandsTask(dbHelper, responseListener, manTable).execute();
    }

    /**
     * Should be executed in background task as this operation can be long executed.
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void getRecordsBySymbolAsync(ResponseListener<ManTableRecord> responseListener,
                                        ManTable manTable, String startSymbol) {
        LOGGER.info("Table: " + manTable.getName() + " Symbol: " + startSymbol);
        new DSCommandsByStartSymbolTask(dbHelper, responseListener, manTable, startSymbol).execute();
    }

    /**
     * Call this method when not know in what section command can be
     * @param responseListener  listener of response
     * @param startSymbol       text used as search pattern
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void searchRecordsBySymbolAsync(ResponseListener<ManTableRecord> responseListener, String startSymbol) {
        LOGGER.info("Symbol: " + startSymbol);
        new DSSearchCommandsByStartSymbolTask(dbHelper, responseListener, startSymbol).execute();
    }
}
