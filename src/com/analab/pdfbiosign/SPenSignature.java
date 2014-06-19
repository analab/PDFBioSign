package com.analab.pdfbiosign;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.samsung.samm.common.SObjectStroke;
import com.samsung.spen.settings.SettingStrokeInfo;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.SPenTouchListener;

public class SPenSignature extends Activity {

	public class TabletPoint {
		private float x, y, pressure;
		private long time;

		public float getX() {
			return x;
		}

		public void setX(float x) {
			this.x = x;
		}

		public float getY() {
			return y;
		}

		public void setY(float y) {
			this.y = y;
		}

		public float getPressure() {
			return pressure;
		}

		public void setPressure(float pressure) {
			this.pressure = pressure;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}
	}

	private final String TAG = "SPen";
	private SCanvasView mSCanvas;
	private Context mContext = null;
	private SettingStrokeInfo mStrokeInfoPen;

	private ArrayList<ArrayList<TabletPoint>> traceCollection = null;
	private ArrayList<TabletPoint> currentTrace = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		setContentView(R.layout.spen_signdoc);
		mSCanvas = (SCanvasView) findViewById(R.id.scanvas);
		Resources res = getResources();
		Drawable dLine = res.getDrawable(R.drawable.signing_dialog_background);
		mSCanvas.setBackgroundDrawable(dLine);
		mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_NONE);

		mStrokeInfoPen = new SettingStrokeInfo();
		mStrokeInfoPen.setStrokeStyle(SObjectStroke.SAMM_STROKE_STYLE_PENCIL);
		mStrokeInfoPen.setStrokeColor(Color.BLUE);
		mStrokeInfoPen.setStrokeWidth(6);

		// PenSettingInfo pen = mSCanvas.getPenSettingInfo();
		// pen.setPenWidth(5);
		// pen.setPenColor(Color.BLUE);
		// mSCanvas.setPenSettingInfo(pen);

		// --------------------------------------------
		// Set S pen Touch Listener
		// --------------------------------------------
		mSCanvas.setSPenTouchListener(new SPenTouchListener() {

			@Override
			public boolean onTouchFinger(View view, MotionEvent event) {
				// Update Current Color
				// if (mCurrentTool != TOOL_FINGER) {
				// mCurrentTool = TOOL_FINGER;
				//
				// if (event.getAction() == MotionEvent.ACTION_DOWN)
				// mSCanvas.setSettingViewStrokeInfo(mStrokeInfoFinger);
				// }
				return true; // dispatch event to SCanvasView for NOT drawing
			}

			@Override
			public boolean onTouchPen(View view, MotionEvent event) {

				mSCanvas.setSettingViewStrokeInfo(mStrokeInfoPen);

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mSCanvas.setSettingViewStrokeInfo(mStrokeInfoPen);
					// start of one trace here
					if (currentTrace == null) {
						currentTrace = new ArrayList<TabletPoint>();
					}
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// end of trace
					traceCollection.add(currentTrace);
					currentTrace = null;
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (currentTrace != null) {
						TabletPoint point = new TabletPoint();
						point.setX(event.getX());
						point.setY(event.getY());
						point.setPressure(event.getPressure());
						point.setTime(new Date().getTime());
						currentTrace.add(point);
						// minX = ((int)event.getX() < minX) ? (int)event.getX()
						// : minX;
						// minY = ((int)event.getY() < minY) ? (int)event.getY()
						// : minY;
						// maxX = ((int)event.getX() > maxX) ? (int)event.getX()
						// : maxX;
						// maxY = ((int)event.getY() > maxY) ? (int)event.getY()
						// : maxY;
					}
				}

				return false; // dispatch event to SCanvasView for drawing
			}

			@Override
			public void onTouchButtonUp(View view, MotionEvent event) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTouchButtonDown(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onTouchPenEraser(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return false;
			}

		});
	}

	public void onOKClicked(View v) {

		Bitmap bmp = mSCanvas.getBitmap(true);
		// UNCOMMENT FOR SIGNATURE CROPING !!!!!
		/*
		 * int w = (maxX - minX) >= bmp.getWidth() ? bmp.getWidth() -2 : (maxX -
		 * minX); int h = (maxY - minY) >= bmp.getHeight()? bmp.getHeight() - 2
		 * : (maxY - minY); log.i( "Signature min X: " + (minX )); log.i(
		 * "Signature min Y: " + (minY )); log.i( "Signature W:" + w); log.i(
		 * "Signature H:" + h);
		 * 
		 * // motion event returns negative coordinates when going out of view
		 * minX = minX < 0 ? 0 : minX; minY = minY < 0 ? 0 : minY;
		 * 
		 * Bitmap finalBmp = Bitmap.createBitmap(bmp,minX, minY,w , h);
		 * ByteArrayOutputStream os = new ByteArrayOutputStream();
		 * finalBmp.compress(Bitmap.CompressFormat.PNG, 100, os);
		 */
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, os);

		byte[] signatureBitmap = os.toByteArray();
		bmp.recycle();

		// place bmp back to intent result
		Intent intent = new Intent();
		intent.putExtra("bmp", signatureBitmap);

		// ...and biometric data after normalization
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		// TODO: Normalize biometric info for current DPI towards 300DPI
		// standard

			// TabletTraceCollection normalized = TabletTraceCollection
			// //.normalize(getTraceCollection(), metrics.densityDpi, 80);
			// .normalize(getTraceCollection(), 300,metrics.densityDpi);

       //FIXME: This is not STANDARD biometric binary format
		byte[] signatureBiometrics = traceCollection.toString().getBytes();
		
		intent.putExtra("sig", signatureBiometrics); 
		setResult(RESULT_OK, intent);
		finish();
	}

	public void onClearClicked(View v) {
		mSCanvas.clearAll(true);
		traceCollection = new ArrayList<ArrayList<TabletPoint>>();
		currentTrace = null;
		// minX = minY = Integer.MAX_VALUE;
		// maxX = maxY = 0;
	}

	public void onCancelClicked(View v) {
		Intent intent = new Intent();
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Release SCanvasView resources
		if (!mSCanvas.closeSCanvasView())
			Log.e(TAG, "Fail to close SCanvasView");
	}

	@Override
	public void onStart() {
		super.onStart();
		traceCollection = new ArrayList<ArrayList<TabletPoint>>();
	}
}
