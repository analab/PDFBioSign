package com.analab.pdfbiosign;

import android.content.Context;
import android.graphics.RectF;
import android.util.Log;
import android.view.*;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.MuPDFPageView;

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

	public static void setListeners(MuPDFPageView pageView) {
		pageView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				((MuPDFPageView) v).canvas.mX = event.getX();
				((MuPDFPageView) v).canvas.mY = event.getY();
				return false;
			}
		});
		
		pageView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO: convert
				MuPDFPageView pv = (MuPDFPageView)v;
				float scale = pv.mSourceScale * (float)pv.getWidth()/(float)pv.mSize.x;
				final float docRelX = (pv.canvas.mX - pv.getLeft())/scale;
				final float docRelY = (pv.canvas.mY - pv.getTop())/scale;
				
				return true;
			}
		});
	}
}
