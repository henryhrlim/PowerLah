package com.se2006.teamkaydon.powerlah;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

public class ChargingStationDataManager implements ChargingStationDAO {
    protected static final String TAG = "ChargingStationDataManager";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private SQLiteHelper mDbHelper;

    public ChargingStationDataManager(Context context) {
        this.mContext = context;
        mDbHelper = new SQLiteHelper(mContext);
    }

    @SuppressLint("LongLogTag")
    public ChargingStationDataManager createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    @SuppressLint("LongLogTag")
    public ChargingStationDataManager open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() {

        mDbHelper.close();
    }

    @SuppressLint("LongLogTag")
    public Cursor retrieveData() {
        try {
            String sql ="SELECT * FROM ChargingStationLocations";

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur!=null) {
                mCur.moveToNext();
            }
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "retrieveData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }
}