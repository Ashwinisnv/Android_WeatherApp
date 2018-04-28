package com.ashwinisnv.weathersay.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.ashwinisnv.weathersay.Model.coordinates;

import java.util.ArrayList;

/**
 * Created by ashwinivishwas on 4/7/18.
 */

public class DataController {

    public static final String DATABASE_NAME="WeatherDB.db";
    public static final String TABLE_NAME="weather";
    public static final String ITEM_COLUMN_LAT="latitude";
    public static final String ITEM_COLUMN_LNG="longitude";
    public static final int DATABASE_VERSION=4;
    public static final String TABLE_CREATE="create table weather (latitude text not null,longitude text not null);";

    DataBaseHelper dbHelper;
    Context context;
    SQLiteDatabase db;


    public DataController(Context context)
    {
        this.context=context;
        dbHelper=new DataBaseHelper(context);
    }

    public DataController open()
    {
        db=dbHelper.getWritableDatabase();
        //dbHelper.onUpgrade(db,3,4);
        return this;
    }

    public void close()
    {
        dbHelper.close();
    }

    public long insert(double lat,double lng)
    {
        //dbHelper.onUpgrade(db,0,1);
        ContentValues content=new ContentValues();
        content.put("latitude", lat);
        content.put("longitude", lng);
        return db.insertOrThrow(TABLE_NAME, null, content);
    }

    public Cursor retrieve(double lat,double lng)
    {
        db = dbHelper.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from weather where latitude="+lat+"and longitude="+lng+"", null );
        return res;
    }

    public void deleteLocation(double lat, double lng)
    {
        db = dbHelper.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME + " where " + ITEM_COLUMN_LAT + "='" + lat + "' and " + ITEM_COLUMN_LNG + "='"+lng+"'" );
        db.close();
    }

    public ArrayList<coordinates> getAllLocations() {
        ArrayList<coordinates> array_list = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from weather", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            coordinates coord = new coordinates(res.getDouble(res.getColumnIndex(ITEM_COLUMN_LAT)),res.getDouble(res.getColumnIndex(ITEM_COLUMN_LNG)));
            array_list.add(coord);
            res.moveToNext();
        }
        return array_list;
    }

    private static class DataBaseHelper extends SQLiteOpenHelper
    {

        public DataBaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            try
            {
                db.execSQL(TABLE_CREATE);
            }
            catch(SQLiteException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS weather");
            onCreate(db);
        }

    }
}


