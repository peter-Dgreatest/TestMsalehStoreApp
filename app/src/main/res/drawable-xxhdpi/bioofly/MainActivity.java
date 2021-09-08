package com.itcrusaders.msaleh.bioofly;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.itcrusaders.msaleh.R;
import com.itcrusaders.msaleh.lib.fpengine.fpengine;
import com.telpo.tps550.api.fingerprint.FingerPrint;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends Activity {

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
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		setContentView(R.layout.activity_main_main);


		FingerPrint.fingerPrintPower(1);//上电

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(Define.ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(Define.ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

        editDirectoryPath1 = (EditText)findViewById(R.id.editDirPath1);
		editDirectoryPath2 = (EditText)findViewById(R.id.editDirPath2);
		editTemplateBinary1 = (EditText)findViewById(R.id.etDisplayLeft);
		editTemplateBinary2 = (EditText)findViewById(R.id.etDisplayRight);
		imgFPLeft = (ImageView)findViewById(R.id.ivFPImageLeft);
		imgFPRight = (ImageView)findViewById(R.id.ivFPImageRight);


        mByteReadImageData = new byte[Define.READ_IMAGE_SIZE];
		mByteDrawImageData = new byte[Define.READ_IMAGE_HEIGHT * (Define.READ_IMAGE_WIDTH + Define.READ_IMAGE_FRICTION) + 1078];
		lefttemplate = new byte[Define.tmplSize];
		righttemplate = new byte[Define.tmplSize];

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

		
		findViewById(R.id.btn_LeftCapture).setOnClickListener(
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
					String tmplFileName = Define.mSDCardImg + "/image_left_" + captureNum + "." + Define.__EXTENSION_IMG;
					SaveToFile(tmplFileName, mByteDrawImageData);
					
					byte[] length = new byte[2];
					byte[] quality = new byte[1];
					
					fpengine.getISO2005Tmpl(lefttemplate, length, quality);
					tmplFileName = Define.mSDCardTmpl + "/tmplISOLeft_" + captureNum + "." + Define.__EXTENSION_TEMPLATE;
					SaveToFile(tmplFileName, lefttemplate);
					
					String strBinaryInformation = byteToHex(lefttemplate);
					editTemplateBinary1.setText(strBinaryInformation); 
					
					editDirectoryPath1.setText(tmplFileName);
					
					Log.i(Define.tag, "Left Captured.");
			    }
			}
		);
		
		findViewById(R.id.btn_RightCapture).setOnClickListener(
		    new Button.OnClickListener() {
		        public void onClick(View v) {
			    	int retVal = 100, limitCnt = 0;
			    	while(retVal > 0)
			    	{
				    	retVal = fpengine.capture(mByteReadImageData);
				    	Log.d(Define.tag, "RightCapture fpengine.capture() result = " + retVal);
				    	limitCnt++;
				    	if(limitCnt > 3)
				    	{
				    		Log.d(Define.tag, "RightCapture failure.");
				    		editDirectoryPath2.setText("RightCapture failure.");
				    		break;
				    	}
			    	}
			    	
		        	makeBmpData();
		        	mBmpFPImage = BitmapFactory.decodeByteArray(mByteDrawImageData, 0, mByteDrawImageData.length);
		        	imgFPRight.setImageBitmap(mBmpFPImage);
		        	
		        	//captureNum++;
					String tmplFileName = Define.mSDCardImg + "/image_right_" + captureNum + "." + Define.__EXTENSION_IMG;
					SaveToFile(tmplFileName, mByteDrawImageData);
					
					byte[] length = new byte[2];
					byte[] quality = new byte[1];
					
					fpengine.getISO2005Tmpl(righttemplate, length, quality);
					tmplFileName = Define.mSDCardTmpl + "/tmplISORight_" + captureNum + "." + Define.__EXTENSION_TEMPLATE;
					SaveToFile(tmplFileName, righttemplate);
					
					String strBinaryInformation = byteToHex(righttemplate);
					editTemplateBinary2.setText(strBinaryInformation); 
					
					editDirectoryPath2.setText(tmplFileName);
					
					Log.i(Define.tag, "Right Captured.");
		        }
		    }
		);
		
		findViewById(R.id.btn_GetLeftTmpl).setOnClickListener(
		    new Button.OnClickListener() {
		        public void onClick(View v) {
		        	//read binary file and display 
					editTemplateBinary1.setText(""); 
					editDirectoryPath1.setText("");
					left_or_right = Define.REQUEST_FILE_EXPLORER_ACTION_LEFT;
					getFilePath(left_or_right, Define.__EXTENSION_TEMPLATE); //get file information(path and name)
					
					Log.i(Define.tag, "Left Captured.");
		        }
		    }
		);
		
		findViewById(R.id.btn_Get_RightTmpl).setOnClickListener(
		    new Button.OnClickListener() {
		        public void onClick(View v) {
		        	editTemplateBinary2.setText(""); 
					editDirectoryPath2.setText("");
					left_or_right = Define.REQUEST_FILE_EXPLORER_ACTION_RIGHT;
					getFilePath(left_or_right, Define.__EXTENSION_TEMPLATE); //get file information(path and name)
					
					Log.i(Define.tag, "Right Captured.");
		        }
		    }
		);
		
		findViewById(R.id.btn_VerifyISO).setOnClickListener(
		    new Button.OnClickListener() {
		        public void onClick(View v) {
		    		if(fpengine.verifyISO(lefttemplate, righttemplate) == 0)
		    		{
		    			Log.i(Define.tag,"Verify ISO successed.");
						Toast.makeText(mContext, "Verify ISO successed.", Toast.LENGTH_SHORT).show();
						}
						else
						{
							Log.i(Define.tag,"Verify ISO failured.");
						Toast.makeText(mContext, "Verify ISO failured.", Toast.LENGTH_SHORT).show();
		    		}
		
		        }
		    }
		);
		
		findViewById(R.id.btn_VerifyANSI).setOnClickListener(
		        new Button.OnClickListener() {
		            public void onClick(View v) {
		        		if(fpengine.verifyANSI(lefttemplate, righttemplate) == 0)
		        		{
		        			Log.i(Define.tag,"Verify ANSI successed.");
						Toast.makeText(mContext, "Verify ANSI successed.", Toast.LENGTH_SHORT).show();
						}
						else
						{
							Log.i(Define.tag,"Verify ANSI failured.");
						Toast.makeText(mContext, "Verify ANSI failured.", Toast.LENGTH_SHORT).show();
		    		}
		
		        }
		    }
		);
	}
	
	/**
	 * Create menu option
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Menu item selection handler
	 */ 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Toast.makeText(mContext, "Setting selected", Toast.LENGTH_SHORT).show();
			return true;
		}
		
		/* file browser menu select */
		if (id == R.id.action_file_explorer) {
			Toast.makeText(mContext, "File explorer selected", Toast.LENGTH_SHORT).show();
			return true;
		}
	 
		/* Select capture processing*/
		
		if (id == R.id.action_interface_test) {
			//Toast.makeText(mContext, "Interface test screen...", Toast.LENGTH_SHORT).show();			 
			Intent intent = new Intent(MainActivity.this, InterfaceActivity.class);
//			intent.putExtra("name",""); /**/
//            intent.putExtra("age",20);
//            intent.putExtra("array",array);
//            intent.putExtra("class",option);
			MainActivity.this.startActivity(intent);
			return true;
		}
		
		/* Select main screen*/
		/*
		if (id == R.id.action_demo_main_screen) {
			Toast.makeText(mContext, "Demo main screen ...", Toast.LENGTH_SHORT).show();			 
			Intent intent = new Intent(MainActivityPrint.this, MainActivityPrint.class);
			MainActivityPrint.this.startActivity(intent);
			return true;
		}*/
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Browse storage and get directory path
	 */
	public void getFilePath(int left_right, String sFileExtension){
		//Toast.makeText(mContext, "File browser selected", Toast.LENGTH_SHORT).show();
    	fileselecting = 1;
    	Intent intent1 = new Intent(this, FileChooser.class);
    	intent1.putExtra("fileExtension", sFileExtension);
    	startActivityForResult(intent1, left_right);
    } 
	  /**
	   * Read the given binary file, and return its contents as a byte array.
	   * @param inputFileName     input file name(path + name)
	   * @return byte array
	   */
	byte[] readBinaryFile(String inputFileName){
	    File file = new File(inputFileName);
	    byte[] result = new byte[(int)file.length()];
	    
	    try {	    	
		      InputStream input = null;
		      
		      try 
		      {
			        int totalBytesRead = 0;
			        input = new BufferedInputStream(new FileInputStream(file));
			        while(totalBytesRead < result.length){
				          int bytesRemaining = result.length - totalBytesRead;
				          //input.read() returns -1, 0, or more :
				          int bytesRead = input.read(result, totalBytesRead, bytesRemaining); 
				          if (bytesRead > 0){
				            totalBytesRead = totalBytesRead + bytesRead;
				          }
			        }  
		      }
		      finally { 
		        input.close();
		      }
	    }
	    catch (FileNotFoundException ex) {
	    	Log.e(Define.tag, ex.getMessage());
	    }
	    catch (IOException ex) {
	    	Log.e(Define.tag, ex.getMessage());
	    }
	    return result;
	  } 
 
	  /**
	   * Write a byte array to the given file. 
	   * @param input
	   * @param outputFileName
	   */
	void writeBinaryFile(byte[] input, String outputFileName){
	    try {
	      OutputStream output = null;
	      try {
	        output = new BufferedOutputStream(new FileOutputStream(outputFileName));
	        output.write(input);
	      }
	      finally {
	        output.close();
	      }
	    }
	    catch(FileNotFoundException ex){
	    	Log.e(Define.tag, ex.getMessage());
	    }
	    catch(IOException ex){
	    	Log.e(Define.tag, ex.getMessage());
	    }
	  }
	  
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
	  
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();	// hex	// string	// constant

	/**
	 * convert byte array to hex string
	 * https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-
	 * to-a-hex-string-in-java
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/* Listen for activity results*/
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back. 
		String strBinaryInformation;

    	//File browser result
    	if (requestCode == Define.REQUEST_FILE_EXPLORER_ACTION_LEFT){
    		if (resultCode == RESULT_OK) {
    			currentFileName = data.getStringExtra("GetFileName"); //file name
    			currentFilePath = data.getStringExtra("GetPath");     //file path
    			currentFileAbsolutePath = currentFilePath + "/" + currentFileName; //file absolute path
    			editDirectoryPath1.setText(currentFileAbsolutePath);
    			File file = new File(currentFileAbsolutePath);
            	if(file.exists() && file.isFile()) {
            		
            		byte temptemplate1[];
            		temptemplate1 = new byte[Define.tmplSize];

            		temptemplate1 = readBinaryFile(currentFileAbsolutePath);
            		strBinaryInformation = byteToHex(temptemplate1);
            		System.arraycopy(temptemplate1, 0, lefttemplate, 0, temptemplate1.length);
            		
            		editTemplateBinary1.setText(strBinaryInformation);
            	}
            	else {
            		//Toast.makeText(mContext, 
        			//		"Please select binary file.", Toast.LENGTH_SHORT).show();
            	}

    			//Log.i(tag, "currentFileAbsolutePath : " + currentFileAbsolutePath);
    		}
    	 }

    	//File browser result
    	if (requestCode == Define.REQUEST_FILE_EXPLORER_ACTION_RIGHT){
    		if (resultCode == RESULT_OK) {
    			currentFileName = data.getStringExtra("GetFileName"); //file name
    			currentFilePath = data.getStringExtra("GetPath");     //file path
    			currentFileAbsolutePath = currentFilePath + "/" + currentFileName; //file absolute path    	 
    			editDirectoryPath2.setText(currentFileAbsolutePath);

    			try{
    				File file = new File(currentFileAbsolutePath);
                	if(file.exists() && file.isFile()) {
                		
                		byte temptemplate2[];
                		temptemplate2 = new byte[Define.tmplSize];

                		temptemplate2 = readBinaryFile(currentFileAbsolutePath);
                		strBinaryInformation = byteToHex(temptemplate2);
                		System.arraycopy(temptemplate2, 0, righttemplate, 0, temptemplate2.length);

                		editTemplateBinary2.setText(strBinaryInformation); 
                	}
                	else {
                		//Toast.makeText(mContext, 
            			//		"Please select binary file.", Toast.LENGTH_SHORT).show();
                	}
    			}
    			catch(Exception e)
    			{
    				Toast.makeText(getBaseContext(),"Error file operation.", Toast.LENGTH_SHORT).show();
    			}
    			
    			//Log.i(tag, "currentFileAbsolutePath : " + currentFileAbsolutePath);
    		}
    	 }
    	
    	fileselecting = 0;
    }
	
	
	/**
	 * hide soft keyboard
	 * @param view
	 */
	public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
	 
	 /**
	  * show soft keyboard
	  * @param view
	  */
	public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
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
		}
		catch(Exception e)
		{
			Toast.makeText(getBaseContext(),"Error save template to file " + filename, Toast.LENGTH_SHORT).show();
			return -1;
		}
		//Toast.makeText(getBaseContext(),"Success save template to file " + filename,Toast.LENGTH_SHORT).show();
		return 0;
	}	

	/**
	 * read template from file
	 * @param filename file name
	 * @param tmpl  template
	 * @return success 0, failure -1
	 */
	public int ReadtemplateFromFile(String filename, byte[] tmpl)
	{
		// TODO Auto-generated method stub
		try 
		{
			File f =  new File(filename);
			FileInputStream fis = new FileInputStream(f);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(tmpl);           
            bis.close();
            fis.close();
    		
		}catch (Exception e){
            e.printStackTrace();
			Toast.makeText(getBaseContext(),"Error read template from file " + filename, Toast.LENGTH_SHORT).show();
            return -1;
        }		
		//Toast.makeText(getBaseContext(),"Success read template from file " + filename,Toast.LENGTH_SHORT).show();
		return 0;
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

	int calcSum(byte[] buf, int len)
	{
        int i = 0;
        int sum = 0;
        for(i = 0; i < len-1; i++)
        {
        	sum += buf[i];
        }
        sum = ~sum;
        return sum;
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

}

