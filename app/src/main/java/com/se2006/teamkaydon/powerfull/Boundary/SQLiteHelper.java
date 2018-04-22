package com.se2006.teamkaydon.powerfull.Boundary;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * SQLite helper class provide methods to read SQL database files
 *
 * @author Team Kaydon
 * @version 1.0
 * @since 2018-04-17
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    // Tag just for the LogCat window.
    private static String TAG = "SQLiteHelper";

    // Destination path (location) of our database on the device.
    private static String DB_PATH = "app/src/main/assets/";

    // Database name.
    private static String DB_NAME ="ChargingStationLocations.db";

    private SQLiteDatabase mDataBase;
    private final Context mContext;

    /**SQLite helper class
     * @param context Context object
     */
    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, 1); // Database Version 1
        if(android.os.Build.VERSION.SDK_INT >= 17){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
    }

    /**Creates database
     * @throws IOException
     */
    public void createDataBase() throws IOException {
        // If the database does not exist, copy it from the assets folder.
        boolean mDataBaseExist = checkDataBase();
        if(!mDataBaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                // Copy the database from assets folder
                copyDataBase();
                Log.e(TAG, "Database created");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDatabase");
            }
        }
    }

    /**Check that the database exists here: /data/data/your package/databases/
     * @return Return true if database file exists
     */

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    // Copy the database from assets
    private void copyDataBase() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    /** Open the database, so we can query it
     * @return Return true if database is not null
     * @throws SQLException
     */
    public boolean openDataBase() throws SQLException {
        String mPath = DB_PATH + DB_NAME;
        //Log.v("mPath", mPath);
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDataBase != null;
    }

    /**
     * Close database
     */
    @Override
    public synchronized void close() {
        if(mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}