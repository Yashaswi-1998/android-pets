package com.example.pets.data;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pets.CatalogActivity;
import com.example.pets.EditorActivity;

import java.sql.SQLPermission;

public class PetsProvider extends ContentProvider {

    public static final String LOG_TAG = PetsProvider.class.getSimpleName();
    // SQLiteDatabase database;
    ShelterDb helper;
     public static final int PETS=100;
     public static final int PETS_ID=101;
     public static final UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
     static {
         sUriMatcher.addURI(SQL.PACKAGE,"pets", PETS);
         sUriMatcher.addURI(SQL.PACKAGE,"pets/#",PETS_ID);
     }

    @Override
    public boolean onCreate() {

       helper=new ShelterDb(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable
            String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
         SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor;
         int i=sUriMatcher.match(uri);
          switch (i)
          {
              case PETS:
                 cursor= db.query(SQL.PetData.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                 break;
              case PETS_ID:
                  selection=SQL.PetData._ID +"=?";
                  selectionArgs=new String []{String.valueOf(ContentUris.parseId(uri))};
                  cursor=db.query(SQL.PetData.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                  break;
                  default:
                      throw new IllegalArgumentException("Cannot query unknown URI " + uri);
          }
          cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
         SQLiteDatabase db=helper.getWritableDatabase();
         int i=sUriMatcher.match(uri);
         int r=0;
         switch (i) {
             case PETS:
                 r= db.delete(SQL.PetData.TABLE_NAME, selection, selectionArgs);
                 break;
             case PETS_ID:
                 selection = SQL.PetData._ID + " =?";
                 selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                 r= db.delete(SQL.PetData.TABLE_NAME, selection, selectionArgs);
                 break;
                 default:
                 throw new IllegalArgumentException("Cannot query unknown URI " + uri);

         }
         if(r!=0)

           getContext().getContentResolver().notifyChange(uri,null);
         return r;

     }




    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        String s= values.getAsString(SQL.PetData.NAME);
        String n=values.getAsString(SQL.PetData.BREED);
        if(n.equals(""))
        {
            values.put(SQL.PetData.BREED,"unknown");
        }

        if(s.equals(""))
        {
            Log.v("tag1","working1");
            Toast.makeText(getContext(),"Name Cannot be blank",Toast.LENGTH_SHORT).show();
             return null;

        }

       int i =sUriMatcher.match(uri);
       switch (i)
       {
           case PETS:
               Uri u=insertPet(uri,values);
               return u;
           default:
               throw new IllegalArgumentException("Cannot query unknown URI " + uri);
       }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
         SQLiteDatabase db=helper.getWritableDatabase();

          int match=sUriMatcher.match(uri);
          switch (match)
          {
              case PETS:
                  return petUpdate(uri,values,selection,selectionArgs);

              case PETS_ID:
                  selection=SQL.PetData._ID+ " =?";
                  selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                  return petUpdate(uri,values,selection,selectionArgs);
                  default:
                      throw  new IllegalArgumentException("cannot update unknown Url"+ uri);
          }


    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

         int match=sUriMatcher.match(uri);

         switch (match)
         {
             case PETS:
                 return SQL.PetData.MIME_DIR;
             case PETS_ID:
                 return SQL.PetData.MIME_ITEM;
              default:
                  throw new IllegalArgumentException(" cannot return unknowm uri"+uri);
         }

    }
    private Uri insertPet(Uri uri,ContentValues values)
    {    SQLiteDatabase db=helper.getWritableDatabase();

        long id=db.insert(SQL.PetData.TABLE_NAME,null,values);
        if(id!=0)
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri,id);
    }
    private  int petUpdate(Uri uri,ContentValues values,String selection,String[] selectionArgs)
    {

        SQLiteDatabase db= helper.getWritableDatabase();
        if(values.containsKey(SQL.PetData.NAME))
        {
          String s = values.getAsString(SQL.PetData.NAME);
            if(s.equals(""))
            {
                Toast.makeText(getContext(), "Name cannot be blank", Toast.LENGTH_LONG);
                return -1;
            }
        }
        if(values.containsKey(SQL.PetData.WEIGHT))
        {
            int w=values.getAsInteger(SQL.PetData.WEIGHT);
            if(w<0) {
                Toast.makeText(getContext(), "Invalid Weight", Toast.LENGTH_LONG);
                return -1;
            }
        }

        if (values.size() == 0) {
            return 0;
        }
            int n=db.update(SQL.PetData.TABLE_NAME,values,selection,selectionArgs);
        if(n!=0)
        getContext().getContentResolver().notifyChange(uri, null);
            return n;
    }
}
