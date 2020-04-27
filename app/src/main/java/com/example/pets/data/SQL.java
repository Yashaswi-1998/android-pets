package com.example.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.PortUnreachableException;
import java.net.URI;

public final class SQL {
    private SQL ()
    { }
        public static final  String SCHEME="content://";
        public static final  String PACKAGE ="com.example.pets.data";
        public  static final Uri uri=Uri.parse(SCHEME+PACKAGE);


        public static final class  PetData implements BaseColumns {
            public static final String MIME_ITEM= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+ PACKAGE+ "/"+"pets/#";
            public static final String MIME_DIR= ContentResolver.CURSOR_DIR_BASE_TYPE+ "/"+ PACKAGE+"/"+"pets";
            public static final Uri petsUri= Uri.withAppendedPath(uri,"pets");
            public static final String TABLE_NAME = "pets";
            public static final String _ID = BaseColumns._ID;
            public static final String NAME = "name";
            public static final String BREED = "breed";
            public static final String WEIGHT="weight";
            public static final String GENDER="gender";

            public static final int MALE=0;
            public static final int FEMALE=1;
            public static final int UNKNOWN=2;
        }
}
