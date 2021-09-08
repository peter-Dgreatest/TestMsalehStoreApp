package com.itcrusaders.msaleh.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 8;
    private static final String DATABASE_NAME = "msalah.db";
    Context myContext;
    String DB_PATH;

    private SQLiteDatabase myDataBase;

    public DatabaseHandler(@Nullable Context context, @Nullable String name) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);

        this.myContext = context;
//        Log.e("Path 1", DB_PATH);
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";


        if(android.os.Build.VERSION.SDK_INT >= 4.2){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";

        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";

        }

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.disableWriteAheadLogging();
    }

    public DatabaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);

        this.myContext = context;
//        Log.e("Path 1", DB_PATH);
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";


        if(android.os.Build.VERSION.SDK_INT >= 4.2){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";

        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";

        }

    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion>oldVersion){
            try {
                myContext.deleteDatabase(DATABASE_NAME);
                createDataBase();
                openDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO: 23/09/2016 to create database
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if(dbExist){

        }else{
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }

    // TODO: 23/09/2016 Check if database exist
    public boolean checkDataBase()
    {
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            Log.w("DB", "Checked and found database ");
        }catch(SQLiteException e){
            //database does't exist yet.
            Log.w("DB", "Database does not exist after checking ");
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    // TODO: 23/09/2016 Copy database
    private void copyDataBase() throws IOException
    {
        Log.w("DB", "Copying database... ");
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
        //MySQLiteOpenHelper helper = new MySQLiteOpenHelper(myContext,DB_NAME);
        SQLiteDatabase database = this.getReadableDatabase();
        String filePath = database.getPath();
        database.close();


        OutputStream myOutput = new FileOutputStream(filePath);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
        /**Log.w("DB", "Copying database... ");
         InputStream myInput = myContext.getAssets().open(DB_NAME);
         String outFileName = DB_PATH + DB_NAME;
         OutputStream myOutput = new FileOutputStream(outFileName);
         byte[] buffer = new byte[1024];
         int length;
         while ((length = myInput.read(buffer))>0){
         myOutput.write(buffer, 0, length);
         }
         //Close the streams
         myOutput.flush();
         myOutput.close();
         myInput.close();
         **/
    }


    public void openDataBase() throws SQLException {
        Log.w("DB", "Opening database... ");
        String myPath = DB_PATH + DATABASE_NAME;
        myDataBase = this.getWritableDatabase();//SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

    }


    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    public long onInsertOrUpdate(ContentValues values, String _tableName)
    {
        long id;
        Log.d("onInsertOrUpdate", "insertOrIgnore on " + values);
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            id=db.insertWithOnConflict(_tableName, null, values,
                    SQLiteDatabase.CONFLICT_REPLACE);
        } finally {
            db.close();
        }
        return id;
    }

    public long onUpdateOrIgnore(ContentValues values, String _tableName, String _fieldName, String _fieldValue)
    {
        long id;
        Log.d("onInsertOrUpdate", "insertOrIgnore on " + values);
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            //id=db.update(_tableName, values, _fieldName + "=?", new String[]{_fieldValue.toString()}); //new String[]{String.valueOf(_fieldValue)}
            id=db.update(_tableName, values, _fieldName + "='" + _fieldValue + "'", null);
        } finally {
            db.close();
        }
        return id;
    }

    public long onInsert(ContentValues values, String _tableName)
    {
        long isSuccess = 0;
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            isSuccess = db.insert(_tableName, null, values);
            db.close();
        }catch(Exception e){
            e.printStackTrace();
            isSuccess = 0;
        }
//        myDataBase.close();
        return isSuccess;
    }

    public boolean onInsert(String query)
    {
        boolean isSuccess = false;
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(query);
            db.close();
            isSuccess = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    // count all records
    public long countRecords(String _tableName){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT count(*) from " + _tableName, null);
        cursor.moveToFirst();

        long recCount = cursor.getInt(0);
        cursor.close();
        db.close();

        return recCount;
    }

    // count all records
    public long countRecordsWhere(String _tableName, String _columnName, String _param){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT count(*) from " + _tableName + " Where " +_columnName+"='"+_param+"'", null);
        cursor.moveToFirst();

        long recCount = cursor.getInt(0);
        cursor.close();
        db.close();

        return recCount;
    }

    public Cursor selectAllFromTable(String _tableName)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + _tableName + ";", null);
        cursor.moveToFirst();

        cursor.close();
        db.close();

        return cursor;
    }

    public Cursor selectAllFromTable(String _from, String _fieldName, String _fieldValue)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + _from + " where " + _fieldName + " = '" +_fieldValue + "';";

        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        return cursor;
    }


    public Cursor selectAllFromTable(String sql, boolean yes)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //String sql = "SELECT * FROM " + _from + " where " + _fieldName + " = '" +_fieldValue + "';";

        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        return cursor;
    }

}
