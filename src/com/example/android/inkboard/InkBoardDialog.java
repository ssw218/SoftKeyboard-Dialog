package com.example.android.inkboard;

import com.example.android.softkeyboard.LatinKeyboard;
import com.example.android.softkeyboard.LatinKeyboardView;
import com.example.android.softkeyboard.R;
import com.example.android.softkeyboard.SoftKeyboard;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.inputmethodservice.InputMethodService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputConnection;

public class InkBoardDialog extends HotKeyEvent{
	
	private static final String TAG = "InkBoardDialog";
	
	private AlertDialog mAlertDialog;
	
	private InputMethodService mInputMethodService;
	
	private InkBoardView mInkBoardView;
	
	private Window mWindow;
	
	private InputConnection mInputConnection;
	
	private LatinKeyboardView mInputView;
	
	private LatinKeyboard mKeyboard;
	
	private int mScreenWidth;
	
	private int mScreenHeight;
	
	private OnHotKeyListener mOnHotKeyListener = new OnHotKeyListener() {
		@Override
		public void onBackKeyPressed() {
			// TODO Auto-generated method stub
			if(mAlertDialog != null) {
				cancel();
			}
		}

		@Override
		public void onBackKeyLongPressed() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onHomeKeyPressed() {
			// TODO Auto-generated method stub
			if(mAlertDialog != null) {
				cancel();
			}
		}

		@Override
		public void onHomeKeyLongPressed() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onRecentAppsKeyPressed() {
			// TODO Auto-generated method stub
			if(mAlertDialog != null) {
				cancel();
			}
		}

		@Override
		public void onRecentAppsKeyLongPressed() {
			// TODO Auto-generated method stub
			//mInputMethodService.
		}
	}; 
	
	public InkBoardDialog(InputMethodService service) {
		super(service);
		mInputMethodService = service;
	}
	
	private void initDialog() {
		mAlertDialog = new AlertDialog.Builder(mInputMethodService).create();
		mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		mAlertDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
		mWindow = mAlertDialog.getWindow();
		mAlertDialog.show();
		mWindow.setContentView(R.layout.inkboardview);
		mAlertDialog.setCancelable(false);
		mWindow.setGravity(Gravity.TOP);
		
		mInkBoardView = (InkBoardView) mWindow.findViewById(R.id.inkboardview);
		mInkBoardView.setDialog(this);
	}
	
	private void updateDialogSize() {
		DisplayMetrics displayMetrics = mInputMethodService.getResources().getDisplayMetrics();
		mScreenWidth = displayMetrics.widthPixels;
		mScreenHeight = displayMetrics.heightPixels;
		
//		mKeyboard = ((SoftKeyboard) mInputMethodService).getEmptyKeyboard();
//		int keyboardHeight = 0;
//		if(mKeyboard != null) {
//			keyboardHeight = mKeyboard.getHeight();
//		}
		WindowManager.LayoutParams layoutParams = mAlertDialog.getWindow().getAttributes();
		layoutParams.width = mScreenWidth;
		layoutParams.height = mScreenHeight;
		mAlertDialog.getWindow().setAttributes(layoutParams);
	}
	
	private void updateInputConnection() {
		mInputConnection = mInputMethodService.getCurrentInputConnection();
	}
	
	public void cancel() {
		mAlertDialog.cancel();
	}
	
	public void show() {
		mAlertDialog.show();
	}
	
	public void commitText() {
		mInputConnection.commitText("8", 1);
	}
	
	public void allCancel() {
		mInputMethodService.requestHideSelf(0);
	}
	
	public void onDialogCreate() {
		if(mAlertDialog == null) {
			initDialog();
		}
		this.cancel();
		this.setOnHotKeyListener(mOnHotKeyListener);
		this.startListen();
		Log.e(TAG, "onDialogCreate()");
	}

	public void onDialogInitializeInterface() {
		updateDialogSize();
		Log.e(TAG, "onDialogInitializeInterface()");
	}
	
	public void onDialogCreateInputView() {
		
		Log.e(TAG, "onDialogCreateInputView()");
	}
	
	public void onDialogStartInput() {
		updateInputConnection();
		Log.e(TAG, "onDialogStartInput()");
	}
	
	public void onDialogFinishInput() {
		
		Log.e(TAG, "onDialogFinishInput()");
	}
	
	public void onDialogStartInputView() {
		updateInputConnection();
		updateDialogSize();
		this.show();
		
		Log.e(TAG, "onDialogStartInputView()");
	}
	
	public void onDialogFinishInputView() {
		this.cancel();
		
		Log.e(TAG, "onDialogFinishInputView()");
	}
	
	public void onDialogDestory() {
		//this.stopListen();
		Log.e(TAG, "onDialogDestory()");
	}
	
	private int getStatusBarHeight() {
		int height = Resources.getSystem().getDimensionPixelSize(
                Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
		Log.e(TAG, "" + height);
        return height;
    }

	public boolean checkPoint(MotionEvent event) {
		mKeyboard = ((SoftKeyboard) mInputMethodService).getEmptyKeyboard();
		if(event.getY() >= 0 && event.getY() < mScreenHeight - mKeyboard.getHeight() - getStatusBarHeight()) {
			return true;
		}
		return false;
	}
	
	public void checkEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//this.cancel();
		mKeyboard = ((SoftKeyboard) mInputMethodService).getEmptyKeyboard();
		if(checkPoint(event)) {
			allCancel();
		}
		else {
			MotionEvent mEventDown = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, event.getX(),
					event.getY() + mKeyboard.getHeight() - mScreenHeight + getStatusBarHeight(), 0);
			MotionEvent mEventUp = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, event.getX(), 
					event.getY() + mKeyboard.getHeight() - mScreenHeight + getStatusBarHeight(), 0);
			//mInputMethodService.getWindow().cancel();
			mInputView = ((SoftKeyboard) mInputMethodService).getInputView();
			boolean down = mInputView.onTouchEvent(mEventDown);
			boolean up = mInputView.onTouchEvent(mEventUp);
			//mInputMethodService.getWindow().dispatchTouchEvent(event);
		}
	}

}
