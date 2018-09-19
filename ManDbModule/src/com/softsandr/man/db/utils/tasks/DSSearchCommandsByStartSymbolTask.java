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
package com.softsandr.man.db.utils.tasks;

import android.database.Cursor;
import com.softsandr.man.db.ManSQLiteOpenHelper;
import com.softsandr.man.db.ResponseListener;
import com.softsandr.man.db.table.ManTable;
import com.softsandr.man.db.table.ManTableRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class represent {@link android.os.AsyncTask} for retrieve all records that starts with certain symbol
 * from all tables - database
 */
public class DSSearchCommandsByStartSymbolTask extends DataSourceAsyncTask {
    private static final String LOG_TAG = DSSearchCommandsByStartSymbolTask.class.getName();
    private static final Logger LOGGER = Logger.getLogger(LOG_TAG);

    private final String startSymbol;

    public DSSearchCommandsByStartSymbolTask(ManSQLiteOpenHelper dbHelper, ResponseListener<ManTableRecord> responseListener,
                                             String startSymbol) {
        super(dbHelper, responseListener);
        this.startSymbol = startSymbol;
    }

    @Override
    protected List<ManTableRecord> doInBackground(Object... params) {
        List<ManTableRecord> commands = new ArrayList<ManTableRecord>();
        if (waitDb()) {
            Cursor cursor = null;
            try {
                for (int i = 1; i < 10; i++) {
                    cursor = dbHelper.getDatabase().query(
                            ManTable.getBySection(i).getName(), // table name
                            allColumns, // select columns
                            ManTable.COLUMN_NAME + " LIKE ?", // selection condition
                            new String[]{startSymbol + "%"}, // condition arguments
                            null, null, ManTable.COLUMN_NAME); // sorted by column
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        ManTableRecord command = cursorToComment(cursor, i);
                        commands.add(command);
                        cursor.moveToNext();
                    }
                }
            } catch (Exception e) {
                LOGGER.throwing(LOG_TAG, "doInBackground", e);
            } finally {
                if (cursor != null) {
                    // make sure to close the cursor
                    cursor.close();
                }
            }
        }
        return commands;
    }
}
