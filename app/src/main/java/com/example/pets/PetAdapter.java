package com.example.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.pets.data.SQL;

import java.util.concurrent.TimeoutException;

public class PetAdapter extends CursorAdapter {
    public PetAdapter(Context context, Cursor cursor)
    {
        super(context,cursor,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.template,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name=(TextView) view.findViewById(R.id.pet_name);
        TextView breed=(TextView) view.findViewById(R.id.pet_breed);
         String s=cursor.getString(cursor.getColumnIndex(SQL.PetData.NAME));
         String n=cursor.getString(cursor.getColumnIndex(SQL.PetData.BREED));
         name.setText(s);
         breed.setText(n);

    }
}
