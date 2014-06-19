package com.analab.pdfbiosign;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.MuPDFPageView;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;

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
