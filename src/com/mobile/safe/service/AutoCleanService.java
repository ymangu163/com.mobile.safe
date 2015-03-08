package com.mobile.safe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class AutoCleanService extends Service {
	private ActivityManager am;
	private ScreenOffReceiver receiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		am  = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		receiver = new ScreenOffReceiver();
		// 注册锁屏广播
		registerReceiver(receiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;		
		super.onDestroy();
	}
	
	
	//屏幕锁屏
		private class ScreenOffReceiver extends BroadcastReceiver{
			@Override
			public void onReceive(Context context, Intent intent) {
				List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
				for (RunningAppProcessInfo info : infos) {
					am.killBackgroundProcesses(info.processName);
				}
			}
		}
	
	
}