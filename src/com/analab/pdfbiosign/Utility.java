package com.analab.pdfbiosign;

import android.content.Context;
import android.graphics.RectF;
import android.util.Log;

import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.TextWord;

public class Utility {
	private MuPDFCore mCore;
	private final String TAG = "PDFBioSign:Utility";
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
	
	
	
	public String[] search(String[] fields) {
		RectF[][] rs = new RectF[fields.length][];
		String[] ret;
		int count = 0;
		int i;
		
		for (i = 0; i < fields.length; ++i) {
			rs[i] = mCore.searchPage(page, fields[i]);
			count += rs[i].length;
		}
		
		i = 0;
		ret = new String[count];
		
		for (int j = 0; j < fields.length; ++j) {
			for (RectF rect : rs[i]) {
				ret[i++] = "" + rect.left + ":" + rect.top + fields[j].substring(11, fields[j].length() - 1);
				Log.d(TAG, "Got string: " + ret[i - 1]);
			}
		}
		return ret;
	}
}
