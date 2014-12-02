package com.example.android.inkboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.KeyEvent;

public class HotKeyEvent implements KeyEvent.Callback {
	
	private static final String TAG = "HotKeyEvent";
	
	private InputMethodService mInputMethodService;
	
	private IntentFilter mIntentFilter;
	
	private OnHotKeyListener mOnHotKeyListener;
	
	private HotKeyBroadcastReceiver mReceiver;
		
	public HotKeyEvent(InputMethodService service) {
		mInputMethodService = service;
		mReceiver = new HotKeyBroadcastReceiver();
		mIntentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
	}
	
	public void setOnHotKeyListener(OnHotKeyListener listener) {
		mOnHotKeyListener = listener;
	}
	
	public void startListen() {
		if(mReceiver != null) {
			mInputMethodService.registerReceiver(mReceiver, mIntentFilter);
		}
	}
	
	public void stopListen() {
		if(mReceiver != null) {
			mInputMethodService.unregisterReceiver(mReceiver);
		}
	}
	
	public interface OnHotKeyListener {
		
		public void onBackKeyPressed();
		
		public void onBackKeyLongPressed();
		
		public void onHomeKeyPressed();
		
		public void onHomeKeyLongPressed();
		
		public void onRecentAppsKeyPressed();
		
		public void onRecentAppsKeyLongPressed();
	
	}
	
	class HotKeyBroadcastReceiver extends BroadcastReceiver {
		
		//See at PhoneWindowManager
		private static final String SYSTEM_DIALOG_REASON_KEY = "reason";		
		private static final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
		private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
		private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
	    private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			Log.e(TAG, "action: " + action + " reason: " + intent.getStringExtra("reason"));
			if(action != null & action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String result = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
				Log.i(TAG, result + " " + mOnHotKeyListener);
				if(result != null & mOnHotKeyListener != null & 
						result.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
					mOnHotKeyListener.onHomeKeyPressed();
				}
				else if(result != null & mOnHotKeyListener != null &
						result.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
					mOnHotKeyListener.onRecentAppsKeyPressed();
				}
			}
		}
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.v(TAG, "KeyCode: " + keyCode);
		if(keyCode == event.KEYCODE_BACK) {
			Log.e(TAG, "BACK");
			mOnHotKeyListener.onBackKeyPressed();
			
		}else if(keyCode == event.KEYCODE_MENU) {
			Log.e(TAG, "MENU");
		} 
		else if(keyCode == event.KEYCODE_HOME) {
			Log.e(TAG, "HOME");
		}
		return true;
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
