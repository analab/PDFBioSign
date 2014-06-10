package com.analab.pdfbiosign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.artifex.mupdfdemo.FilePicker;
import com.artifex.mupdfdemo.MuPDFActivity;
import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.MuPDFPageView;

public class MainActivity extends Activity {
	private final String TAG ="PDFBioSign";
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
		
		String pathToFile = Environment.getExternalStorageDirectory().getPath() + "/" + getString(R.string.pdf_file);
		mPdfCore = openFile(pathToFile);
		
		Log.d(TAG, "Creating view");
		
		View v = new PDFBioSignView(getBaseContext(), mPdfCore);
		
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
}

class PDFBioSignView extends View {
	private static final String TAG = "PDFBioSign";
	private MuPDFCore mCore; 

	public PDFBioSignView(Context context, MuPDFCore core) {
		super(context);
		mCore = core;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		int w = canvas.getWidth();
		int h = canvas.getHeight();
		
		Log.d(TAG, "Creating bitmap");
		
		Bitmap myBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		
		Log.d(TAG, "Drawing page");
		
		mCore.drawPage(myBitmap, mCore.countPages() - 1, w, h, 1, 1, w - 2, h - 2);
		
		Log.d(TAG, "Drawing bitmap");
		
		canvas.drawBitmap(myBitmap, 0, 0, null);
	}
	
}
