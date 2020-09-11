package com.kitchas.kitchenassistant.utils.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.kitchas.kitchenassistant.assistant.user.User;

import java.util.List;
import java.util.Map;

public class SQLHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "KITCHEN_ASSISTANT";
    public static final String TABLE_SAVED_DATA = "SAVED_DATA";
    public static final String COLUMN_ID = "identify";
    public static final String COLUMN_JSON = "json";


    public static final String[] SELECTED_COLUMN = { COLUMN_JSON };
    public static final String WHERE_COLUMN = COLUMN_ID + " = ?";

    public final SQLiteDatabase reader;
    public final SQLiteDatabase writer;

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.reader = getReadableDatabase();
        this.writer = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_SAVED_DATA + " (" +
                COLUMN_ID + " VARCHAR(255), " +
                COLUMN_JSON + " TEXT" + ")");
        sqLiteDatabase.execSQL("CREATE TABLE " + User.DB_LAST_RECIPES_LIST +
                "(recipe TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED_DATA);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + User.DB_LAST_RECIPES_LIST);
        onCreate(sqLiteDatabase);
    }

    public String getData(String identify) {
        Cursor cursor = reader.query(
                SQLHelper.TABLE_SAVED_DATA,
                SQLHelper.SELECTED_COLUMN,
                SQLHelper.WHERE_COLUMN,
                new String[]{ identify }, null, null, null);

        if (cursor.getCount() > 0) {
            try {
                cursor.moveToFirst();
                String res = cursor.getString(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_JSON));
                cursor.close();
                return res;
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    public long insertData(Object params, String identify) {
        ContentValues values = new ContentValues();
        values.put(SQLHelper.COLUMN_ID, identify);
        values.put(SQLHelper.COLUMN_JSON, new Gson().toJson(params));

        long id = writer.insert(SQLHelper.TABLE_SAVED_DATA, null, values);
        writer.close();
        return id;
    }

    public boolean updateData(Object params, String identify) {
        ContentValues values = new ContentValues();
        values.put(SQLHelper.COLUMN_JSON, new Gson().toJson(params));

        Cursor tempCursor = reader.rawQuery(
                "SELECT * FROM " + SQLHelper.TABLE_SAVED_DATA + " " +
                        "WHERE " + SQLHelper.COLUMN_ID + " = '" + identify + "'", null);
        if (tempCursor.getCount() == 0) {
            return false;
        }

        int res = writer.update(SQLHelper.TABLE_SAVED_DATA,
                values,
                SQLHelper.WHERE_COLUMN,
                new String[]{ identify });
        System.out.println(res);
        writer.close();
        return true;
    }

    public SQLHelper reOpenReader(Context context) {
        return new SQLHelper(context);
    }
}
