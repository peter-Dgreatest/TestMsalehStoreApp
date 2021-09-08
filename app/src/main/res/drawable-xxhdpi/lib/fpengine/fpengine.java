package com.itcrusaders.msaleh.lib.fpengine;

import android.util.Log;

public class fpengine 
{
	///////////////  algorithm  //////////////////////////////////////////
	public native static int initialize(byte version[]);
	public native static int terminate();
	public native static int capture(byte imgData[]);
	public native static int enroll(int EnrolStep, int TargetID, byte MergeUsedCnt[]);
	public native static int identify(byte SuccessID[]);
	public native static int delete(int delStartID, int delCnt);
	public native static int deleteAll();
	public native static int emptyId(byte emptyId[]);
	public native static int readTemp(int id, byte tmpl[]);
	public native static int writeTemp(int id, byte tmpl[], byte returnID[]);
	
	public native static int img2wsq(byte img[], byte wsq[], byte wsqlen[]);
	public native static int wsq2img(byte img[], byte wsq[], int wsqlen);

	public native static int setSecure(int secureLevel);
	public native static int getSecure();
	public native static int setDup(int enDup);
	public native static int getDup();

	public native static int FFStore(int RamAddr);
	public native static int FFRead(int RamAddr, byte tmpl[]);
	public native static int FFGenerate(int RamAddr, int GenCount);
	public native static int FFUpdate(byte p_Learned[]);
	public native static int verifyGA(byte tmpl[]);

	public native static int getISO2011Tmpl(byte tmpl[], byte len[], byte quality[]);
	public native static int getISO2005Tmpl(byte tmpl[], byte len[], byte quality[]);
	public native static int getANSITmpl(byte tmpl[], byte len[], byte quality[]);
	public native static int verifyISO(byte tmpl1[], byte tmpl2[]);
	public native static int verifyANSI(byte tmpl1[], byte tmpl2[]);

	///////////////  load library  /////////////////////////////////////////
	static
	{
		try
		{
			System.loadLibrary("telpo_android_FPH");
			Log.i("BIOOFLY", "libtelpo_android_FPH.so loaded");
		}
		catch(UnsatisfiedLinkError err)
		{
			Log.e("BIOOFLY", "libtelpo_android_FPH.so  no found");
		}
		
		try
		{	
			System.loadLibrary("soengine");
			Log.i("BIOOFLY", "libsoengine.so loaded");
		}
		catch(UnsatisfiedLinkError err)
		{
			Log.e("BIOOFLY", "libsoengine.so  no found");
		}
		
	/*	try
		{	
			System.loadLibrary("fp_fph");
			Log.i("BIOOFLY", "libfp_fph.so loaded");
		}
		catch(UnsatisfiedLinkError err)
		{
			Log.e("BIOOFLY", "libfp_fph.so  no found");
		}*/
	}
}
