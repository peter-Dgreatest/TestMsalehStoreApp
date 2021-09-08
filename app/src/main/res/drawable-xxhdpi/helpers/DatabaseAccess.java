package com.itcrusaders.msaleh.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.itcrusaders.msaleh.database.EnrollDAO;

import java.util.ArrayList;


/**
 * Created by AKPO  LELEJI on 29/05/2016.
 */
public class DatabaseAccess {
private SQLiteOpenHelper openHelper;
private SQLiteDatabase database;
private static DatabaseAccess instance;

    App appState;



        /**
         * Private constructor to aboid object creation from outside classes.
         *
         * @param context
         */
        private DatabaseAccess(Context context) {
            this.openHelper = new DatabaseOpenHelper(context);
        }

        /**
         * Return a singleton instance of DatabaseAccess.
         *
         * @param context the Context
         * @return the instance of DabaseAccess
         */
        public static DatabaseAccess getInstance(Context context) {
            if (instance == null) {
                instance = new DatabaseAccess(context);

            }
            return instance;
        }

        /**
         * Open the database connection.
         */
        public void open() {
            this.database = openHelper.getWritableDatabase();
        }

        /**
         * Close the database connection.
         */
        public void close() {
            if (database != null) {
                this.database.close();
            }
        }

        /**
         * Read all quotes from the database.
         *
         * @return a List of quotes
         */

        public boolean insertEnrol(String employee_no, String tpt, String ftype, String enrolled_by, String entered_on, String service_id, String user_id, String longitude, String latitude, String auth_id)
        {
            ContentValues cv=new ContentValues();
            cv.put("employee_no",employee_no);
            cv.put("tpt",tpt);
            cv.put("ftype",ftype);
            cv.put("enrolled_by",enrolled_by);
            cv.put("entered_on",entered_on);
            cv.put("service_id",service_id);
            cv.put("user_id",user_id);
            cv.put("longitude",longitude);
            cv.put("latitude",latitude);
            cv.put("authorization_id",auth_id);


            database.insert("mobile_synch_enroll","NA",cv);
            return true;
        }

    public boolean insertEnrol2(String employee_no, String tpt, String ftype, String enrolled_by, String entered_on, String service_id, String user_id, String longitude, String latitude, String auth_id)
    {
        ContentValues cv=new ContentValues();
        cv.put("employee_no",employee_no);
        cv.put("tpt",tpt);
        cv.put("ftype",ftype);
        cv.put("enrolled_by",enrolled_by);
        cv.put("entered_on",entered_on);
        cv.put("service_id",service_id);
        cv.put("user_id",user_id);
        cv.put("longitude",longitude);
        cv.put("latitude",latitude);
        cv.put("authorization_id",auth_id);


        database.insert("mobile_synch_pensions_enroll","NA",cv);
        return true;
    }
        public ArrayList<Templates> getTemplates()
        {

            ArrayList<Templates> tm=new ArrayList<Templates>();
            //Templates list = new Templates();
            Cursor cursor = database.rawQuery("select tpt,employee_no from mobile_synch_enroll", null);
            cursor.moveToFirst();


            while (!cursor.isAfterLast()) {
                Templates t=new Templates();
                byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(cursor.getString(0).getBytes());
                t.templates=decoded;
                t.employee_no=cursor.getString(1);
/*
                appState = ((App)getInstance(Contex context));
*/

                tm.add(t);
                cursor.moveToNext();
            }
            cursor.close();
            return tm;
        }
        public void insertUserDetails(String email, String password, String name, String department, String ministry, String service_id, String user_id)
        {
        // boolean suc;

            String query = "INSERT INTO users (email,password,name,ministry,department,service_id,id_user) VALUES('"+email+"','"+password+"','"+name+"','"+ministry+"','"+department+"','"+service_id+"','"+user_id+"')";
            database.execSQL(query);


        }
    public void insertStaff(EnrollDAO st)
    {

        ContentValues cv=new ContentValues();
        cv.put("employee_no",st.getStaffId());
        cv.put("surname",st.getStaffName());
        cv.put("first_name",st.getStaffName());
        database.insert("synch_employee_info","NA",cv);
        ///String query="insert into synch_employee_info(employee_no,surname,first_name,middle_name)" +
          //      " values ('"+emp_no+"','"+lname+"','"+fname+"','"+mname+"')";

        //database.execSQL(query);


    }

        public long getUsers()
        {

            long taskCount = DatabaseUtils.queryNumEntries(database,"users");
            return taskCount;

        }
    public ArrayList<EnrollDAO> getUser(String email, String password)
    {
        ArrayList<EnrollDAO> list = new ArrayList<EnrollDAO>();
        Cursor cursor = database.rawQuery("select email,password,name,ministry,department,service_id from users where email='"+email+"' and password='"+password+"'", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            EnrollDAO s=new EnrollDAO();
            s.setStaffId(cursor.getString(2));
            list.add(s);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }
                                   // db.checkIn(x,z,a, y, "","","",dd,cc,b,y,e,latitude,longitude);

    public void checkIn(String employee_no, String month, String year, String checck_in_time, String interval_id, String interval_time, String fpt, String user_id, String service_id, String registered_by, String registered_on, String auth_id, String longitude, String latitude, String cid)
    {
        String query = "INSERT INTO check_in (employee_no,month,year,check_in_time,interval_time,interval_id,fpt,user_id,service_id,registered_by, registered_on,authorization_id,longitude,latitude,check_in_date) VALUES('"+employee_no+"','"+month+"','"+year+"','"+checck_in_time+"','"+interval_time+"','"+interval_id+"','"+fpt+"','"+user_id+"','"+service_id+"','"+registered_by+"','"+registered_on+"','"+auth_id+"','"+longitude+"','"+latitude+"','"+checck_in_time+"')";
        database.execSQL(query);


    }
    public void insertMobileSynch(String date, String auth_id, String tableName)
    {
        String query = "update "+tableName+" set synch_status=1, registered_on='"+date+"' where authorization_id='"+auth_id+"';";
        database.execSQL(query);


    }
    public void insertMobileSynch2(String date, String auth_id, String tableName)
    {
        String query = "update "+tableName+" set synch_status=1, entered_on='"+date+"' where authorization_id='"+auth_id+"';";
        database.execSQL(query);


    }

    public Cursor mobile_synch(String _tableName)
    {
//        String query = "SELECT * FROM " + _tableName + " WHERE synch_status=0 AND service_id='"+service_id+"' LIMIT 50;";
        String query = "SELECT * FROM " + _tableName + " WHERE synch_status=0 LIMIT 50;";

//        database.execSQL(query);

        Cursor cursor = database.rawQuery(query, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        return cursor;


    }

    //
    public String getDateTime()
    {
        //String[] args = {_tax_id.trim()};
//        appState = ((App)context);
        String getDateTime="2017-05-25";
//        SQLiteDatabase db = myConnection(context).getWritableDatabase();
//        String sq = "SELECT datetime('now')";
        String sq = "SELECT datetime('now','localtime')";
//        select datetime('now','localtime')
        //Cursor cursor = db.rawQuery("SELECT tax_registration_id FROM tax_registration WHERE tax_id=? OR temp_tax_id=? LIMIT 1", args);
        Cursor cursor = database.rawQuery(sq,null);
        int k = cursor.getCount();
        try{
            String _taxid = "",_temp_taxid="";
            if (cursor.moveToFirst()) {
                do {
                    getDateTime = cursor.getString(0);
                } while (cursor.moveToNext());
            }

        }finally{
            cursor.close();
//            db.close();
        }
//        myDBconnection.close();

        return getDateTime;
    }

    //-------
    public Cursor checkForAuthorizationRequest(Context context)
    {
//        Cursor cursor=null;
        String sql = "SELECT * FROM users LIMIT 1;";//WHERE authorized=0
  /*try {
      cursor = database.rawQuery(sql, null);
      Log.i(TAG, "Result from DBCheckAuthorization query; " + cursor.toString());
      if (cursor != null) {
          cursor.moveToFirst();
      }

      //database.close();

      else {
          return null;
      }
  }
       catch (Exception e){      Log.i(TAG, "Error");
       }
        return  cursor;
   */
        Cursor cursor = database.rawQuery(sql, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        return cursor;

    }

    public ArrayList<EnrollDAO> getStaff(String empno)
    {
        ArrayList<EnrollDAO> list = new ArrayList<EnrollDAO>();
        Cursor cursor = database.rawQuery("select surname,first_name,middle_name,employee_no,service_id,user_id from employee_info where employee_no='"+empno+"' ", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            EnrollDAO s=new EnrollDAO();
            s.setStaffName(cursor.getString(1));
            s.setStaffId(cursor.getString(5));

            list.add(s);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    /*public ArrayList<Staff> getStaff2(String empno)
    {
        ArrayList<Staff> list = new ArrayList<Staff>();
        Cursor cursor = database.rawQuery("select employee_no, surname,first_name,middle_name from synch_employee_info where employee_no='"+empno+"' ", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Staff s=new Staff();
            s.fname=cursor.getString(1);
            s.lname=cursor.getString(0);
            s.mname=cursor.getString(2);
            list.add(s);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }
*/
    public ArrayList<EnrollDAO> getUserData()
    {
        ArrayList<EnrollDAO> list = new ArrayList<EnrollDAO>();
        Cursor cursor = database.rawQuery("select email,service_id,id_user from users ORDER BY user_id desc limit 0,1", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            EnrollDAO s=new EnrollDAO();
            s.setStaffMail(cursor.getString(0));
            s.setStaffId(cursor.getString(2));


            list.add(s);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }
    public String getUser()
    {
        String list = "";
        Cursor cursor = database.rawQuery("select email from users limit 1", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String mail=cursor.getString(0);

           list=mail;
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public ArrayList<String> getLga(String state)
    {
        ArrayList<String> list = new ArrayList<String>();
        Cursor cursor = database.rawQuery("select LGAName from lga where Statename='"+state+"'", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return list;

    }
    public ArrayList<String> getward(String lga)
    {
        ArrayList<String> list = new ArrayList<String>();
        Cursor cursor = database.rawQuery("select RAName from ras where Lga='"+lga+"'", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return list;

    }








}
