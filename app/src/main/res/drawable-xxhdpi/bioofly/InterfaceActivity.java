package com.itcrusaders.msaleh.bioofly;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.itcrusaders.msaleh.R;
import com.itcrusaders.msaleh.lib.fpengine.fpengine;

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

public class InterfaceActivity extends Activity {

	// Directory browser action constant

	String currentFileName = ""; // file name
	String currentFilePath = ""; // file path
	String currentFileAbsolutePath = ""; // file path
	int left_or_right = 0;
	int captureNum = 0;
	public static int fileselecting = 0;

	// init components
	ImageView imgFP;
	EditText editDirectoryPath1; // directory path
	EditText editDirectoryPath2; // directory path
	EditText editTemplateBinary; // binary file information

	// private String mfileName, mFileName;
	int fileNum = 0;
	private Context mContext = this;

	// for process bitmap image
	Bitmap mBmpFPImage;
	BitmapHeader_8Bit BH_8;
	static byte mByteReadImageData[];
	static byte mByteDrawImageData[];
	static byte mByteWSQImageData[];
	static byte mByteTemplate[];

	static int mStartID = 1;
	static int mCount = 1;
	static int mEnrollStep = 0;
	static int mTargetID = 1;
	static int mTmp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_interface);

		editDirectoryPath1 = (EditText) findViewById(R.id.editDirPath1);
		editDirectoryPath2 = (EditText) findViewById(R.id.editDirPath2);
		editTemplateBinary = (EditText) findViewById(R.id.editTemplateBinary);
		imgFP = (ImageView) findViewById(R.id.imageViewEnrollFP);

		// Define.READ_IMAGE_WIDTH = Define.READ_IMAGE_WIDTH_ZHUHAI;
		// Define.READ_IMAGE_HEIGHT = Define.READ_IMAGE_HEIGHT_ZHUHAI;
		// Define.READ_IMAGE_FRICTION = (4 - Define.READ_IMAGE_WIDTH % 4) % 4;
		// Define.READ_IMAGE_SIZE = Define.READ_IMAGE_WIDTH *
		// Define.READ_IMAGE_HEIGHT;

		mByteReadImageData = new byte[Define.READ_IMAGE_SIZE];
		mByteDrawImageData = new byte[Define.READ_IMAGE_HEIGHT * (Define.READ_IMAGE_WIDTH + Define.READ_IMAGE_FRICTION)
				+ 1078];
		mByteTemplate = new byte[Define.tmplSize];

		byte version[];
		version = new byte[30];

		// hide software keyboard
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// initialize fingerprint engine
		// fpengine.initialize(version);
		// String ver = new String(version);
		// Log.i(Define.tag, "version : " + ver);
		// Toast.makeText(mContext, "version : " + ver,
		// Toast.LENGTH_SHORT).show();

		findViewById(R.id.btn_Capture).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				int retVal = 100, limitCnt = 0;
				while (retVal > 0) {
					retVal = fpengine.capture(mByteReadImageData);
					Log.d(Define.tag, "btn_Capture fpengine.capture() result = " + retVal);
					limitCnt++;
					if (limitCnt > 3) {
						Log.d(Define.tag, "btn_Capture failure.");
						editDirectoryPath1.setText("btn_Capture failure.");
						break;
					}
				}

				captureNum = 0;

				makeBmpData();
				mBmpFPImage = BitmapFactory.decodeByteArray(mByteDrawImageData, 0, mByteDrawImageData.length);
				imgFP.setImageBitmap(mBmpFPImage);
				fileNum++;
				String tmplFileName = Define.mSDCardImg + "/image_" + fileNum + "." + Define.__EXTENSION_IMG;
				SaveToFile(tmplFileName, mByteDrawImageData);

			}
		});

		findViewById(R.id.btn_Enroll).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				byte MergeUsedCnt[];
				MergeUsedCnt = new byte[4];

				mEnrollStep++;
				int ret = fpengine.enroll(mEnrollStep, mTargetID, MergeUsedCnt);
				Log.i(Define.tag, "mEnrollStep : " + String.valueOf(mEnrollStep));

				if (ret > 0) {
					Toast.makeText(mContext, "Failure enroll " + String.valueOf(mEnrollStep) + ".", Toast.LENGTH_SHORT)
							.show();
					mEnrollStep--;
					Log.i(Define.tag, "Failure enroll step " + String.valueOf(mEnrollStep) + ".");
					// SoundFailure();

				}

				if (ret == 0) {
					if (mEnrollStep < 3) {
						Toast.makeText(mContext, "Successfully enroll " + String.valueOf(mEnrollStep) + ".",
								Toast.LENGTH_SHORT).show();
						Log.i(Define.tag, "Success enroll step " + String.valueOf(mEnrollStep));
					}
					if (mEnrollStep == 3) {
						Toast.makeText(mContext, "Successfully enroll completed.", Toast.LENGTH_SHORT).show();
						Log.i(Define.tag, "Success enroll completelly.");
						mEnrollStep = 0;
					}
					// SoundSuccess();

				}

			}
		});

		findViewById(R.id.btn_Identify).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				byte SuccessID[];
				SuccessID = new byte[4];

				int ret = fpengine.identify(SuccessID);
				Log.i(Define.tag, String.valueOf(SuccessID[0] & 0xff) + "   " + String.valueOf(SuccessID[1] & 0xff));
				Log.i(Define.tag, String.valueOf(SuccessID[2] & 0xff) + "   " + String.valueOf(SuccessID[3] & 0xff));

				int sucsID = (SuccessID[0] & 0xff) * 256 + (SuccessID[1] & 0xff);
				if (ret > 0) {
					Toast.makeText(mContext, "Failure identify...", Toast.LENGTH_SHORT).show();
					// SoundFailure();
				} else {
					Toast.makeText(mContext, "Successfully identified. User ID " + String.valueOf(sucsID),
							Toast.LENGTH_SHORT).show();
					// SoundSuccess();
				}
			}
		});

		findViewById(R.id.btn_EmptyID).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				byte emptyID[];
				emptyID = new byte[2];

				int ret = fpengine.emptyId(emptyID);

				if (ret > 0) {
					Toast.makeText(mContext, "Failure get empty ID...", Toast.LENGTH_SHORT).show();
					// SoundFailure();
				} else {
					mTargetID = (emptyID[1] & 0xff) * 256 + emptyID[0] & 0xff;
					Toast.makeText(mContext, "Successfully getted. Empty ID " + String.valueOf(mTargetID),
							Toast.LENGTH_SHORT).show();
					// SoundSuccess();
				}

			}
		});

		findViewById(R.id.btn_Delete).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				selectDeleteID();
			}
		});

		findViewById(R.id.btn_DeleteAll).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (fpengine.deleteAll() == 0) {
					Toast.makeText(mContext, "Successfullly deleted.", Toast.LENGTH_SHORT).show();
					// SoundSuccess();
				} else {
					Toast.makeText(mContext, "Failure delete...", Toast.LENGTH_SHORT).show();
					// SoundFailure();
				}
			}
		});

		findViewById(R.id.btn_ReadTempData).setOnClickListener(new Button.OnClickListener() {
			public void onClick(final View v) {

				AlertDialog.Builder ad = new AlertDialog.Builder(InterfaceActivity.this);

				ad.setTitle("Input source ID for read");
				ad.setMessage("please input id(0, 1, 2, ...)");

				final EditText et = new EditText(InterfaceActivity.this);
				et.setInputType(InputType.TYPE_CLASS_NUMBER);
				et.setText("1");
				et.selectAll();

				// showing the soft keyboard
				final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				// imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

				ad.setView(et);

				/* cancel button */
				ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// hiding the soft keyboard
						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						dialog.dismiss();
					}
				});

				/* ok button */
				ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

						String value = et.getText().toString();

						currentFilePath = Define.mSDCardTmpl + "/tmplRead_" + value + "." + Define.__EXTENSION_TEMPLATE;
						editDirectoryPath1.setText(currentFilePath);
						Log.v(Define.tag, "PATH = " + currentFilePath);

						byte tmplReadData[];
						tmplReadData = new byte[Define.templateSize];

						fpengine.readTemp(Integer.valueOf(value), tmplReadData);

						SaveToFile(currentFilePath, tmplReadData);

						String strBinaryInformation = byteToHex(tmplReadData);
						editTemplateBinary.setText(strBinaryInformation);

						dialog.dismiss();
					}
				});

				ad.show();
			}
		});

		findViewById(R.id.btn_WriteTempData).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				// initialize file constants
				currentFileName = ""; // file name
				currentFilePath = ""; // file path
				currentFileAbsolutePath = ""; // file path
				editDirectoryPath2.setText("");

				// get directory path
				left_or_right = Define.REQUEST_FILE_EXPLORER_ACTION_RIGHT;
				getFilePath(left_or_right, Define.__EXTENSION_TEMPLATE); // get
																			// file
																			// information(path
																			// and
																			// name)
				Log.v(Define.tag, "getFilePath() process");

				// while(fileselecting == 1){}

				AlertDialog.Builder ad = new AlertDialog.Builder(InterfaceActivity.this);

				ad.setTitle("Input destinate ID for write");
				ad.setMessage("Input id (0,1,2, ...)");

				final EditText et = new EditText(InterfaceActivity.this);
				et.setInputType(InputType.TYPE_CLASS_NUMBER);
				et.setText(String.valueOf(1));
				et.selectAll();
				// showing the soft keyboard
				final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				// imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

				ad.setView(et);

				ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// hiding the soft keyboard
						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						dialog.dismiss();
					}
				});

				// Ok buttton click
				ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						byte existID[];
						existID = new byte[2];

						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

						String value = et.getText().toString();

						// file validation
						File f = new File(currentFileAbsolutePath);
						if (!f.exists() || f.isDirectory()) {
							Log.e(Define.tag, "currentFileAbsolutePath = " + currentFileAbsolutePath);
							Toast.makeText(mContext, "File does not exist", Toast.LENGTH_SHORT).show();
							return;
						}

						byte tmplWriteData[];
						tmplWriteData = new byte[Define.templateSize];
						// String tmplFileName = Define.mSDCardNotDelete +
						// "/templateLib." + Define.__EXTENSION_TEMPLATE;
						String tmplFileName = currentFileAbsolutePath;
						Log.d(Define.tag, "SavefileName = " + currentFileAbsolutePath);

						ReadtemplateFromFile(tmplFileName, tmplWriteData);

						byte tmplrealWriteData[];
						tmplrealWriteData = new byte[Define.templateSize];

						System.arraycopy(tmplWriteData, 0, tmplrealWriteData, 0, tmplWriteData.length);

						if (fpengine.writeTemp(Integer.parseInt(value), tmplWriteData, existID) != 0) {
							Toast.makeText(mContext,
									"ID " + String
											.valueOf((int) existID[0] + (existID[1] * 256) + " is already exist."),
									Toast.LENGTH_SHORT).show();
							String strBinaryInformation = byteToHex(tmplWriteData);
							editTemplateBinary.setText(strBinaryInformation);
						} else
							Toast.makeText(mContext,
									"ID " + String.valueOf(value) + " Template was successfully saved.",
									Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				});

				ad.show();

			}
		});

		findViewById(R.id.btn_GetISO2005Tmpl).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder ad = new AlertDialog.Builder(InterfaceActivity.this);

				ad.setTitle("SaveFileName");
				ad.setMessage("please input new template filename...");

				final EditText et = new EditText(InterfaceActivity.this);
				// et.setInputType(InputType.TYPE_CLASS_NUMBER);
				et.setText("tmpl_");
				et.selectAll();

				// showing the soft keyboard
				final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

				ad.setView(et);

				/* cancel button */
				ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// hiding the soft keyboard
						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						dialog.dismiss();
					}
				});

				/* ok button */
				ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

						String filename = et.getText().toString();

						String tmplFileName = Define.mSDCardTmpl + "/" + filename + "(ISO2005)."
								+ Define.__EXTENSION_TEMPLATE;
						byte tmplReadData[];
						byte[] length = new byte[2];
						byte[] quality = new byte[1];

						tmplReadData = new byte[Define.tmplSize];
						fpengine.getISO2005Tmpl(tmplReadData, length, quality);

						SaveToFile(tmplFileName, tmplReadData);

						String strBinaryInformation = byteToHex(tmplReadData);
						editTemplateBinary.setText(strBinaryInformation);

						dialog.dismiss();
					}
				});

				ad.show();

			}
		});

		findViewById(R.id.btn_GetISO2011Tmpl).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder ad = new AlertDialog.Builder(InterfaceActivity.this);

				ad.setTitle("SaveFileName");
				ad.setMessage("please input new template filename...");

				final EditText et = new EditText(InterfaceActivity.this);
				// et.setInputType(InputType.TYPE_CLASS_NUMBER);
				et.setText("tmpl_");
				et.selectAll();

				// showing the soft keyboard
				final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

				ad.setView(et);

				/* cancel button */
				ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// hiding the soft keyboard
						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						dialog.dismiss();
					}
				});

				/* ok button */
				ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

						String filename = et.getText().toString();

						String tmplFileName = Define.mSDCardTmpl + "/" + filename + "(ISO2011)."
								+ Define.__EXTENSION_TEMPLATE;
						byte tmplReadData[];
						byte[] length = new byte[2];
						byte[] quality = new byte[1];

						tmplReadData = new byte[Define.tmplSize];
						fpengine.getISO2011Tmpl(tmplReadData, length, quality);

						SaveToFile(tmplFileName, tmplReadData);

						String strBinaryInformation = byteToHex(tmplReadData);
						editTemplateBinary.setText(strBinaryInformation);

						dialog.dismiss();
					}
				});

				ad.show();

			}
		});
		findViewById(R.id.btn_GetANSITmpl).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder ad = new AlertDialog.Builder(InterfaceActivity.this);

				ad.setTitle("SaveFileName");
				ad.setMessage("please input new template filename...");

				final EditText et = new EditText(InterfaceActivity.this);
				// et.setInputType(InputType.TYPE_CLASS_NUMBER);
				et.setText("tmpl_");
				et.selectAll();

				// showing the soft keyboard
				final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

				ad.setView(et);

				/* cancel button */
				ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// hiding the soft keyboard
						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						dialog.dismiss();
					}
				});

				/* ok button */
				ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

						String filename = et.getText().toString();

						String tmplFileName = Define.mSDCardTmpl + "/" + filename + "(ANSI)."
								+ Define.__EXTENSION_TEMPLATE;
						byte tmplReadData[];
						byte[] length = new byte[2];
						byte[] quality = new byte[1];

						tmplReadData = new byte[Define.tmplSize];
						fpengine.getANSITmpl(tmplReadData, length, quality);

						SaveToFile(tmplFileName, tmplReadData);

						String strBinaryInformation = byteToHex(tmplReadData);
						editTemplateBinary.setText(strBinaryInformation);

						dialog.dismiss();
					}
				});

				ad.show();

			}
		});
		findViewById(R.id.btn_ToWSQ).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// initialize file constants
				currentFileName = ""; // file name
				currentFilePath = ""; // file path
				currentFileAbsolutePath = ""; // file path
				editDirectoryPath2.setText("");

				// get directory path
				left_or_right = Define.SELECT_IMG2WSQ_IMGFILE;
				getFilePath(left_or_right, Define.__EXTENSION_IMG); // get file
																	// information(path
																	// and name)

			}
		});
		findViewById(R.id.btn_FromWSQ).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// initialize file constants
				currentFileName = ""; // file name
				currentFilePath = ""; // file path
				currentFileAbsolutePath = ""; // file path
				editDirectoryPath2.setText("");

				// get directory path
				left_or_right = Define.SELECT_WSQ2IMG_WSQFILE;
				getFilePath(left_or_right, Define.__EXTENSION_WSQ); // get file
																	// information(path
																	// and name)
			}
		});
		findViewById(R.id.btn_SetSecure).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder ad = new AlertDialog.Builder(InterfaceActivity.this);

				ad.setTitle("Input secureLevel.");
				ad.setMessage("Please input secureLevel(0,1,2) ...");

				final EditText et = new EditText(InterfaceActivity.this);
				et.setInputType(InputType.TYPE_CLASS_NUMBER);
				et.setText(String.valueOf(1));
				et.selectAll();
				// showing the soft keyboard
				final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				// imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

				ad.setView(et);

				ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// hiding the soft keyboard
						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						dialog.dismiss();
					}
				});

				// Ok buttton click
				ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						int secureLevel;

						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

						String value = et.getText().toString();
						secureLevel = Integer.valueOf(value);
						if (fpengine.setSecure(secureLevel) == 0) {
							Toast.makeText(mContext, "Success set secureLevel !", Toast.LENGTH_SHORT).show();
						} else
							Toast.makeText(mContext, "Failure set secureLevel ...", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				});

				ad.show();

			}
		});
		findViewById(R.id.btn_GetSecure).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				int secureLevel = 0;
				secureLevel = fpengine.getSecure();
				Log.d(Define.tag, "secureLevel = " + String.valueOf(secureLevel));
				Toast.makeText(mContext, "SecureLevel is " + String.valueOf(secureLevel) + ".", Toast.LENGTH_SHORT)
						.show();
			}
		});
		findViewById(R.id.btn_SetDup).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder ad = new AlertDialog.Builder(InterfaceActivity.this);

				ad.setTitle("Set Enable Duplication.");
				ad.setMessage("Please select ...");

				final CheckBox cb = new CheckBox(InterfaceActivity.this);
				cb.setChecked(true);

				ad.setView(cb);

				ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				// Ok buttton click
				ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						int dupEn;

						if (cb.isChecked())
							dupEn = 1;
						else
							dupEn = 0;
						fpengine.setDup(dupEn);
						if (dupEn == 0) {
							Toast.makeText(mContext, "Duplicate disabled !", Toast.LENGTH_SHORT).show();
						} else
							Toast.makeText(mContext, "Duplicate Enabled", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				});

				ad.show();
			}
		});
		findViewById(R.id.btn_GetDup).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// int dupEn = 0;
				int ret = fpengine.getDup();
				if (ret == 1) {
					Log.d(Define.tag, "Dupicate enabled.");
					Toast.makeText(mContext, "Dupicate enabled.", Toast.LENGTH_SHORT).show();
				} else {
					Log.d(Define.tag, "Dupicate diabled.");
					Toast.makeText(mContext, "Dupicate diabled.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		findViewById(R.id.btn_FFStore).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder ad = new AlertDialog.Builder(InterfaceActivity.this);

				ad.setTitle("Input ram addr number.");
				ad.setMessage("Please input number(0,1,2) ...");

				final EditText et = new EditText(InterfaceActivity.this);
				et.setInputType(InputType.TYPE_CLASS_NUMBER);
				et.setText(String.valueOf(0));
				et.selectAll();
				// showing the soft keyboard
				final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				// imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

				ad.setView(et);

				ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// hiding the soft keyboard
						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						dialog.dismiss();
					}
				});

				// Ok buttton click
				ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						int ramAddr;
						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

						String value = et.getText().toString();
						ramAddr = Integer.valueOf(value);
						if (fpengine.FFStore(ramAddr) == 0) {
							Toast.makeText(mContext, "Success FF_STORE !", Toast.LENGTH_SHORT).show();
						} else
							Toast.makeText(mContext, "Failure FF_STORE ...", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				});

				ad.show();
			}
		});
		findViewById(R.id.btn_FFRead).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder ad = new AlertDialog.Builder(InterfaceActivity.this);

				ad.setTitle("Input ram addr number.");
				ad.setMessage("Please input number(0,1,2) ...");

				final EditText et = new EditText(InterfaceActivity.this);
				et.setInputType(InputType.TYPE_CLASS_NUMBER);
				et.setText(String.valueOf(0));
				et.selectAll();
				// showing the soft keyboard
				final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				// imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

				ad.setView(et);

				ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// hiding the soft keyboard
						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						dialog.dismiss();
					}
				});

				// Ok buttton click
				ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

						int ramAddr;
						byte[] tmpl;
						tmpl = new byte[Define.templateSize];

						String value = et.getText().toString();
						try {
							ramAddr = Integer.parseInt(value);
						} catch (NumberFormatException nfe) {
							System.out.println("Could not parse " + nfe);
							ramAddr = 0;
						}
						// Log.d(Define.tag, "------------ ramAddr = " +
						// String.valueOf(ramAddr));

						if (fpengine.FFRead(ramAddr, tmpl) == 0) {
							String tmplname = Define.mSDCardTmpl + "/tmpl_FFRead_" + value + "."
									+ Define.__EXTENSION_TEMPLATE;
							// Log.d(Define.tag, "------------ tmplname = " +
							// tmplname);

							SaveToFile(tmplname, tmpl);

							// Log.d(Define.tag, "------------ SaveToFile");

							String strBinaryInformation = byteToHex(tmpl);
							editTemplateBinary.setText(strBinaryInformation);

							Toast.makeText(mContext, "Success FF_READ and store !", Toast.LENGTH_SHORT).show();
						} else
							Toast.makeText(mContext, "Failure FF_READ ...", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				});

				ad.show();
			}
		});
		findViewById(R.id.btn_FFGenerate).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				selectFFGenerateID();
			}
		});
		findViewById(R.id.btn_FFUpdate).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				int ret = 0;
				byte[] learned;
				learned = new byte[4];

				ret = fpengine.FFUpdate(learned);

				if (ret == 0) {
					if (learned[0] == 0 || learned[1] == 0) {
						Log.d(Define.tag, "Success! Updated.");
						Toast.makeText(mContext, "Update success ! Updated.", Toast.LENGTH_SHORT).show();
					} else {
						Log.d(Define.tag, "Success! But not updated.");
						Toast.makeText(mContext, "Update success ! but not updated.", Toast.LENGTH_SHORT).show();
					}
				} else {
					Log.d(Define.tag, "Failure!");
					Toast.makeText(mContext, "Update failure.", Toast.LENGTH_SHORT).show();
				}

			}
		});
		findViewById(R.id.btn_VerifyGA).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// initialize file constants
				currentFileName = ""; // file name
				currentFilePath = ""; // file path
				currentFileAbsolutePath = ""; // file path
				editDirectoryPath2.setText("");

				// get directory path
				left_or_right = Define.SELECT_VERIFYGA_TMPLFILE;
				getFilePath(left_or_right, Define.__EXTENSION_TEMPLATE); // get
																			// file
																			// information(path
																			// and
																			// name)
			}
		});

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

		/* Select capture processing */
		/*
		 * if (id == R.id.action_interface_test) { Toast.makeText(mContext,
		 * "Interface test screen...", Toast.LENGTH_SHORT).show(); Intent intent
		 * = new Intent(MainActivityPrint.this, InterfaceActivity.class);
		 * InterfaceActivity.this.startActivity(intent); return true; }
		 */

		/* Select main screen */

		if (id == R.id.action_demo_main_screen) {
			// Toast.makeText(mContext, "Demo main screen ...",
			// Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(InterfaceActivity.this, MainActivity.class);
			InterfaceActivity.this.startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Browse storage and get directory path
	 * 
	 * @param view
	 *            View control
	 */
	public void getFilePath(int left_right, String sFileExtension) {
		// Toast.makeText(mContext, "File browser selected",
		// Toast.LENGTH_SHORT).show();
		fileselecting = 1;
		Intent intent1 = new Intent(this, FileChooser.class);
		intent1.putExtra("fileExtension", sFileExtension);
		startActivityForResult(intent1, left_right);
	}

	/**
	 * Read the given binary file, and return its contents as a byte array.
	 * 
	 * @param inputFileName
	 *            input file name(path + name)
	 * @return byte array
	 */
	byte[] readBinaryFile(String inputFileName) {
		File file = new File(inputFileName);
		byte[] result = new byte[(int) file.length()];
		try {
			InputStream input = null;

			try {
				int totalBytesRead = 0;
				input = new BufferedInputStream(new FileInputStream(file));

				while (totalBytesRead < result.length) {
					int bytesRemaining = result.length - totalBytesRead;
					// input.read() returns -1, 0, or more :
					int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
					if (bytesRead > 0) {
						totalBytesRead = totalBytesRead + bytesRead;
					}
				}
			} finally {
				input.close();
			}
		} catch (FileNotFoundException ex) {
			Log.e(Define.tag, ex.getMessage());
		} catch (IOException ex) {
			Log.e(Define.tag, ex.getMessage());
		}
		return result;
	}

	/**
	 * Write a byte array to the given file.
	 * 
	 * @param input
	 * @param outputFileName
	 */
	void writeBinaryFile(byte[] input, String outputFileName) {
		try {
			OutputStream output = null;
			try {
				output = new BufferedOutputStream(new FileOutputStream(outputFileName));
				output.write(input);
			} finally {
				output.close();
			}
		} catch (FileNotFoundException ex) {
			Log.e(Define.tag, ex.getMessage());
		} catch (IOException ex) {
			Log.e(Define.tag, ex.getMessage());
		}
	}

	/**
	 * convert byte array to hex string
	 * https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-
	 * to-a-hex-string-in-java
	 * 
	 * @param hash
	 * @return
	 */
	public String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();// hex
																			// string
																			// constant

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

	/* Listen for activity results */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// See which child activity is calling us back.
		String strBinaryInformation;

		// Directory browser result
		if (requestCode == Define.REQUEST_DIRECTORY_EXPLORER_ACTION_LEFT) {
			if (resultCode == RESULT_OK) {
				// currentFileName = data.getStringExtra("GetFileName"); //file
				// name
				currentFilePath = data.getStringExtra("GetPath"); // file path
				currentFileAbsolutePath = currentFilePath;// + currentFileName;
															// //file absolute
															// path
				editDirectoryPath1.setText(currentFilePath);
				// Log.i(tag, "currentFileAbsolutePath : " + currentFilePath);
			}
		}
		if (requestCode == Define.REQUEST_DIRECTORY_EXPLORER_ACTION_RIGHT) {
			if (resultCode == RESULT_OK) {
				// currentFileName = data.getStringExtra("GetFileName"); //file
				// name
				currentFilePath = data.getStringExtra("GetPath"); // file path
				currentFileAbsolutePath = currentFilePath + "/";// +
																// currentFileName;
																// //file
																// absolute path
				editDirectoryPath2.setText(currentFilePath);
				// Log.i(tag, "currentFileAbsolutePath : " + currentFilePath);
			}
		}

		// File browser result
		if (requestCode == Define.REQUEST_FILE_EXPLORER_ACTION_RIGHT) {
			if (resultCode == RESULT_OK) {
				currentFileName = data.getStringExtra("GetFileName"); // file
																		// name
				currentFilePath = data.getStringExtra("GetPath"); // file path
				currentFileAbsolutePath = currentFilePath + "/" + currentFileName; // file
																					// absolute
																					// path
				editDirectoryPath2.setText(currentFileAbsolutePath);

				try {
					File file = new File(currentFileAbsolutePath);
					if (file.exists() && file.isFile()) {

						byte temptemplate2[];
						temptemplate2 = new byte[Define.tmplSize];

						temptemplate2 = readBinaryFile(currentFileAbsolutePath);
						strBinaryInformation = byteToHex(temptemplate2);
						System.arraycopy(temptemplate2, 0, mByteTemplate, 0, temptemplate2.length);

						editTemplateBinary.setText(strBinaryInformation);
					} else {
						// Toast.makeText(mContext,
						// "Please select binary file.",
						// Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					Toast.makeText(getBaseContext(), "Error file operation.", Toast.LENGTH_SHORT).show();
				}

				// Log.i(tag, "currentFileAbsolutePath : " +
				// currentFileAbsolutePath);
			}
		}

		if (requestCode == Define.SELECT_IMG2WSQ_IMGFILE) {
			if (resultCode == RESULT_OK) {
				currentFileName = data.getStringExtra("GetFileName"); // file
																		// name
				currentFilePath = data.getStringExtra("GetPath"); // file path
				currentFileAbsolutePath = currentFilePath + "/" + currentFileName; // file
																					// absolute
																					// path
				editDirectoryPath2.setText(currentFileAbsolutePath);

				int i = 0, j = 0;
				mByteWSQImageData = new byte[Define.READ_IMAGE_SIZE];
				byte[] wsqlen = new byte[4];

				String tmplFileName = currentFileAbsolutePath;
				ReadtemplateFromFile(tmplFileName, mByteDrawImageData);

				for (i = 0; i < Define.READ_IMAGE_HEIGHT; i++)
					for (j = 0; j < Define.READ_IMAGE_WIDTH; j++)
						mByteReadImageData[i * Define.READ_IMAGE_WIDTH
								+ j] = mByteDrawImageData[i * (Define.READ_IMAGE_WIDTH + Define.READ_IMAGE_FRICTION) + j
										+ 1078];

				if (fpengine.img2wsq(mByteReadImageData, mByteWSQImageData, wsqlen) == 0) {
					int wsq_length = (int) (wsqlen[0] & 0xff) + (int) (wsqlen[1] & 0xff) * 256
							+ (int) (wsqlen[2] & 0xff) * 256 * 256 + (int) (wsqlen[3] & 0xff) * 256 * 256 * 256;
					// Log.d(Define.tag, "wsq_length = " +
					// String.valueOf(wsq_length));
					// Log.d(Define.tag, "len[0] = " + String.valueOf(wsqlen[0])
					// + " -> " + String.valueOf(wsqlen[0] & 0xff));
					// Log.d(Define.tag, "len[1] = " + String.valueOf(wsqlen[1])
					// + " -> " + String.valueOf(wsqlen[1] & 0xff));
					// Log.d(Define.tag, "len[2] = " + String.valueOf(wsqlen[2])
					// + " -> " + String.valueOf(wsqlen[2] & 0xff));
					// Log.d(Define.tag, "len[3] = " + String.valueOf(wsqlen[3])
					// + " -> " + String.valueOf(wsqlen[3] & 0xff));

					byte[] tmplReadData;
					tmplReadData = new byte[wsq_length];

					System.arraycopy(mByteWSQImageData, 0, tmplReadData, 0, wsq_length);

					currentFilePath = currentFileAbsolutePath + ".wsq";
					SaveToFile(currentFilePath, tmplReadData);
				}

			}
		}
		if (requestCode == Define.SELECT_WSQ2IMG_WSQFILE) {
			if (resultCode == RESULT_OK) {
				currentFileName = data.getStringExtra("GetFileName"); // file
																		// name
				currentFilePath = data.getStringExtra("GetPath"); // file path
				currentFileAbsolutePath = currentFilePath + "/" + currentFileName; // file
																					// absolute
																					// path
				editDirectoryPath2.setText(currentFileAbsolutePath);

				String tmplFileName = currentFileAbsolutePath;

				File file = new File(tmplFileName);
				if (file.exists()) {
					long wsqfileLength = file.length();
					mByteWSQImageData = new byte[(int) wsqfileLength];

					ReadtemplateFromFile(tmplFileName, mByteWSQImageData);
					int wsqlen = mByteWSQImageData.length;
					Log.d(Define.tag, "wsqlen : " + String.valueOf(wsqlen));

					if (fpengine.wsq2img(mByteReadImageData, mByteWSQImageData, wsqlen) == 0) {
						makeBmpData();
						currentFilePath = currentFileAbsolutePath + "." + Define.__EXTENSION_IMG;
						SaveToFile(currentFilePath, mByteDrawImageData);
						mBmpFPImage = BitmapFactory.decodeByteArray(mByteDrawImageData, 0, mByteDrawImageData.length);
						imgFP.setImageBitmap(mBmpFPImage);
					}
				}

			}
		}
		if (requestCode == Define.SELECT_VERIFYGA_TMPLFILE) {
			if (resultCode == RESULT_OK) {
				currentFileName = data.getStringExtra("GetFileName"); // file
																		// name
				currentFilePath = data.getStringExtra("GetPath"); // file path
				currentFileAbsolutePath = currentFilePath + "/" + currentFileName; // file
																					// absolute
																					// path
				editDirectoryPath2.setText(currentFileAbsolutePath);

				String tmplFileName = currentFileAbsolutePath;

				File file = new File(tmplFileName);
				if (file.exists()) {
					byte[] mtmpl;
					mtmpl = new byte[Define.templateSize];

					ReadtemplateFromFile(tmplFileName, mtmpl);

					String BinaryInformation = byteToHex(mtmpl);

					editTemplateBinary.setText(BinaryInformation);

					if (fpengine.verifyGA(mtmpl) == 0) {
						Toast.makeText(mContext, "Success verify.", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(mContext, "Failure veryfy...", Toast.LENGTH_SHORT).show();
					}

				}

			}
		}

		fileselecting = 0;
	}

	/**
	 * hide soft keyboard
	 * 
	 * @param view
	 */
	public void hideSoftKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	/**
	 * show soft keyboard
	 * 
	 * @param view
	 */
	public void showSoftKeyboard(View view) {
		if (view.requestFocus()) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	/**
	 * select id to delete
	 */
	public void selectDeleteID() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		final View dialogView = inflater.inflate(R.layout.delete_select_dialog, null);
		dialogBuilder.setView(dialogView);
		final EditText edt_Start = (EditText) dialogView.findViewById(R.id.edt_StartID);
		final EditText edt_Count = (EditText) dialogView.findViewById(R.id.edt_Count);
		final CheckBox chk_All = (CheckBox) dialogView.findViewById(R.id.chk_All);
		edt_Start.setText(String.valueOf(mTargetID));
		edt_Count.setText("1");
		chk_All.setChecked(false);
		int position = edt_Start.length();
		Editable etext = edt_Start.getText();
		Selection.setSelection(etext, position);
		position = edt_Count.length();
		etext = edt_Count.getText();
		Selection.setSelection(etext, position);

		dialogBuilder.setTitle("Enter start id and count");
		dialogBuilder.setMessage("Please enter start ID and count below...");
		dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				mStartID = Integer.valueOf(edt_Start.getText().toString());
				mCount = Integer.valueOf(edt_Count.getText().toString());

				if (chk_All.isChecked() == true)
					if (fpengine.deleteAll() == 0) {
						Toast.makeText(mContext, "Successfullly deleted.", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(mContext, "Failure delete...", Toast.LENGTH_SHORT).show();
					}
				else {
					if (fpengine.delete(mStartID, mCount) == 0) {
						Toast.makeText(mContext, "Successfullly deleted.", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(mContext, "Failure delete...", Toast.LENGTH_SHORT).show();
					}
				}

			}
		});
		dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// pass
				Drawable drawableTmp = getResources().getDrawable(R.drawable.fpsensor);
				imgFP.setImageDrawable(drawableTmp);
			}
		});
		AlertDialog b = dialogBuilder.create();
		b.show();

	}

	/**
	 * select id to delete
	 */
	public void selectFFGenerateID() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		final View dialogView = inflater.inflate(R.layout.ff_generate_id_dialog, null);
		dialogBuilder.setView(dialogView);
		final EditText edt_RamAddr = (EditText) dialogView.findViewById(R.id.edt_RamAddrID);
		final EditText edt_GenCount = (EditText) dialogView.findViewById(R.id.edt_GenCount);
		edt_RamAddr.setText("0");
		edt_GenCount.setText("2");
		int position = edt_RamAddr.length();
		Editable etext = edt_RamAddr.getText();
		Selection.setSelection(etext, position);
		position = edt_GenCount.length();
		etext = edt_GenCount.getText();
		Selection.setSelection(etext, position);

		dialogBuilder.setTitle("Enter start id and count");
		dialogBuilder.setMessage("Please enter start ID and count ...");
		dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				int ramAddr = 0;
				int genCount = 2;
				try {
					ramAddr = Integer.parseInt(edt_RamAddr.getText().toString());
					genCount = Integer.parseInt(edt_GenCount.getText().toString());
				} catch (Exception e) {
					Log.d(Define.tag, e.toString());
					ramAddr = 0;
					genCount = 2;
				}
				if (fpengine.FFGenerate(ramAddr, genCount) == 0) {
					Toast.makeText(mContext, "Success FFGenerate.", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, "Failure FFGenerate...", Toast.LENGTH_SHORT).show();
				}

			}
		});
		dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		AlertDialog b = dialogBuilder.create();
		b.show();

	}

	/**
	 * make bitmap data
	 */
	public void makeBmpData() {
		BH_8 = new BitmapHeader_8Bit(Define.READ_IMAGE_WIDTH, Define.READ_IMAGE_HEIGHT);

		for (int i = 0; i < 1078; i++)
			mByteDrawImageData[i] = BH_8.BMP_HEADER[i];

		for (int i = 0; i < Define.READ_IMAGE_HEIGHT; i++) {
			for (int j = 0; j < (Define.READ_IMAGE_WIDTH + Define.READ_IMAGE_FRICTION); j++) {
				if (j >= Define.READ_IMAGE_WIDTH) {
					mByteDrawImageData[i * (Define.READ_IMAGE_WIDTH + Define.READ_IMAGE_FRICTION) + j + 1078] = 0x00;
				} else {
					mByteDrawImageData[i * (Define.READ_IMAGE_WIDTH + Define.READ_IMAGE_FRICTION) + j
							+ 1078] = mByteReadImageData[i * Define.READ_IMAGE_WIDTH + j];
				}
			}
		}
		return;
	}

	/**
	 * save to file
	 * 
	 * @param filename
	 *            file name
	 * @param tmpl
	 *            template
	 * @return success 0, failure -1
	 */
	public int SaveToFile(String filename, byte[] tmpl) {
		// TODO Auto-generated method stub
		File tmplFile = null;
		try {
			tmplFile = new File(filename);
			tmplFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(tmplFile);
			BufferedOutputStream myOutWriter = new BufferedOutputStream(fOut);
			myOutWriter.write(tmpl);
			myOutWriter.close();
			fOut.close();
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), "Error save template to file " + filename, Toast.LENGTH_SHORT).show();
			return -1;
		}
		Toast.makeText(getBaseContext(), "Success save template to file " + filename, Toast.LENGTH_SHORT).show();
		return 0;
	}

	/**
	 * read template from file
	 * 
	 * @param filename
	 *            file name
	 * @param tmpl
	 *            template
	 * @return success 0, failure -1
	 */
	public int ReadtemplateFromFile(String filename, byte[] tmpl) {
		// TODO Auto-generated method stub
		try {
			File f = new File(filename);
			FileInputStream fis = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(tmpl);
			bis.close();
			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getBaseContext(), "Error read template from file " + filename, Toast.LENGTH_SHORT).show();
			return -1;
		}
		Toast.makeText(getBaseContext(), "Success read template from file " + filename, Toast.LENGTH_SHORT).show();
		return 0;
	}

}
