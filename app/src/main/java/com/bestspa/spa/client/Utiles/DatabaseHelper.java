package com.bestspa.spa.client.Utiles;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bestspa.spa.client.Model.Country;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DB_NAME = "BestSpa.sqlite";
    public static String DB_PATH = null;
    private static final String KEY_COUNTRY_ID = "country_id";
    private static final String KEY_COUNTRY_NAME = "country_name";
    private static final String KEY_COUNTRY_CODE = "country_code";
    private static DatabaseHelper sInstance;
    private Context context;
    public SQLiteDatabase database;

    public static synchronized DatabaseHelper getInstance(Context context) {
        DatabaseHelper databaseHelper;
        synchronized (DatabaseHelper.class) {
            if (sInstance == null) {
                sInstance = new DatabaseHelper(context.getApplicationContext());
            }
            databaseHelper = sInstance;
        }
        return databaseHelper;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 5);
        this.context = context;
        DB_PATH = String.format("//data//data//%s//databases//", new Object[]{context.getPackageName()});
        openDataBase();
    }

    public SQLiteDatabase getDb() {
        return this.database;
    }

    public SQLiteDatabase openDataBase() throws SQLException {
        String path = DB_PATH + DB_NAME;
        if (this.database == null) {
            createDataBase();
            this.database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        }
        return this.database;
    }

    public void createDataBase() {
        if (!checkDataBase()) {
            getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database!"+e.getMessage());
            }
        }
    }

    public synchronized void closeDataBase()throws SQLException
    {
        if(database != null)
            database.close();
        super.close();
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDb = null;
        try {
            String path = DB_PATH + DB_NAME;
            if (new File(path).exists()) {
                checkDb = SQLiteDatabase.openDatabase(path, null, 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (checkDb != null) {
            checkDb.close();
        }
        if (checkDb != null) {
            return true;
        }
        return false;
    }

    private void copyDataBase() throws IOException {
        InputStream externalDbStream = this.context.getAssets().open(DB_NAME);
        OutputStream localDbStream = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = externalDbStream.read(mBuffer)) > 0) {
            localDbStream.write(mBuffer, 0, mLength);
        }
        localDbStream.flush();
        localDbStream.close();
        externalDbStream.close();
    }

    public synchronized void close() {
        if (this.database != null) {
            this.database.close();
        }
        super.close();
    }

    public List<Country> getAllToCountry() {
        List<Country> countryList = new ArrayList();
        Cursor c = getReadableDatabase().rawQuery("SELECT  * FROM country", null);
        if (c.moveToFirst()) {
            do {
                Country con = new Country();
                con.setCountyId(c.getString(c.getColumnIndex("country_id")));
                con.setCountryName(c.getString(c.getColumnIndex(KEY_COUNTRY_NAME)));
                con.setCountryCode(c.getString(c.getColumnIndex(KEY_COUNTRY_CODE)));
                countryList.add(con);
            } while (c.moveToNext());
        }
        c.close();
        return countryList;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
