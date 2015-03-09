package com.mobile.safe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.mobile.safe.activity.EnterPwdActivity;
import com.mobile.safe.db.dao.ApplockDao;

public class WatchDogService extends Service {

	private ActivityManager am;
	private ApplockDao dao;
	private boolean flag;
	private List<String> protectPacknames;
	private Intent intent;
	private InnerReceiver innerReceiver;
	private String tempStopProtectPackname;
	private ScreenOffReceiver offreceiver;
	
	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		offreceiver = new ScreenOffReceiver();
		registerReceiver(offreceiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver,new IntentFilter("com.qzd.mobilesafe.tempstop"));
		
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		dao = new ApplockDao(this);
		flag = true;
//		protectPacknames = dao.findAll();
		intent = new Intent(getApplicationContext(),EnterPwdActivity.class);
		//服务是没有任务栈信息的.在服务开启activity,要指定这个activity运行的任务栈
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		new Thread(){
			public void run() {
				while(flag){
					List<RunningTaskInfo> infos = am.getRunningTasks(100);
					String packname = infos.get(0).topActivity.getPackageName();
					if(dao.find(packname)){
						//当前应用需要保护,蹦出来,弹出来一个输入密码的界面
						//设置要保护程序的包名
						intent.putExtra("packname",packname);
						startActivity(intent);
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}			
		}.start();
		
		
		super.onCreate();
	}
	
	private class InnerReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("接收了临时停止保护的广播事件");
			tempStopProtectPackname = intent.getStringExtra("packname");
		}
	}
	
	private class ScreenOffReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			tempStopProtectPackname = null;
		}
	}
	
	
	@Override
	public void onDestroy() {
		flag = false;
		unregisterReceiver(innerReceiver);
		innerReceiver = null;
		unregisterReceiver(offreceiver);
		offreceiver = null;
		super.onDestroy();
	}
}
