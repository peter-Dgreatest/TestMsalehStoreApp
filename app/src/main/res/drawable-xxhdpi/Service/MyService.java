package com.itcrusaders.msaleh.Service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;


import com.itcrusaders.msaleh.LoginActivity;
import com.itcrusaders.msaleh.R;
import com.itcrusaders.msaleh.helpers.App;
import com.itcrusaders.msaleh.helpers.AuthorizationHttpResponse;
import com.itcrusaders.msaleh.helpers.DataDB;
import com.itcrusaders.msaleh.helpers.HttpConnectionHelper;
import com.itcrusaders.msaleh.helpers.MyJSON;
import com.itcrusaders.msaleh.helpers.Push;
import com.itcrusaders.msaleh.helpers.ServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Hosanna_TechVibes on 06-Feb-17.
 */

public class MyService extends Service {

  String latitude = "Unknown", longitude = "Unknown", email, appK, deviceimei, accessCode, user_id, service_code, errorMssg, service_id, authorization_code, isFirstSync, add_request_url, request_date, urlParameters;
  App app;
  int authorized;
  AuthorizationHttpResponse authResp;
  private static final String TAG = "AuthorizationService";
  private LocationManager mLocationManager = null;
  private static final int LOCATION_INTERVAL = 1000;
  private static final float LOCATION_DISTANCE = 10f;
  // if GPS is enabled
  boolean isGPSEnabled = false;
  // if Network is enabled
  boolean isNetworkEnabled = false;
  DataDB dataDB = new DataDB();

  MyJSON pull = new MyJSON();
  com.itcrusaders.msaleh.helpers.Push Push = new Push();
    /*MyJSON myJSON = new MyJSON();
    Push push = new Push();*/

  String SyncEmpInfoEP, SyncEmpInfoVerifyEP, SyncPenEmpInfoEP, SyncPenEmpInfoVerifyEP, PushEmpInfo, PushEnroll, PushPenEmpInfo, PushPenEnroll, PushCheckin;

  public MyService() {
    super();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }


  @Override
  public void onCreate() {
    super.onCreate();
    app = ((App) getApplicationContext());

    Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_LONG).show();
    Log.e("Service", "fffff");



    initializeLocationManager();
    try {
      // Getting GPS status
      isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    } catch (SecurityException ex) {
      Log.i(TAG, "fail to request location update, ignore", ex);
    } catch (IllegalArgumentException ex) {
      Log.d(TAG, "network provider does not exist, " + ex.getMessage());
    }


    mTimer = new Timer();
    mTimer.schedule(timerTask, 2000, 20 * 1000); //2000, 5 * 1000 //60*1000

  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    try {
      Log.e("[-V-]", "My VeRiTeX iS RuNiNg:-)");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return super.onStartCommand(intent, flags, startId);
  }

  private Timer mTimer;

  TimerTask timerTask = new TimerTask() {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void run() {
      if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {


        Cursor cursor1=null;
        try {
          cursor1 = dataDB.
                  myConnection(getApplicationContext()).
                  selectAllFromTable("login");
        }catch (Exception ex){

        }

        if (cursor1 != null && cursor1.getColumnCount() > 0) {
          try {
            cursor1.moveToFirst();
            do {
              user_id = cursor1.getString(cursor1.getColumnIndex("username"));
            } while (cursor1.moveToNext());
          } catch (Exception ex) {
            Log.e("pllll;;;;", ex.toString() + ";;;;;");
          }
        } else {
          Log.e("empty", "empty;;;;;");
        }

         //   verify(email, service_code);
            Sync("stafffingerprint","staffId");
            Sync("staffidcard","staffId");
            Sync("staffsignature","staffId");
            Sync("staffphoto","staffId");

            Sync("equipmentImages","equipmentId");

      } else {
        Log.e("service", " Permission not granted ");
//          ActivityCompat.requestPermissions(MyService.getApplicationContext(), new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.BLUETOOTH_PRIVILEGED,Manifest.permission.READ_PHONE_STATE }, 0);

      }


    }
  };

  void Sync(String what2sync, String idCol) {


    HashMap<String, String> postDataParams;
    postDataParams = new HashMap<String, String>();

    try {
      String url = getString(R.string.mobilesync);


      Log.e(TAG, "This is request " + url);

      HttpConnectionHelper service = new HttpConnectionHelper(getApplicationContext());



      Cursor cursor = new DataDB().myConnection(getApplicationContext())
              .selectAllFromTable("SELECT * FROM "+what2sync+" WHERE synchedstatus = 1 LIMIT 1;", true);

      int count=0;
      cursor.moveToFirst();
      while (cursor.isAfterLast() == false) {



        int totalColumn = cursor.getColumnCount();
        count=totalColumn;

        for( int i=0 ;  i< totalColumn ; i++ )
        {
          if( cursor.getColumnName(i) != null )
          {
            try
            {

              if(cursor.getString(i) != null &&
                      !cursor.getColumnName(i).equalsIgnoreCase("Img"))
              {
                //Log.d(TAG, cursor.getColumnName(i));
                postDataParams.put(cursor.getColumnName(i),cursor.getString(i));
              }
              else
              {
                postDataParams.put(cursor.getColumnName(i),"null");
              }
              postDataParams.put("Img",cursor.getString(cursor.getColumnIndex("Img")));
            }
            catch( Exception e )
            {
              Log.d(TAG, e.getMessage()  );

            }
          }

        }
        postDataParams.put("type",what2sync);
        cursor.moveToNext();

      }

      Log.d(TAG,"hello"  );

      if(count>0) {
        String response = service.sendRequest(url, postDataParams);
        //AuthorizationHttpResponse httpResp = serviceHandler.makeServiceCall(url, 1, params);

        Log.d(TAG, response);

        if(response != null && !response.contains("failure")) {
          Log.e(TAG, "This is response " + response);

          ContentValues contentValues = new ContentValues();
          contentValues.put("synchedstatus", 2);

          long suc = new DataDB().myConnection(getApplicationContext())
                  .onUpdateOrIgnore(contentValues, what2sync, idCol, response);

          //  if (suc > 0) {

          //} else {
          errorMssg = response;
          //}
        } else {
          errorMssg = response;
        }
      }
    } catch (Exception ex) {
      Log.e("Error",ex.getMessage());
      ex.printStackTrace();
    }
  }

  public AuthorizationHttpResponse callServiceMod(String endPoint, JSONObject enteredDataIds) {
    return authResp;
  }

  private void initializeLocationManager() {
    Log.e(TAG, "initializeLocationManager");
    if (mLocationManager == null) {
      mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }
  }


  @Override
  public void onDestroy() {
    super.onDestroy();
  }


  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  void verify(String username, String servicecode) {

    String abc;
    String SeseErrorMssg;

    try {
      /**
      JSONObject authJsonObject = new JSONObject();
      authJsonObject.put("email", username.trim());
//                    authJsonObject.put("password", mPassword.trim());
      authJsonObject.put("service_code", servicecode.trim());
      authJsonObject.put("device_os", Devices.getDeviceModel());
//                    authJsonObject.put("appKeyParameter", getString(R.string.appId));
      authJsonObject.put("device_uuid", Devices.getDeviceUUID(getApplicationContext().getApplicationContext()));
      authJsonObject.put("device_imei", Devices.getDeviceIMEI(getApplicationContext().getApplicationContext()));
      authJsonObject.put("device_name", Devices.getDeviceName());
      authJsonObject.put("device_model", Devices.getDeviceModel());
      authJsonObject.put("device_longitude", longitude);
      authJsonObject.put("device_latitude", latitude);
      authJsonObject.put("device_version", Devices.getDeviceVersion());

      Log.e(TAG, "This is request " + authJsonObject.toString());
**/
      ServiceHandler serviceHandler = new ServiceHandler();


   //   abc = authJsonObject.toString();
                    /*Cursor mycursor = dataDB.myConnection(getActivity().getApplicationContext()).selectAllFromTableAs222();
                    String EPdata="";
                    if (mycursor != null  && mycursor.moveToFirst() ) { EPdata = mycursor.getString(20);}
*/
      AuthorizationHttpResponse httpResp = null;//= serviceHandler.makeServiceCallJSON(getString(R.string.auth), 2, authJsonObject);


      if (httpResp != null && httpResp.getResponseData() != null) {
        Log.e(TAG, "This is response " + httpResp.getResponseCode() + "::" + httpResp.getResponseData());
        if (httpResp.getResponseCode() == 200) {

          // TODO: 11/10/2016 get user data from json
          JSONObject parentObject = new JSONObject(httpResp.getResponseData());

          JSONObject childObject = parentObject.getJSONObject("response");

          String SeseResponseCode = childObject.getString("responseCode");

          String SeseResponseDescription = childObject.getString("responseDescription");


          Log.e(TAG, "Parent Object: " + parentObject);
          Log.e(TAG, "Child Object: " + childObject);
          Log.e(TAG, "sese response code: " + SeseResponseCode);
          Log.e(TAG, "Seses response description: " + SeseResponseDescription);

          if (SeseResponseDescription.equals("Success")) {

            Log.d(TAG, " user authorized: ");


            // TODO: 11/10/2016 save user data
            ContentValues contentValues = new ContentValues();

            //contentValues.put("tax_registration_id",tax_registration_id);
            //Log.e("tax registration is**: ", Long.toString(tax_registration_id));

            contentValues.put("authorized", "1");
            contentValues.put("user_type", "3");


            //db code


            long rowInserted = dataDB.myConnection(getApplicationContext()).onUpdateOrIgnore(contentValues,
                    "users", "email", username);
            if (rowInserted != -1) {

              Log.e(TAG, "user details saved: " + ":-)");
            //  ReInitializeDB reInitializeDB = new ReInitializeDB(getApplicationContext());

           //   if (reInitializeDB.clearDB())
              {
                sendAuthorizationNotification("Authorization Granted");
                ContentValues contentValues1 = new ContentValues();
                contentValues1.put("idpayer_balance",0);
                contentValues1.put("balance",0);
                contentValues1.put("tax_id",username);
                contentValues1.put("account","");

                long lastInsertId2 = dataDB.myConnection(getApplicationContext())
                        .onInsertOrUpdate(contentValues1,"payer_balance");
              }
            }


            SeseErrorMssg = "null";

            //return true;
          } else if (SeseResponseCode.equals("500")) {
            SeseErrorMssg = "Internal Server Error";
            errorMssg = SeseResponseCode;
          } else {
            errorMssg = SeseResponseCode;
            SeseErrorMssg = SeseResponseDescription;
          }
        } else {
          errorMssg = httpResp.getResponseData();
          String respcode = String.valueOf(httpResp.getResponseCode());
          errorMssg = respcode;

        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }

  }

  //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  public void sendAuthorizationNotification(String message) {


    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
    PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    int defaults = 0;
    defaults = defaults | Notification.DEFAULT_LIGHTS;
    defaults = defaults | Notification.DEFAULT_VIBRATE;
    defaults = defaults | Notification.DEFAULT_SOUND;
    Notification n = new Notification.Builder(getApplicationContext())
            .setContentTitle("AlphaPay")
            .setContentText(message)
            .setTicker("Authorized!")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pIntent)
            .setDefaults(defaults)
            .setAutoCancel(true).build();


    NotificationManager notificationManager =
            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    notificationManager.notify(0, n);


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

      CharSequence name = "Revcollect Enumerator";
      String description = "User Authorized!";
      int importance = NotificationManager.IMPORTANCE_HIGH;
      NotificationChannel channel = new NotificationChannel("1099", name, importance);
      channel.setDescription(description);
      // Register the channel with the system; you can't change the importance
      // or other notification behaviors after this
      NotificationManager notificationManager2 = getSystemService(NotificationManager.class);
      notificationManager2.createNotificationChannel(channel);

      NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1099")
              .setSmallIcon(R.mipmap.ic_launcher)
              .setContentTitle("Revcollect Enumerator")
              .setContentText("User Authorized!")
              .setPriority(NotificationCompat.PRIORITY_DEFAULT)
              // Set the intent that will fire when the user taps the notification
              .setContentIntent(pIntent)
              .setAutoCancel(true);

      NotificationManagerCompat notificationManagerC = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
      notificationManagerC.notify(1099, builder.build());
    }


  }


}

