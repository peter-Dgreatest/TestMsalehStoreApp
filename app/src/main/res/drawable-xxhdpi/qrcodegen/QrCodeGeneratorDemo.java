/* 
 * QR Code generator demo (Java)
 * 
 * Run this command-line program with no arguments. The program creates/overwrites a bunch of
 * PNG and SVG files in the current working directory to demonstrate the creation of QR Codes.
 * 
 * Copyright (c) Project Nayuki. (MIT License)
 * https://www.nayuki.io/page/qr-code-generator-library
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The Software is provided "as is", without warranty of any kind, express or
 *   implied, including but not limited to the warranties of merchantability,
 *   fitness for a particular purpose and noninfringement. In no event shall the
 *   authors or copyright holders be liable for any claim, damages or other
 *   liability, whether in an action of contract, tort or otherwise, arising from,
 *   out of or in connection with the Software or the use or other dealings in the
 *   Software.
 */

package com.itcrusaders.msaleh.qrcodegen;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


public final class QrCodeGeneratorDemo {
	
	// The main application program.
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Bitmap main(String[] args) throws IOException {
		return doBasicDemo();
		//doVarietyDemo();
		//doSegmentDemo();
		//doMaskDemo();
	}

	Context context;

	public QrCodeGeneratorDemo(Context context){
		this.context=context;
		this.cw = new ContextWrapper(context);
	}

	ContextWrapper cw;
	
	/*---- Demo suite ----*/
	
	// Creates a single QR Code, then writes it to a PNG file and an SVG file.
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	private Bitmap doBasicDemo() throws IOException {
		QrCode.Ecc errCorLvl = QrCode.Ecc.LOW;  // Error correction level
		
		QrCode qr = QrCode.encodeText("", errCorLvl);  // Make the QR Code symbol
		
		Bitmap img = qr.toImage(10, 4);           // Convert to bitmap image




		File directory = new File(Environment.getExternalStorageDirectory()+"/imageDir/");

        if(!directory.exists()){
            //directory.mkdirs();
            Log.d("directory",directory.getAbsolutePath());
			boolean uj = directory.mkdirs();
			Log.d("directory making",uj+" "+directory.isDirectory());

		}

		File imgFile = new File(directory,"hello-world-QR.png");   // File path for output
		if(!imgFile.exists()){
			Log.d("file output path",imgFile.toString());
			FileOutputStream fos =null;
			try{
				fos = new FileOutputStream(imgFile);
				img.compress(Bitmap.CompressFormat.PNG,100,fos);	// Write image to file
				fos.flush();
				fos.close();
			}catch (IOException ex){
				ex.printStackTrace();
			}
		}
		return img;
	}

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
	
	// Creates a variety of QR Codes that exercise different features of the library, and writes each one to file.
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	private  void doVarietyDemo() throws IOException {
		QrCode qr;
		
		// Numeric mode encoding (3.33 bits per digit)
		qr = QrCode.encodeText("314159265358979323846264338327950288419716939937510", QrCode.Ecc.MEDIUM);
		writePng(qr.toImage(13, 1), "pi-digits-QR.png");
		
		// Alphanumeric mode encoding (5.5 bits per character)
		qr = QrCode.encodeText("DOLLAR-AMOUNT:$39.87 PERCENTAGE:100.00% OPERATIONS:+-*/", QrCode.Ecc.HIGH);
		writePng(qr.toImage(10, 2), "alphanumeric-QR.png");
		
		// Unicode text as UTF-8
		qr = QrCode.encodeText("????????????wa???????????? ????????", QrCode.Ecc.QUARTILE);
		writePng(qr.toImage(10, 3), "unicode-QR.png");
		
		// Moderately large QR Code using longer text (from Lewis Carroll's Alice in Wonderland)
		qr = QrCode.encodeText(
			"Alice was beginning to get very tired of sitting by her sister on the bank, "
			+ "and of having nothing to do: once or twice she had peeped into the book her sister was reading, "
			+ "but it had no pictures or conversations in it, 'and what is the use of a book,' thought Alice "
			+ "'without pictures or conversations?' So she was considering in her own mind (as well as she could, "
			+ "for the hot day made her feel very sleepy and stupid), whether the pleasure of making a "
			+ "daisy-chain would be worth the trouble of getting up and picking the daisies, when suddenly "
			+ "a White Rabbit with pink eyes ran close by her.", QrCode.Ecc.HIGH);
		writePng(qr.toImage(6, 10), "alice-wonderland-QR.png");
	}
	
	
	// Creates QR Codes with manually specified segments for better compactness.
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private  void doSegmentDemo() throws IOException {
		QrCode qr;
		List<QrSegment> segs;
		
		// Illustration "silver"
		String silver0 = "THE SQUARE ROOT OF 2 IS 1.";
		String silver1 = "41421356237309504880168872420969807856967187537694807317667973799";
		qr = QrCode.encodeText(silver0 + silver1, QrCode.Ecc.LOW);
		writePng(qr.toImage(10, 3), "sqrt2-monolithic-QR.png");
		
		segs = Arrays.asList(
			QrSegment.makeAlphanumeric(silver0),
			QrSegment.makeNumeric(silver1));
		qr = QrCode.encodeSegments(segs, QrCode.Ecc.LOW);
		writePng(qr.toImage(10, 3), "sqrt2-segmented-QR.png");
		
		// Illustration "golden"
		String golden0 = "Golden ratio ?? = 1.";
		String golden1 = "6180339887498948482045868343656381177203091798057628621354486227052604628189024497072072041893911374";
		String golden2 = "......";
		qr = QrCode.encodeText(golden0 + golden1 + golden2, QrCode.Ecc.LOW);
		writePng(qr.toImage(8, 5), "phi-monolithic-QR.png");
		
		segs = Arrays.asList(
			QrSegment.makeBytes(golden0.getBytes(StandardCharsets.UTF_8)),
			QrSegment.makeNumeric(golden1),
			QrSegment.makeAlphanumeric(golden2));
		qr = QrCode.encodeSegments(segs, QrCode.Ecc.LOW);
		writePng(qr.toImage(8, 5), "phi-segmented-QR.png");
		
		// Illustration "Madoka": kanji, kana, Cyrillic, full-width Latin, Greek characters
		String madoka = "??????????????????????????????????????????????????????????????????????????????????";
		qr = QrCode.encodeText(madoka, QrCode.Ecc.LOW);
		writePng(qr.toImage(9, 4), "madoka-utf8-QR.png");
		
//		segs = Arrays.asList(QrSegmentAdvanced.makeKanji(madoka));
		qr = QrCode.encodeSegments(segs, QrCode.Ecc.LOW);
		writePng(qr.toImage(9, 4), "madoka-kanji-QR.png");
	}
	
	
	// Creates QR Codes with the same size and contents but different mask patterns.
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private  void doMaskDemo() throws IOException {
		QrCode qr;
		List<QrSegment> segs;
		
		// Project Nayuki URL
		segs = QrSegment.makeSegments("https://www.nayuki.io/");
		qr = QrCode.encodeSegments(segs, QrCode.Ecc.HIGH, QrCode.MIN_VERSION, QrCode.MAX_VERSION, -1, true);  // Automatic mask
		writePng(qr.toImage(8, 6), "project-nayuki-automask-QR.png");
		qr = QrCode.encodeSegments(segs, QrCode.Ecc.HIGH, QrCode.MIN_VERSION, QrCode.MAX_VERSION, 3, true);  // Force mask 3
		writePng(qr.toImage(8, 6), "project-nayuki-mask3-QR.png");
		
		// Chinese text as UTF-8
		segs = QrSegment.makeSegments("???????????????Wikipedia?????????i/??w??k?????pi??di.??/????????????????????????????????????????????????????????????????????????????????????");
		qr = QrCode.encodeSegments(segs, QrCode.Ecc.MEDIUM, QrCode.MIN_VERSION, QrCode.MAX_VERSION, 0, true);  // Force mask 0
		writePng(qr.toImage(10, 3), "unicode-mask0-QR.png");
		qr = QrCode.encodeSegments(segs, QrCode.Ecc.MEDIUM, QrCode.MIN_VERSION, QrCode.MAX_VERSION, 1, true);  // Force mask 1
		writePng(qr.toImage(10, 3), "unicode-mask1-QR.png");
		qr = QrCode.encodeSegments(segs, QrCode.Ecc.MEDIUM, QrCode.MIN_VERSION, QrCode.MAX_VERSION, 5, true);  // Force mask 5
		writePng(qr.toImage(10, 3), "unicode-mask5-QR.png");
		qr = QrCode.encodeSegments(segs, QrCode.Ecc.MEDIUM, QrCode.MIN_VERSION, QrCode.MAX_VERSION, 7, true);  // Force mask 7
		writePng(qr.toImage(10, 3), "unicode-mask7-QR.png");
	}

	/*---- Utilities ----*/
	
	// Helper function to reduce code duplication.
	private  void writePng(Bitmap img, String filepath) throws IOException {
		//ImageIO.write(img, "png", new File(filepath));
		File directory = new File(Environment.getExternalStorageDirectory()+"/imageDir/");

		if(!directory.exists()){
		    directory.mkdirs();
        }
		File imgFile = new File(directory,"hello-world-QR.png");   // File path for output
		if(!imgFile.exists()){
			Log.d("file output path",imgFile.toString());
			FileOutputStream fos =null;
			try{
				fos = new FileOutputStream(imgFile);
				img.compress(Bitmap.CompressFormat.PNG,100,fos);	// Write image to file
				fos.flush();
				fos.close();
			}catch (IOException ex){
				ex.printStackTrace();
			}
		}
	}
	
}
