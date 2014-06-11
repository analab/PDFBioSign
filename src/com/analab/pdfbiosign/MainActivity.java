package com.analab.pdfbiosign;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.os.Environment;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.artifex.mupdfdemo.*;
import com.itextpdf.text.Image;

public class MainActivity extends Activity {
	private final String TAG ="PDFBioSign";
	private String mPath;
	private MuPDFCore mPdfCore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		
		Intent intent = getIntent();
		
		Log.d(TAG, "Intent action: " + intent.getAction());
		Log.d(TAG, "Intent data: " + intent.getData());
		
		/*Uri uri = intent.getData();
		if (uri == null) {
			Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/" + getString(R.string.pdf_file));
		}
		if (uri.toString().startsWith("content://media/external/file")) {
			Cursor cursor = getContentResolver().query(uri,
					new String[] { "_data" }, null, null, null);
			if (cursor.moveToFirst()) {
				uri = Uri.parse(cursor.getString(0));
			}
		}*/
		
		Log.d(TAG, "Opening file");
		
		//mPath = Environment.getExternalStorageDirectory().getPath() + "/" + getString(R.string.pdf_file);
		mPath = "/storage/sdcard0/external_SD/AcroMaker.pdf";
		mPdfCore = openFile(mPath);
		
		Log.d(TAG, "Creating view");
		
		//View v = new PDFBioSignView(getBaseContext(), mPdfCore);
		ListView v = new ListView(getBaseContext());
		
		v.setAdapter(new MuPDFPageAdapter(getBaseContext(), null, mPdfCore));
		
		
		Utility u = new Utility(mPdfCore);
		String[] s = new String[1];
		s[0] = "{PDFBioSign:sign:mysign}";
		u.search(s);
		
		//setContentView(R.layout.activity_main);
		setContentView(v);
		
		//setContentView(new MuPDFPageView(getBaseContext(), null, mPdfCore, new Point(0,0), ));
		/*
		Uri uri2 = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/" + getString(R.string.pdf_file));
		Intent intent2 = new Intent(this, MuPDFActivity.class);
		intent2.setAction(Intent.ACTION_VIEW);
		intent2.setData(uri2);
		startActivity(intent2);*/
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
	
	public void signDocument(View view) {
		final String RESOURCE = "/storage/sdcard0/external_SD/sign.gif";
		byte[] stream = {'a', 'c', 'x', '0'};
		try {
			AcroMaker.SignDocument(mPath, "mysign", mPath + ".new", Image.getInstance(RESOURCE), stream);
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}
		File from = new File(mPath + ".new");
		File to = new File(mPath);
		from.renameTo(to);
		finish();
		startActivity(getIntent());
	}
}

class PDFBioSignView extends View {
	private static final String TAG = "PDFBioSign";
	private MuPDFCore mCore; 
	private Bitmap mBitmap;

	public PDFBioSignView(Context context, MuPDFCore core) {
		super(context);
		mCore = core;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		int w = canvas.getWidth();
		int h = canvas.getHeight();
		
		Log.d(TAG, "Creating bitmap");
		
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		
		Log.d(TAG, "Drawing page");
		
		mCore.drawPage(mBitmap, mCore.countPages() - 1, w, h, 1, 1, w - 2, h - 2);
		
		Log.d(TAG, "Drawing bitmap");
		
		canvas.drawBitmap(mBitmap, 0, 0, null);
	}
	
}
