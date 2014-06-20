package com.analab.pdfbiosign;

import java.io.File;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.artifex.mupdfdemo.*;
import com.itextpdf.text.Image;

public class MainActivity extends Activity {
	private static final int FILE_CHOOSER = 11;
	private static final int ACTIVITY_CHOOSE_FILE = 88;
	private static final int CHOOSE_FILE_REQUESTCODE = 0;
	public static final int DIALOG_SIGN = 55;
	public static final int DIALOG_SIGN_LONG = 66;
	private final String TAG = "PDFBioSign";
	private String mPath;
	private MuPDFCore mPdfCore;
	private Intent intent = getIntent();

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);

		intent = getIntent();

		Log.d(TAG, "Intent action: " + intent.getAction());
		Log.d(TAG, "Intent data: " + intent.getData());

		/*
		 * Uri uri = intent.getData(); if (uri == null) {
		 * Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/" +
		 * getString(R.string.pdf_file)); } if
		 * (uri.toString().startsWith("content://media/external/file")) { Cursor
		 * cursor = getContentResolver().query(uri, new String[] { "_data" },
		 * null, null, null); if (cursor.moveToFirst()) { uri =
		 * Uri.parse(cursor.getString(0)); } }
		 */

		Log.d(TAG, "Opening file");

		/*
		 * Intent chooseFile; Intent intent2; chooseFile = new
		 * Intent(Intent.ACTION_GET_CONTENT); chooseFile.setType("file/*");
		 * intent2 = Intent.createChooser(chooseFile, "Choose a file");
		 * startActivityForResult(intent2, ACTIVITY_CHOOSE_FILE);
		 */

		// mPath = Environment.getExternalStorageDirectory().getPath() + "/" +
		// getString(R.string.pdf_file);
		// mPath = Environment.getExternalStorageDirectory().getPath() + "/" +
		// "Analab/AcroMaker.pdf";
		// mPath = Environment.getExternalStorageDirectory().getPath() + "/" +
		// "external_SD/AcroMaker.pdf";
		// mPath = "/storage/sdcard0/external_SD/AcroMaker.pdf";

		/*
		 * mPdfCore = openFile(mPath);
		 * 
		 * 
		 * Log.d(TAG, "Creating view");
		 * 
		 * //View v = new PDFBioSignView(getBaseContext(), mPdfCore); //ListView
		 * v = (ListView) getLayoutInflater().inflate(R.id.listView1, null);
		 * //ListView v = (ListView) findViewById(R.id.listView1); ListView v =
		 * new ListView(this);
		 * 
		 * Log.d(TAG, "Setting adapter"); v.setAdapter(new
		 * MuPDFPageAdapter(getBaseContext(), null, mPdfCore));
		 * 
		 * Log.d(TAG, "Creating utility"); Utility u = new Utility(mPdfCore);
		 * String[] s = new String[1]; s[0] = "{PDFBioSign:sign:mysign}";
		 * u.search(s);
		 * 
		 * 
		 * 
		 * //setContentView(R.layout.activity_main); setContentView(v);
		 */

		// setContentView(new MuPDFPageView(getBaseContext(), null, mPdfCore,
		// new Point(0,0), ));
		/*
		 * Uri uri2 =
		 * Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/" +
		 * getString(R.string.pdf_file)); Intent intent2 = new Intent(this,
		 * MuPDFActivity.class); intent2.setAction(Intent.ACTION_VIEW);
		 * intent2.setData(uri2); startActivity(intent2);
		 */
	}

	private void showPdf(String path) {

		mPath = path;
		mPdfCore = openFile(path);

		Log.d(TAG, "Creating view");

		// View v = new PDFBioSignView(getBaseContext(), mPdfCore);
		// ListView v = (ListView) getLayoutInflater().inflate(R.id.listView1,
		// null);
		// ListView v = (ListView) findViewById(R.id.listView1);
		RelativeLayout l = new RelativeLayout(this);
		ListView v = new ListView(this);

		Log.d(TAG, "Setting adapter");
		v.setAdapter(new MuPDFPageAdapter(this, null, mPdfCore));

		Log.d(TAG, "Creating utility");
		Utility u = new Utility(mPdfCore);
		String[] s = new String[1];
		s[0] = "{PDFBioSign:sign:mysign}";
		u.search(s);

		// setContentView(R.layout.activity_main);
		l.addView(v);
		setContentView(l);
	}

	private MuPDFCore openFile(String path) {

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Trying to open " + path);
		}

		try {
			mPdfCore = new MuPDFCore(getBaseContext(), path);
		} catch (Exception e) {
			Log.e(TAG, "", e);
			return null;
		}
		return mPdfCore;
	}

	public void signDocument() throws NoSuchAlgorithmException {
		Log.d(TAG, "Signing document");
		// final String RESOURCE = "/mnt/sdcard/sign.gif";
		// final String RESOURCE =
		// Environment.getExternalStorageDirectory().getPath() + "/" +
		// "Analab/sign.gif";
		final String RESOURCE = "/storage/sdcard0/external_SD/sign.gif";

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024, new SecureRandom());
		KeyPair pair = keyGen.generateKeyPair();
		PrivateKey priv = pair.getPrivate();
		PublicKey pub = pair.getPublic();
		byte[] bio = { 'f', 'd', 'a', 'b', 'a', 'd', 'f', 'b', 'a', 'b', 'f',
				'a', 'b', 'f', 'a' };
		try {
			AcroMaker.SignDocument(mPath, "mysign",
					mPath.substring(0, mPath.length() - 4) + "_signed.pdf",
					Image.getInstance(RESOURCE), bio, priv);
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}
		Log.d(TAG, "Signed file " + RESOURCE);
		mPath = mPath.substring(0, mPath.length() - 4) + "_signed.pdf";
		/*
		 * File from = new File(mPath + ".new"); File to = new File(mPath);
		 * from.renameTo(to); Log.d(TAG, "Renaming file"); finish();
		 * //startActivity(getIntent());
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.sign:
			try {
				signDocument();
				showPdf(mPath);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		case R.id.open:
			Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);

			intent2.setType("*/*");
			intent2.addCategory(Intent.CATEGORY_OPENABLE);

			// special intent for Samsung file manager
			Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
			// if you want any file type, you can skip next line
			sIntent.putExtra("CONTENT_TYPE", "file/*");
			sIntent.addCategory(Intent.CATEGORY_DEFAULT);

			Intent chooserIntent;
			if (getPackageManager().resolveActivity(sIntent, 0) != null) {
				// it is device with samsung file manager
				chooserIntent = Intent.createChooser(sIntent, "Open file");
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
						new Intent[] { intent2 });
			} else {
				chooserIntent = Intent.createChooser(intent2, "Open file");
			}
			Log.d(TAG, "Intent2 action: " + intent2.getAction());
			Log.d(TAG, "Intent data: " + intent2.getData());
			try {
				startActivityForResult(chooserIntent, CHOOSE_FILE_REQUESTCODE);
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(getApplicationContext(),
						"No suitable File Manager was found.",
						Toast.LENGTH_SHORT).show();
			}
			return true;

		case R.id.dialog: {

			Intent intent = new Intent(this, SPenSignature.class);

			// the return code for the result is based on formula =>

			// (page*100 + field)

			startActivityForResult(intent, DIALOG_SIGN);

			showPdf(mPath);
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case CHOOSE_FILE_REQUESTCODE:
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				Log.d(TAG, "intent 2 " + uri.getPath());
				String mPath = uri.getPath();
				showPdf(mPath);

			}
			break;
		case DIALOG_SIGN:
			if (resultCode == Activity.RESULT_OK) {

				Bundle extras = data.getExtras();

				byte[] sig_bio = null, sig_bmp = null;

				Bitmap bmp = null;

				sig_bmp = extras.getByteArray("bmp");
				sig_bio = extras.getByteArray("sig");

				KeyPairGenerator keyGen = null;
				try {
					keyGen = KeyPairGenerator.getInstance("RSA");
				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				keyGen.initialize(1024, new SecureRandom());
				KeyPair pair = keyGen.generateKeyPair();
				PrivateKey priv = pair.getPrivate();
				PublicKey pub = pair.getPublic();

				try {
					AcroMaker.SignDocument(mPath, "mysign",
							mPath.substring(0, mPath.length() - 4)
									+ "_signed.pdf",
							Image.getInstance(sig_bmp), sig_bio, priv);
				} catch (Exception e) {
					Log.e(TAG, "", e);
				}
				mPath = mPath.substring(0, mPath.length() - 4) + "_signed.pdf";

				showPdf(mPath);

			}

			break;

		case DIALOG_SIGN_LONG:
			if (resultCode == Activity.RESULT_OK) {

				Bundle extras = data.getExtras();

				byte[] sig_bio = null, sig_bmp = null;

				Bitmap bmp = null;
				String name;
				sig_bmp = extras.getByteArray("bmp");
				sig_bio = extras.getByteArray("sig");
				name = extras.getString("name");
				mPath=extras.getString("path");
				KeyPairGenerator keyGen = null;
				try {
					keyGen = KeyPairGenerator.getInstance("RSA");
				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				keyGen.initialize(1024, new SecureRandom());
				KeyPair pair = keyGen.generateKeyPair();
				PrivateKey priv = pair.getPrivate();
				PublicKey pub = pair.getPublic();

				try {
					AcroMaker.SignDocument(mPath, name,
							mPath.substring(0, mPath.length() - 4)
									+ "_signed.pdf",
							Image.getInstance(sig_bmp), sig_bio, priv);
				} catch (Exception e) {
					Log.e(TAG, "", e);
				}
				mPath = mPath.substring(0, mPath.length() - 4) + "_signed.pdf";

				showPdf(mPath);

			}

			break;
		}
	}
}

/*
 * class PDFBioSignView extends View { private static final String TAG =
 * "PDFBioSign"; private MuPDFCore mCore; private Bitmap mBitmap;
 * 
 * public PDFBioSignView(Context context, MuPDFCore core) { super(context);
 * mCore = core; }
 * 
 * @Override public void onDraw(Canvas canvas) { int w = canvas.getWidth(); int
 * h = canvas.getHeight();
 * 
 * Log.d(TAG, "Creating bitmap");
 * 
 * mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
 * 
 * Log.d(TAG, "Drawing page");
 * 
 * mCore.drawPage(mBitmap, mCore.countPages() - 1, w, h, 1, 1, w - 2, h - 2);
 * 
 * Log.d(TAG, "Drawing bitmap");
 * 
 * canvas.drawBitmap(mBitmap, 0, 0, null); }
 * 
 * }
 */
