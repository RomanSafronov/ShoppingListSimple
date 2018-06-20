package com.roman.safronov.simpleshopinglist;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DB {

    private static final String DB_NAME = "mysimpleshopinglist";
    private static final int DB_VERSION = 1;
    private static final float START_QUANTITY = 1;
    private static final String DB_TABLE = "simpleshopingtab";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_QUNTITY = "quantity";
    public static final String COLUMN_TXT = "txt";
    public static final String COLUMN_STATE = "state";

    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_QUNTITY + " integer, " +
                    COLUMN_TXT + " text, " +
                    COLUMN_STATE + " ineteger"+
                    ");";

    private final Context mCtx;


    private DBHelper mDBHelper;
    private SQLiteDatabase mSSLDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    // open the connection
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mSSLDB = mDBHelper.getWritableDatabase();
    }

    // close the connection
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    // get all data from DB_TABLE
    public Cursor getAllData() {
        return mSSLDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    // get data by ID
    public Cursor getData (long id){
        Cursor c = mSSLDB.query(DB_TABLE, null, "_id = "+String.valueOf(id), null, null, null, null);
        if (c.moveToFirst()){
            return c;
        }
        return c = null;
    }

    // add record to DB_TABLE
    public void addRec(String txt, float quant, int state) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TXT, txt);
        cv.put(COLUMN_QUNTITY, quant);
        cv.put(COLUMN_STATE, state);
        mSSLDB.insert(DB_TABLE, null, cv);
    }

    // delete record from DB_TABLE
    public void delRec(long id) {
        mSSLDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    //remove all data from DB_TABLE

    public void clearTable() { mSSLDB.delete(DB_TABLE, null, null);}

    //edit record

    public void editRec(long id, String txt, float quant, int state){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TXT, txt);
        cv.put(COLUMN_QUNTITY, quant);
        cv.put(COLUMN_STATE, state);
        mSSLDB.update(DB_TABLE, cv, COLUMN_ID + " = " + id, null);

    }


   //add new record
    public void addNewRec(String txt, float quant, int state){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TXT, txt);
        cv.put(COLUMN_QUNTITY, quant);
        cv.put(COLUMN_STATE, state);
        mSSLDB.insert(DB_TABLE, null, cv);

    }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DB_CREATE);

            ContentValues cv = new ContentValues();
            for (int i = 1; i < 20; i++) {
                cv.put(COLUMN_TXT, "sometext " + i);
                cv.put(COLUMN_QUNTITY, START_QUANTITY);
                cv.put(COLUMN_STATE, 0);
                db.insert(DB_TABLE, null, cv);
            }
        }



        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}