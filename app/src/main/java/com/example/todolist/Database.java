package com.example.todolist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // truy vấn CREATE, INSERT, UPDATE, DELETE,...
    public void QueryData(String sql){
        //getWritableDatabase: đọc + ghi
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }


    // truy vấn SELECT
    // trả dữ liệu dạng con trỏ
    public Cursor GetData(String sql){
        //getReadableDatabase: ghi
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql,null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
