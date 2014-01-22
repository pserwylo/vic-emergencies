package com.serwylo.emergencies.data;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @TargetApi(11)
    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String INCIDENT_DDL =
            "CREATE TABLE incident (" +
                "_id integer primary key, " +
                "type text, " +
                "name text, " +
                "status text, " +
                "createdTime integer, " +
                "updatedTime integer, " +
                "severity integer" +
            ")";
        db.execSQL(INCIDENT_DDL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static SQLiteOpenHelper create(Context context) {
        return new DbHelper(context, "incidents", null, 1);
    }
}
