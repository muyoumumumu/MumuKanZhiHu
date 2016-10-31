package com.muyoumumumu.mumukanzhihu.other;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 我的dbHelper
 * Created by amumu on 2016/8/26.
 */
public class MyDbHelper extends SQLiteOpenHelper {
    public MyDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //primary key 作为主键，主要排序用吧
        //作为标题显示页，主要为首页图和标题 简介
        sqLiteDatabase.execSQL("create table if not exists Posts(_id integer primary key,title text not null,type integer not null,img_url text not null,date integer not null)");
        //数据存储页为内容
        sqLiteDatabase.execSQL("create table if not exists Contents(_id integer primary key,date text not null,content text not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
