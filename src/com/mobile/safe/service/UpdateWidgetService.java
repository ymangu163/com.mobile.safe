package com.mobile.safe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.mobile.safe.R;
import com.mobile.safe.receiver.MyWidget;
import com.mobile.safe.utils.SystemInfoUtils;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.RemoteViews;
/**
 * 锁屏服务
 */
public class UpdateWidgetService extends Service {
	private Timer timer;
	private TimerTask task;
	//wedget的管理器
	private AppWidgetManager awm;
	private ScreenOffReceiver offreceiver;
	private ScreenOnReceiver onreceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		onreceiver = new ScreenOnReceiver();
		offreceiver = new ScreenOffReceiver();
		registerReceiver(onreceiver,new IntentFilter(Intent.ACTION_SCREEN_ON));
		registerReceiver(offreceiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));
		awm = AppWidgetManager.getInstance(this);
		startTimer();
		super.onCreate();
	}
	
	//开启
	/*
	 * 序列化：serialverable , parcelabe 区别？
		serialverable ：① Java 下的 ②可以把对象写到文件
		parcelabe：① Android 特有 ②可以把对象写到公共的内存空间
		
		那么怎么通过访问公共内存空间来实现通信呢？
		通过RemoteView
	 */
			private void startTimer(){
				if(timer == null && task == null){
			timer = new Timer();
			task = new TimerTask() {
				@Override
				public void run() {
					//设置更新的组件
					ComponentName provider = new ComponentName(UpdateWidgetService.this,MyWidget.class);
					RemoteViews views = new RemoteViews(getPackageName(),R.layout.process_widget);
					views.setTextViewText(R.id.process_count,"正在运行的进程:"+SystemInfoUtils.getRunningProcessCount(getApplicationContext())+"个");
					views.setTextViewText(R.id.process_memory,"可用内存:"+(SystemInfoUtils.getAvailRam(getApplicationContext())/1024/1024)+"MB");
					System.out.println("更新widget.");
					
					// PendingIntent 描述一个动作,这个动作是由另外的一个应用程序执行的.
					// 自定义一个广播事件,杀死后台进度的事件
					Intent intent = new Intent();
					intent.setAction("com.qzd.mobilesafe.killall");
					PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,
							intent,PendingIntent.FLAG_UPDATE_CURRENT);
					views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
					awm.updateAppWidget(provider, views);
				}		
			};
		/*
		 *  参数1： 要执行的任务
		 *  参数2：第一次执行 隔多久后执行
		 *  参数3：启动之后，隔多久执行一次
		 */
			timer.schedule(task,0,2000);
		}
			}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(offreceiver);
		unregisterReceiver(onreceiver);
		offreceiver = null;
		onreceiver = null;
		stopTimer();
	}

	private void stopTimer() {
		if(timer != null && task != null){
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}
	}
	
	
	//锁屏
		private class ScreenOffReceiver extends BroadcastReceiver{
			@Override
			public void onReceive(Context context, Intent intent) {
				stopTimer();
				System.out.println("手机锁屏了.");
			}
		}
	
		//解锁
		private class ScreenOnReceiver extends BroadcastReceiver{
			@Override
			public void onReceive(Context context, Intent intent) {
				startTimer();
				System.out.println("手机解锁了.");
			}
		}
}
