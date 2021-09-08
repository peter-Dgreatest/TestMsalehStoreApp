package com.itcrusaders.msaleh.bioofly;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.itcrusaders.msaleh.R;
import com.itcrusaders.msaleh.helpers.DataDB;
import com.itcrusaders.msaleh.lib.fpengine.fpengine;
import com.telpo.tps550.api.fingerprint.FingerPrint;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity2 extends AppCompatActivity {

    String currentFileName = "" ;    //file name
    String currentFilePath = "";     //file path
    String currentFileAbsolutePath= "";     //file path
    int left_or_right = 0;
    int captureNum = 0;
    public static int fileselecting = 0;

    // init components
    ImageView imgFPLeft;
    ImageView imgFPRight;
    EditText editDirectoryPath1; //directory path
    EditText editDirectoryPath2; //directory path
    EditText editTemplateBinary1; //binary file information
    EditText editTemplateBinary2; //binary file information

    byte g_Version[];

    //	private String mfileName, mFileName;
    int fileNum = 0;
    private Context mContext = this;

    // for process bitmap image
    Bitmap mBmpFPImage;
    BitmapHeader_8Bit BH_8;
    static byte mByteReadImageData[];
    static byte mByteDrawImageData[];

    static byte lefttemplate[];
    static byte righttemplate[];

    static int mStartID = 1;
    static int mCount = 1;
    static int mEnrollStep = 0;
    static int mTargetID = 1;
    static int mTmp;

    private UsbManager manager; // USB管理器
    private UsbDevice mUsbDevice; // 找到的USB设备
    private UsbInterface mInterface1;
    private UsbInterface mInterface2;
    private UsbDeviceConnection mDeviceConnection;
    private PendingIntent mPermissionIntent;
    private UsbEndpoint epCmd;
    private UsbEndpoint epIn;
    private UsbEndpoint epOut;

    String staffId="";

    /**
     * convert byte array to hex string
     * https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
     * @param hash
     * @return
     */
    public String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    // 寻找接口和分配结点
    private void findIntfAndEpt() {
        if (mUsbDevice == null) {
            Log.i("yw", "没有找到设备");
            editTemplateBinary1.setText("没有找到设备");
            return;
        }
        //查找接口
        Log.i(Define.tag, "mUsbDevice.getInterfaceCount() = " + mUsbDevice.getInterfaceCount());
        //for (int i = 0; i < mUsbDevice.getInterfaceCount(); i++) {
        // 获取设备接口，一般都是一个接口，你可以打印getInterfaceCount()方法查看接
        // 口的个数，在这个接口上有两个端点，OUT 和 IN
        mInterface1 = mUsbDevice.getInterface(0);
        mInterface2 = mUsbDevice.getInterface(1);
        //break;
        //}

        if (mInterface1 != null && mInterface2 != null) {
            UsbDeviceConnection connection = null;
            // 判断是否有权限
            if (manager.hasPermission(mUsbDevice)) {

                // 打开设备，获取 UsbDeviceConnection 对象，连接设备，用于后面的通讯
                connection = manager.openDevice(mUsbDevice);
                if (connection == null) {
                    return;
                }
                //
                if (connection.claimInterface(mInterface1, true)) {
                    Log.i("yw", "找到接口1");
                    editTemplateBinary1.setText("找到接口1");
                    mDeviceConnection = connection;
                    // 获取USB通讯的读写端点
                    //getEndpoint(mDeviceConnection, mInterface1);
                    Log.i(Define.tag, "getEndPoint0 started.");
                    if (mInterface1.getEndpoint(0) != null) {
                        epCmd = mInterface1.getEndpoint(0);
                    }
                } else {
                    connection.close();
                }
                if (connection.claimInterface(mInterface2, true)) {
                    Log.i(Define.tag, "找到接口2");
                    editTemplateBinary1.setText("找到接口2");
                    mDeviceConnection = connection;
                    // 获取USB通讯的读写端点
                    //getEndpoint(mDeviceConnection, mInterface2);
                    Log.i(Define.tag, "getEndPoint1 started. cnt = " + mInterface2.getEndpointCount());
                    if (mInterface2.getEndpoint(0) != null) {
                        epOut = mInterface2.getEndpoint(0);
                    }
                    Log.i(Define.tag, "getEndPoint3 started.");
                    if (mInterface2.getEndpoint(1) != null) {
                        epIn = mInterface2.getEndpoint(1);
                    }
                    g_Version[0] = (byte) Define.CAPTURE_MODE_USB;
                    g_Version[2] = (byte)mDeviceConnection.getFileDescriptor();
                    g_Version[3] = (byte)(mDeviceConnection.getFileDescriptor() / 256);
                    g_Version[4] = (byte)epIn.getAddress();
                    g_Version[5] = (byte)(epIn.getAddress() / 256);
                    g_Version[6] = (byte)epOut.getAddress();
                    g_Version[7] = (byte)(epOut.getAddress() / 256);

                    String strBinaryVersion = byteToHex(g_Version);
                    editTemplateBinary2.setText(strBinaryVersion);
                    Log.d(Define.tag, "g_Version = " + strBinaryVersion);


                    fpengine.initialize(g_Version);
                    String ver = new String(g_Version);
                    Log.i(Define.tag, "version : " + ver);
                    Toast.makeText(mContext, "version : " + ver, Toast.LENGTH_SHORT).show();

//                    cSepronik.OpenDevice(mDeviceConnection.getFileDescriptor(),epIn.getAddress(),epOut.getAddress());
//                	Log.d(TAG, "================= epIn information ==================");
//                	Log.d(TAG, "getEndPoint3 ended.");
//                	Log.d(TAG, "epIn.getAddress() : " + epIn.getAddress());
//                	Log.d(TAG, "epIn.getDirection() : " + epIn.getDirection());
//                	Log.d(TAG, "epIn.getAttributes() : " + epIn.getAttributes());
//                	Log.d(TAG, "epIn.getEndpointNumber() : " + epIn.getEndpointNumber());
//                	Log.d(TAG, "epIn.getInterval() : " + epIn.getInterval());
//                	Log.d(TAG, "epIn.getMaxPacketSize() : " + epIn.getMaxPacketSize());
//                	Log.d(TAG, "epIn.getType() : " + epIn.getType());
//
//                	Log.d(TAG, "================= epOut information ==================");
//                	Log.d(TAG, "epOut.getAddress() : " + epOut.getAddress());
//                	Log.d(TAG, "epOut.getDirection() : " + epOut.getDirection());
//                	Log.d(TAG, "epOut.getAttributes() : " + epOut.getAttributes());
//                	Log.d(TAG, "epOut.getEndpointNumber() : " + epOut.getEndpointNumber());
//                	Log.d(TAG, "epOut.getInterval() : " + epOut.getInterval());
//                	Log.d(TAG, "epOut.getMaxPacketSize() : " + epOut.getMaxPacketSize());
//                	Log.d(TAG, "epOut.getType() : " + epOut.getType());
//
//                	Log.d(TAG, "================= Constants information ==================");
//                	Log.d(TAG, "UsbConstants.USB_DIR_IN : " + UsbConstants.USB_DIR_IN);
//                	Log.d(TAG, "UsbConstants.USB_DIR_OUT : " + UsbConstants.USB_DIR_OUT);

                } else {
                    connection.close();
                }
            } else {
                Log.e("yw","没有权限" );
                mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(Define.ACTION_USB_PERMISSION), 0);
                IntentFilter filter = new IntentFilter(Define.ACTION_USB_PERMISSION);
                registerReceiver(mUsbReceiver, filter);
                manager.requestPermission(mUsbDevice, mPermissionIntent);
                Log.i(Define.tag, "没有权限");
                editTemplateBinary1.setText("没有权限");
            }
        } else {
            Log.i(Define.tag, "没有找到接口");
            editTemplateBinary1.setText("没有找到接口");
        }
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            Log.e("yw","收到广播："+intent.getAction());
            String action = intent.getAction();
            if (Define.ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            // call method to set up device communication
                            findIntfAndEpt();
                        }
                    } else {
                        Log.d(Define.tag, "permission denied for device " + device);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        if(getIntent()!=null){
            staffId = getIntent().getStringExtra("staffId");
        }else{
            finish();
            return;//startActivity(new Intent(this, ImagesCapture.cl));
        }
        FingerPrint.fingerPrintPower(1);//上电

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(Define.ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(Define.ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);



        mByteReadImageData = new byte[Define.READ_IMAGE_SIZE];
        mByteDrawImageData = new byte[Define.READ_IMAGE_HEIGHT * (Define.READ_IMAGE_WIDTH + Define.READ_IMAGE_FRICTION) + 1078];
        lefttemplate = new byte[Define.tmplSize];
        righttemplate = new byte[Define.tmplSize];

        imgFPLeft = findViewById(R.id.fingerprintImage);

        g_Version = new byte[30];

        //hide software keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //initialize fingerprint engine
        if(Define.comm_mode == "USB")
        {
            try{
                configUSB();
            }
            catch(Exception e)
            {
                Toast.makeText(getBaseContext(),"configUSB failure!", Toast.LENGTH_SHORT).show();
            }

        }


        findViewById(R.id.capturebtn).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        int retVal = 100, limitCnt = 0;
                        while(retVal > 0)
                        {
                            retVal = fpengine.capture(mByteReadImageData);
                            Log.d(Define.tag, "LeftCapture fpengine.capture() result = " + retVal);
                            limitCnt++;
                            if(limitCnt > 3)
                            {
                                Log.d(Define.tag, "LeftCapture failure.");
                                editDirectoryPath1.setText("LeftCapture failure.");
                                break;
                            }
                        }
                        makeBmpData();
                        mBmpFPImage = BitmapFactory.decodeByteArray(mByteDrawImageData, 0, mByteDrawImageData.length);
                        imgFPLeft.setImageBitmap(mBmpFPImage);

                        //captureNum++;
                        String tmplFileName = Define.mSDCardImg + staffId+ "." + Define.__EXTENSION_IMG;
                        SaveToFile(tmplFileName, mByteDrawImageData);

                        byte[] length = new byte[2];
                        byte[] quality = new byte[1];

                        fpengine.getISO2005Tmpl(lefttemplate, length, quality);
                        tmplFileName = Define.mSDCardTmpl + "/tmpl_"+staffId+ "." + Define.__EXTENSION_TEMPLATE;
                        SaveToFile(tmplFileName, lefttemplate);

                        String strBinaryInformation = byteToHex(lefttemplate);
                    //    editTemplateBinary1.setText(strBinaryInformation);

                      //  editDirectoryPath1.setText(tmplFileName);

                        Log.i(Define.tag, "Left Captured.");
                    }
                }
        );

    }

    private void configUSB() {
        // 启动的时候就去获取设备

        // 获取USB设备
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if (manager == null) {
            Log.e("yw","manager null");
            return;
        } else {
            Log.e("yw","manager：" + manager.toString());
            // Log.i(Define.tag, "usb设备：" + String.valueOf(manager.toString()));
        }


        //获取USB设备列表
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        // Log.i(Define.tag, "usb设备：" + String.valueOf(deviceList.size()));
        Log.e("yw","manager device：" + deviceList.size());
        editTemplateBinary1.setText("usb设备：" + String.valueOf(deviceList.size()));
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            Log.e("yw_id",""+device.getVendorId());
            // 在这里添加处理设备的代码         根据VID和PID查找指定的设备
            if (device.getVendorId() == 0x483 && device.getProductId() == 0x5750) {


                mUsbDevice = device;
                Log.i(Define.tag, "找到设备");
                Log.e("yw","找到特定设备" );
                editTemplateBinary1.setText("找到设备");


                manager.requestPermission(mUsbDevice, mPermissionIntent);


                Log.e("yw","开始找端点" );
                //查找通信端点
                //  findIntfAndEpt();
            }
        }


    }



    /**
     * make bitmap data
     */
    public void makeBmpData() {
        BH_8 = new BitmapHeader_8Bit(Define.READ_IMAGE_WIDTH, Define.READ_IMAGE_HEIGHT);

        for(int i = 0; i < 1078; i ++)
            mByteDrawImageData[i] = BH_8.BMP_HEADER[i];

        for(int i = 0; i < Define.READ_IMAGE_HEIGHT; i ++){
            for(int j = 0; j < (Define.READ_IMAGE_WIDTH + Define.READ_IMAGE_FRICTION); j ++){
                if(j >= Define.READ_IMAGE_WIDTH){
                    mByteDrawImageData[i * (Define.READ_IMAGE_WIDTH + Define.READ_IMAGE_FRICTION) + j + 1078] = 0x00;
                }
                else{
                    mByteDrawImageData[i * (Define.READ_IMAGE_WIDTH + Define.READ_IMAGE_FRICTION) + j + 1078] = mByteReadImageData[i * Define.READ_IMAGE_WIDTH + j];
                }	}	}
        return;
    }

    /**
     * save template to file
     * @param filename  file name
     * @param tmpl      template
     * @return success 0, failure -1
     */
    public int SaveToFile(String filename, byte[] tmpl)
    {
        // TODO Auto-generated method stub
        File tmplFile = null;
        try{
            tmplFile = new File(filename);
            tmplFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(tmplFile);
            BufferedOutputStream myOutWriter = new BufferedOutputStream(fOut);
            myOutWriter.write(tmpl);
            myOutWriter.close();
            fOut.close();

            ContentValues contentValue = new ContentValues();
            String base64String = Base64.encodeToString(tmpl, Base64.NO_WRAP);
            contentValue.put("Img", base64String);
            contentValue.put("synchedstatus", 1);
            long insert_emp = new DataDB().myConnection(getApplicationContext()).onUpdateOrIgnore(contentValue,
                    "stafffingerprint","staffId",staffId);
            if (insert_emp > 0) {
                Log.e("saving +++++: " ,":}");

                //app.setCameraByte(picData);
                Toast.makeText(getApplicationContext(), "Picture saved successfully", Toast.LENGTH_LONG).show();

                Intent output = new Intent();
           //     output.putExtra("type",imgtype);
                //     output.putExtra("img",base64String);
                setResult(RESULT_OK, output);
                MainActivity2.this.finish();
            }
            else {
                Toast.makeText(getApplicationContext(), "Error saving picture. Please try again", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(getBaseContext(),"Error save template to file " + filename, Toast.LENGTH_SHORT).show();
            return -1;
        }
        return 0;
    }

    @Override
    public void onDestroy() {
        fpengine.terminate();
        FingerPrint.fingerPrintPower(0);//下电
        try {
            unregisterReceiver(mUsbReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

}