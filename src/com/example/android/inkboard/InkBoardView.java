package com.example.android.inkboard;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class InkBoardView extends View{
	
	private static final boolean DRAW_DEBUG = false;
	
	private static final boolean KEY_EVENT_DEBUG = true;
	
	private Paint mPaint;
	
	private List<Point> mPoints;
	
	private List<Path> mPaths;

	private final static String TAG = "InkBoardView";
	
	private long timeDown;
	
	private long timeUp;
	
	private InkBoardDialog mDialog;
	
	private Path mPath;

	public InkBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.setBackgroundColor(0x00000000);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		//this.setBackgroundColor(Color.TRANSPARENT);
		
		mPoints = new ArrayList<Point>();
		mPaths = new ArrayList<Path>();
		mPath = new Path();
		
		mPaint = new Paint();
		mPaint.setColor(Color.BLUE);
		mPaint.setAlpha(255);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(3);
		mPaint.setAntiAlias(true);
		mPaint.setPathEffect(new CornerPathEffect(10));
	}
	
	public void setDialog(InkBoardDialog dialog) {
		mDialog = dialog;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(1000, 1000);
	}
	
	@Override
	public void onDraw(Canvas canvas) {

		if(mPoints.size() != 0) {
			mPath.moveTo(mPoints.get(0).x, mPoints.get(0).y);
		}
		for(Point next: mPoints) {
			canvas.drawPoint(next.x, next.y, mPaint);
			mPath.lineTo(next.x, next.y);
		}
		
		for(Path next: mPaths) {
			canvas.drawPath(next, mPaint);
		}
		//mPath.close();
		canvas.drawPath(mPath, mPaint);
	}
	
	private boolean mPushPoint = false;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//Log.e(TAG, "onTouchEvent(event) " + event + ": (" + event.getX() + ", " + event.getY() + ")");
		Point point = new Point((int) event.getX(), (int) event.getY());
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
//			System.out.println(mPoints);
			if(DRAW_DEBUG) {
				Log.v(TAG, "MotionEvent.ACTION_DOWN");
			}
			if(mDialog.checkPoint(event)) {
				mPushPoint = true;
			}
			if(mPushPoint) {
				mPoints.add(point);
			}
			timeDown = System.currentTimeMillis();
			
		}
		else if(event.getAction() == MotionEvent.ACTION_MOVE) {
//			System.out.println(mPoints);
			if(mPushPoint) {
				mPoints.add(point);
			}
			if(DRAW_DEBUG) {
				Log.v(TAG, "MotionEvent.ACTION_MOVE");
			}
			if(mPoints.size() > 1) {
				Path pathTemp = new Path();
				pathTemp.moveTo(mPoints.get(mPoints.size() - 2).x, 
						mPoints.get(mPoints.size() - 2).y);
				pathTemp.lineTo(mPoints.get(mPoints.size() - 1).x, 
						mPoints.get(mPoints.size() - 1).y);
				mPaths.add(pathTemp);
			}
		}
		else if(event.getAction() == MotionEvent.ACTION_UP) {
//			System.out.println(mPoints);
			if(DRAW_DEBUG) {
				Log.v(TAG, "MotionEvent.ACTION_UP");
			}
			if(mPushPoint) {
				mPoints.add(point);
			}
			timeUp = System.currentTimeMillis();
			if(timeUp - timeDown < 150) {
				mDialog.checkEvent(event);
			}
			else if(mPushPoint) {
				mDialog.commitText();
			}
			mPushPoint = false;
			mPoints.clear();
			mPaths.clear();
			mPath.reset();
		}
		invalidate();
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KEY_EVENT_DEBUG) {
			Log.v(TAG, "KeyCode: " + keyCode);
			mDialog.allCancel();
		}
		
		if(keyCode == event.KEYCODE_BACK) {
			Log.v(TAG, "BACK");
			
		}else if(keyCode == event.KEYCODE_MENU) {
			Log.v(TAG, "MENU");
		} 
		else if(keyCode == event.KEYCODE_HOME) {
			Log.v(TAG, "HOME");
		}
		return super.onKeyDown(keyCode, event);
	}

}
