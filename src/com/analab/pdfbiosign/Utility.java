package com.analab.pdfbiosign;

import android.content.Context;
import android.graphics.RectF;
import android.util.Log;

import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.TextWord;

public class Utility {
	private MuPDFCore mCore;
	private final String TAG = "PDFBioSign:Utility";
	private TextWord[][] words;
	private int page = -1;
	
	public Utility(MuPDFCore core) {
		mCore = core;
		if (core != null)
			page = mCore.countPages();
	}
	
	public Utility(Context context, String filename) {
		try {
			mCore = new MuPDFCore(context, filename);
		} catch (Exception e) {
			Log.e(TAG, "", e);
			mCore = null;
		}
		
		if (mCore != null)
			page = mCore.countPages() - 1;
	}
	
	
	
	public void search() {
		RectF[] rs = mCore.searchPage(page, "{PDFBioSign");
		
		for (RectF r : rs)
			Log.d(TAG, "Got: " + r);
	}
}
