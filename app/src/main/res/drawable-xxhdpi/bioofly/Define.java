package com.itcrusaders.msaleh.bioofly;

public class Define 
{
	public static int READ_IMAGE_WIDTH = 208;//256;//120;//160;				// image width
	public static int READ_IMAGE_HEIGHT = 288;//360;//120;//160;			// image height
	public static int READ_IMAGE_WIDTH_ZHUHAI = 242;	//120;//160;				// image width
	public static int READ_IMAGE_HEIGHT_ZHUHAI = 266;	//120;//160;			// image height
	public static int READ_IMAGE_FRICTION = (4 - READ_IMAGE_WIDTH % 4) % 4;
	public static int READ_IMAGE_SIZE = READ_IMAGE_WIDTH * READ_IMAGE_HEIGHT;	//59904 + 1080;	//14400;//25600; 			// image length = 118*110
	public static int READ_IMAGE_SIZE_ZHUHAI = READ_IMAGE_WIDTH_ZHUHAI * READ_IMAGE_HEIGHT_ZHUHAI;	//59904 + 1080;	//14400;//25600;			// crop image length = 100*100
	public final static String __EXTENSION_TEMPLATE = "tmpl";
	public final static String __EXTENSION_IMG = "bmp";
	public final static String __EXTENSION_WSQ = "wsq";
	
	//	data output store position
//	public final static String mSDCardRoot = "/storage/sdcard0/";//Environment.getExternalStorageDirectory().getAbsolutePath();
//	public final static String mSDCardImg = "/storage/sdcard0/telpo/img";
//	public final static String mSDCardTmpl = "/storage/sdcard0/telpo/tmpl";
//	public final static String mSDCardNotDelete = "/storage/sdcard0/telpo/notdelete";
	public final static String mSDCardRoot = "/sdcard/";//Environment.getExternalStorageDirectory().getAbsolutePath();
	public final static String mSDCardImg = "/sdcard/telpo/img";
	public final static String mSDCardTmpl = "/sdcard/telpo/tmpl";
	public final static String mSDCardNotDelete = "/sdcard/telpo/notdelete";
    
	//	init global variables
	public static final String tag = "BIOOFLY";
	public static final String comm_mode = "USB";	//	"SPI";
	public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	public static final int CAPTURE_MODE_USB = 0x55;
	public static final int CAPTURE_MODE_SPI = 0x00;
    
	public static final int templateSize = 512;
	public static final int tmplSize = 512;

	//	File browser action constant
	public static final int REQUEST_DIRECTORY_EXPLORER_ACTION_LEFT = 10;
	public static final int REQUEST_DIRECTORY_EXPLORER_ACTION_RIGHT = 11;
	public static final int REQUEST_FILE_EXPLORER_ACTION_LEFT = 20;
	public static final int REQUEST_FILE_EXPLORER_ACTION_RIGHT = 21;
	public static final int SELECT_IMG2WSQ_IMGFILE = 31;
	public static final int SELECT_WSQ2IMG_WSQFILE = 32;
	public static final int SELECT_VERIFYGA_TMPLFILE = 33;
	
}
