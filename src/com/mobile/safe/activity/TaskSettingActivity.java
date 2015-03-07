package com.mobile.safe.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.mobile.safe.R;
import com.mobile.safe.service.AutoCleanService;
import com.mobile.safe.utils.ServiceUtils;

public class TaskSettingActivity extends Activity {
	private SharedPreferences sp;
	private CheckBox cb_show_system;
	private CheckBox cb_auto_clean;
	
	
	/*
	 *   延时执行的几种做法：
	 *   1. Timer   + TimerTask
	 *   2. CountDownTimer
	 */
	 private Timer timer;
	 private TimerTask task;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_setting);
		sp = getSharedPreferences("config",MODE_PRIVATE);
		cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		cb_auto_clean = (CheckBox) findViewById(R.id.cb_auto_clean);
		cb_show_system.setChecked(sp.getBoolean("showsystem",false));
		cb_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = sp.edit();
				editor.putBoolean("showsystem",isChecked);
				editor.commit();
			}
		});
		
		
		cb_auto_clean.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				Intent intent = new Intent(TaskSettingActivity.this,AutoCleanService.class);
				if(isChecked){
					startService(intent);
				}else{
					stopService(intent);
				}
				
			}					
		});
		
		// 方法一： 定时器 Timer		
//		timer.schedule(task, 200000);
		// 方法二： CountDownTimer  倒计时计速器  ,案例---秒表
		/*
		 *  参数1： 开始之后 到结束之间 多长时间
		 *  参数2： 多久执行一次 onTick 方法
		 */		
		CountDownTimer  cdt=new CountDownTimer(3000, 1000){

			@Override
			public void onTick(long millisUntilFinished) {  // 在倒计时
				System.out.println(millisUntilFinished);
			}

			@Override
			public void onFinish() {
				System.out.println("finish.");
			}			
		};
		cdt.start();
		
	}

	@Override
	protected void onResume() {
		boolean running = ServiceUtils.isServiceRunning(this,"com.mobile.safe.service.AutoCleanService");
		cb_auto_clean.setChecked(running);		
		super.onResume();
	}
	
	
	
}
