package com.artifex.mupdfdemo;

import java.io.IOException;

import com.analab.pdfbiosign.AcroMaker;
import com.analab.pdfbiosign.MainActivity;
import com.analab.pdfbiosign.SPenSignature;
import com.analab.pdfbiosign.Utility;
import com.itextpdf.text.DocumentException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;

public class MuPDFPageAdapter extends BaseAdapter {
	private final Context mContext;
	private final FilePicker.FilePickerSupport mFilePickerSupport;
	private final MuPDFCore mCore;
	private final SparseArray<PointF> mPageSizes = new SparseArray<PointF>();
	private       Bitmap mSharedHqBm;

	public MuPDFPageAdapter(Context c, FilePicker.FilePickerSupport filePickerSupport, MuPDFCore core) {
		mContext = c;
		mFilePickerSupport = filePickerSupport;
		mCore = core;
	}

	public int getCount() {
		return mCore.countPages();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final MuPDFPageView pageView;
		if (convertView == null) {
			if (mSharedHqBm == null || mSharedHqBm.getWidth() != parent.getWidth() || mSharedHqBm.getHeight() != parent.getHeight())
				mSharedHqBm = Bitmap.createBitmap(parent.getWidth(), parent.getHeight(), Bitmap.Config.ARGB_8888);

			pageView = new MuPDFPageView(mContext, mFilePickerSupport, mCore, new Point(parent.getWidth(), parent.getHeight()), mSharedHqBm);
		} else {
			pageView = (MuPDFPageView) convertView;
		}

		PointF pageSize = mPageSizes.get(position);
		if (pageSize != null) {
			// We already know the page size. Set it up
			// immediately
			pageView.setPage(position, pageSize);
		} else {
			// Page size as yet unknown. Blank it for now, and
			// start a background task to find the size
			pageView.blank(position);
			AsyncTask<Void,Void,PointF> sizingTask = new AsyncTask<Void,Void,PointF>() {
				@Override
				protected PointF doInBackground(Void... arg0) {
					return mCore.getPageSize(position);
				}

				@Override
				protected void onPostExecute(PointF result) {
					super.onPostExecute(result);
					// We now know the page size
					mPageSizes.put(position, result);
					// Check that this view hasn't been reused for
					// another page since we started
					if (pageView.getPage() == position)
						pageView.setPage(position, result);
				}
			};

			sizingTask.execute((Void)null);
		}
		
		pageView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				((MuPDFPageView) v).canvas.mX = event.getX();
				((MuPDFPageView) v).canvas.mY = event.getY();
				return false;
			}
		});
		
		final MuPDFPageAdapter adp = this;
		
		pageView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO: convert
				MuPDFPageView pv = (MuPDFPageView)v;
				
				float scale = pv.mSourceScale * (float)pv.getWidth()/(float)pv.mSize.x;
				Log.d("pv source",pv.mSourceScale+"");
				Log.d("pv.getWidth()",pv.getWidth()+"");
				Log.d("pv.getHeight()",pv.getHeight()+"");
				Log.d("pv.canvas.mX",pv.canvas.mX+"");
				Log.d("pv.canvas.mY",pv.canvas.mY+"");
				Log.d("pv.getLeft()",pv.getLeft()+"");
				Log.d("pv.gettop()",pv.getTop()+"");
				Log.d("scale",scale+"");
				Log.d("mupdfpagesize", mCore.getPageSize(pv.getPage())+"");
				final float docRelX = (pv.canvas.mX /(scale))/2;
				final float docRelY = ((pv.getHeight()-pv.canvas.mY) /(scale))/2;
				String name;
				name="sign"+docRelX+""+docRelY;
				String[] mTMP={docRelX+":"+docRelY+":sign:"+name+":"+(pv.mPageNumber+1)}; 
				try {
					AcroMaker.PutAcros(pv.mCore.filename,pv.mCore.filename.substring(0, pv.mCore.filename.length()-4) + "_created.pdf",mTMP);
				} catch (IOException | DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Intent intent = new Intent(adp.mContext, SPenSignature.class);
				intent.putExtra("path",pv.mCore.filename.substring(0, pv.mCore.filename.length()-4) + "_created.pdf");
				intent.putExtra("name",name);
				Activity act = (Activity) adp.mContext;
				act.startActivityForResult(intent, MainActivity.DIALOG_SIGN_LONG);
				return true;
			}
		});
		return pageView;
	}
}
