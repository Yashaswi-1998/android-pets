package com.example.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.core.content.ContextCompat;

public class ShelterDb extends SQLiteOpenHelper
{
    public  final static  String DATABASE_NAME="shelter.db";
    public final static int VERSION =1;
    public ShelterDb (Context context)
    {
        super(context,DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQLITE_CREATE_TABLE= "CREATE TABLE " + SQL.PetData.TABLE_NAME +"(" + SQL.PetData._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                SQL.PetData.NAME +" TEXT, " +SQL.PetData.BREED + " TEXT DEFAULT 'dont know', "+ SQL.PetData.GENDER + " INTEGER DEFAULT 2, " + SQL.PetData.WEIGHT
                +" INTEGER default 0);";
        db.execSQL(SQLITE_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
}
