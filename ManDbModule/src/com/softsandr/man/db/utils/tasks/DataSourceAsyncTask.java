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

import android.annotation.TargetApi;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import com.softsandr.man.db.ManSQLiteOpenHelper;
import com.softsandr.man.db.ResponseListener;
import com.softsandr.man.db.table.ManTable;
import com.softsandr.man.db.table.ManTableRecord;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class used for retrieving data from SQLite database in background process.
 * Class extends {@link AsyncTask}
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public abstract class DataSourceAsyncTask extends AsyncTask<Object, Object, List<ManTableRecord>> {
    protected final static String[] allColumns = {
            ManTable.COLUMN_ID,
            ManTable.COLUMN_NAME,
            ManTable.COLUMN_SYNOPSIS,
            ManTable.COLUMN_FILE};

    protected final ResponseListener<ManTableRecord> responseListener;
    protected final ManSQLiteOpenHelper dbHelper;

    public DataSourceAsyncTask(ManSQLiteOpenHelper dbHelper, ResponseListener<ManTableRecord> responseListener) {
        this.dbHelper = dbHelper;
        this.responseListener = responseListener;
    }

    protected boolean waitDb() {
        while (!dbHelper.isReady()) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                return false;
            }
            dbHelper.openDatabase();
        }
        return true;
    }

    /**
     * Used for parsing Cursor into ManTableRecord
     *
     * @param cursor  instance of {@link Cursor}
     * @param section number of man's section
     * @return a parsed {@link ManTableRecord}
     */
    protected ManTableRecord cursorToComment(Cursor cursor, int section) {
        ManTableRecord command = new ManTableRecord();
        command.setId(cursor.getLong(0));
        command.setName(cursor.getString(1));
        command.setSynopsis(cursor.getString(2));
        command.setFile(cursor.getString(3));
        command.setSection(section);
        return command;
    }

    @Override
    protected void onPostExecute(List<ManTableRecord> manTableRecords) {
        responseListener.onDbResponse(manTableRecords);
    }
}
