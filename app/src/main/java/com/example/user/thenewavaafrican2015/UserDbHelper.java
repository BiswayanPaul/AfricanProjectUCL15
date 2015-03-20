package com.example.user.thenewavaafrican2015;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
/**
 * Created by User on 17/03/2015.
 */
import com.example.user.thenewavaafrican2015.UserContract.UserEntry;

public class UserDbHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VER = 1;
    public static final String DATABASE_NAME = "User.db";

    public UserDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VER);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + UserEntry.TABLE_NAME + " ("
        + UserEntry.COLUMN_NAME_ID + " INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,"
        + UserEntry.COLUMN_NAME_NAME + " VARCHAR(30), "
        + UserEntry.COLUMN_NAME_AGE + " INTEGER, "
        + UserEntry.COLUMN_NAME_INFECTED + " INTEGER, "
        + UserEntry.COLUMN_NAME_LAST_ACCESS + " TIMESTAMP)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        onCreate(db);
    }
}
