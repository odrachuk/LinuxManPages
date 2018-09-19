/*******************************************************************************
 * Created by o.drachuk on 15/05/2014.
 * <p/>
 * Copyright Oleksandr Drachuk.
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
package com.softsandr.man.db;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * 1. About onCreate() and onUpdate()
 * <p/>
 * onCreate(..) is called whenever the app is freshly installed.
 * onUpgrade is called whenever the app is upgraded and launched and the database version is not the same.
 * <p/>
 * 2. Incrementing the db version
 * <p/>
 * You need a constructor like:
 * <p/>
 * MyOpenHelper(Context context) {
 * super(context, "dbname", null, 2); // 2 is the database version
 * }
 * IMPORTANT: Incrementing the app version alone is not enough for onUpgrade to be called!
 * <p/>
 * 3. Don't forget your new users!
 * <p/>
 * Don't forget to add
 * <p/>
 * database.execSQL(DATABASE_CREATE_color);
 * to your onCreate() method as well or newly installed apps will lack the table.
 * <p/>
 * 4. How to deal with multiple database changes over time
 * <p/>
 * When you have successive app upgrades, several of which have database upgrades,
 * you want to be sure to check oldVersion:
 * <p/>
 * onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 * switch(oldVersion) {
 * case 1:
 * db.execSQL(DATABASE_CREATE_color);
 * // we want both updates, so no break statement here...
 * case 2:
 * db.execSQL(DATABASE_CREATE_someothertable);
 * }
 * }
 * This way when a user upgrades from version 1 to version 3, they get both updates.
 * When a user upgrades from version 2 to 3, they just get the revision 3 update...
 * After all, you can't count on 100% of your user base to upgrade each time you release an update.
 * Sometimes they skip an update or 12 :)
 * <p/>
 * 5. Keeping your revision numbers under control while developing
 * <p/>
 * And finally... calling
 * <p/>
 * adb uninstall <yourpackagename>
 * totally uninstalls the app.
 * When you install again, you are guaranteed to hit onCreate which keeps you from having to keep incrementing
 * the database version into the stratosphere as you develop...
 */
public class ManSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = ManSQLiteOpenHelper.class.getName();
    private static final Logger LOGGER = Logger.getLogger(LOG_TAG);
    //The Android's default system path of your application database.
    private static final String DB_LOCATION = "/data/data/com.softsandr.man/databases/";
    private static final String DB_NAME = "man.sqlite";
    private static final String DB_PATH = DB_LOCATION + DB_NAME;

    private SQLiteDatabase database;

    private final AtomicBoolean ready = new AtomicBoolean(false);
    private final Context context;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context application context
     */
    public ManSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
        // Creates a empty database on the system and rewrites it with your own database.
        boolean dbExist = checkDbExist();
        try {
            if (dbExist) {
                if (checkDbVersion()) {
                    LOGGER.info("SQLite database already present and have last updates");
                } else {
                    LOGGER.warning("SQLite database already present but not have last updates.");
                    if (deleteDatabase(new File(DB_PATH))) {
                        LOGGER.info("SQLite database is updating...");
                        copyDatabase();
                    }
                }
            } else {
                copyDatabase();
                LOGGER.info("SQLite database has been copied in first");
            }
        } catch (IOException e) {
            LOGGER.severe("SQLite database cannot be opened or created");
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDbExist() {
        SQLiteDatabase database = null;
        try {
            String path = DB_LOCATION + DB_NAME;
            database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
            LOGGER.severe("SQLite database of project does not exist yet");
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return database != null;
    }

    private boolean checkDbVersion() {
        return getAppVersion().equals(getDbVersion());
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transferring byte stream.
     */
    private void copyDatabase() throws IOException {
        LOGGER.info("");
        //By calling getReadableDatabase a database will be created into the default system path
        //of your application so we are gonna be able to overwrite that database with our database.
        getReadableDatabase();
        OutputStream output = null;
        InputStream input = null;
        try {
            //Open your local db as the input stream
            input = context.getAssets().open(DB_NAME);
            // Path to the just created empty db
            String outFileName = DB_PATH;
            //Open the empty db as the output stream
            output = new FileOutputStream(outFileName);
            //transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } catch (Exception e) {
            LOGGER.throwing(LOG_TAG, "copyDatabase", e);
        } finally {
            //Close the streams
            if (output != null) {
                output.flush();
                output.close();
            }
            if (input != null) {
                input.close();
            }
        }
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public synchronized boolean openDatabase() {
        //Open the database
        try {
            database = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
            ready.set(true);
        } catch (Exception e) {
            LOGGER.throwing(LOG_TAG, "openDatabase", e);
            ready.set(false);
        }
        return ready.get();
    }

    public synchronized void closeDatabase() {
        LOGGER.info("");
        try {
            if (database != null && database.isOpen()) {
                database.close();
            }
            ready.set(false);
        } catch (Exception e) {
            LOGGER.throwing(LOG_TAG, "closeDatabase", e);
        }
    }

    @Override
    public synchronized void close() {
        LOGGER.info("");
        try {
            if (database != null && database.isOpen()) {
                database.close();
            }
        } catch (Exception e) {
            LOGGER.throwing(LOG_TAG, "close", e);
        }
        ready.set(false);
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LOGGER.info("");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOGGER.info("oldVersion = " + oldVersion + " newVersion = " + newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOGGER.info("oldVersion = " + oldVersion + " newVersion = " + newVersion);
        super.onDowngrade(db, oldVersion, newVersion);
    }

    public boolean isReady() {
        return ready.get();
    }

    private String getDbVersion() {
        String dbVersion = "";
        final String[] tableColumns = {
                "_id",
                "code",
                "name"};
        Cursor cursor = null;
        try {
            openDatabase();
            cursor = database.query("db_info", tableColumns, null, null, null, null, tableColumns[0]);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                dbVersion = cursor.getString(2);
                cursor.moveToNext();
            }
        } catch (SQLiteException e) {
            LOGGER.throwing(LOG_TAG, "getDbVersion", e);
        } catch (Exception e) {
            LOGGER.throwing(LOG_TAG, "getDbVersion", e);
        } finally {
            if (cursor != null) {
                // make sure to close the cursor
                cursor.close();
            }
        }
        LOGGER.info("DB version is " + dbVersion);
        return dbVersion;
    }

    private String getAppVersion() {
        String appVersion = "1.0.0";
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                PackageInfo pInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                appVersion = pInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            // ignored
        }
        LOGGER.info("APP version is " + appVersion);
        return appVersion;
    }

    /**
     * Deletes a database including its journal file and other auxiliary files
     * that may have been created by the database engine.
     *
     * @param file The database file path.
     * @return True if the database was successfully deleted.
     */
    private boolean deleteDatabase(File file) {
        LOGGER.info("");
        ready.set(false);
        if (file == null) {
            throw new IllegalArgumentException("file must not be null");
        }

        boolean deleted = false;
        deleted |= file.delete();
        deleted |= new File(file.getPath() + "-journal").delete();
        deleted |= new File(file.getPath() + "-shm").delete();
        deleted |= new File(file.getPath() + "-wal").delete();

        File dir = file.getParentFile();
        if (dir != null) {
            final String prefix = file.getName() + "-mj";
            final FileFilter filter = new FileFilter() {
                @Override
                public boolean accept(File candidate) {
                    return candidate.getName().startsWith(prefix);
                }
            };
            for (File masterJournal : dir.listFiles(filter)) {
                deleted |= masterJournal.delete();
            }
        }
        return deleted;
    }
}
