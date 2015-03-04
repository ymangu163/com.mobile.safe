package com.mobile.safe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.TextView;

import com.mobile.safe.R;
import com.mobile.safe.utils.SystemInfoUtils;

public class TaskManagerActivity extends Activity {
	private TextView tv_process_count;
	private TextView tv_mem_info;
	// 正在运行的进程数量
	private int runningProcessCount;
	// 可用ram内存
	private long availRam;
	//总内存
	private long totalRam;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);		
		
		tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		runningProcessCount = SystemInfoUtils.getRunningProcessCount(this);//获取正在运行的进程
		
		availRam = SystemInfoUtils.getAvailRam(this);//获取可用内存
		totalRam = SystemInfoUtils.getTotalRam(this); //获取总内存
		tv_process_count.setText("运行中进程:"+runningProcessCount+"个");
		tv_mem_info.setText("剩余/总内存:"+Formatter.formatFileSize(this,availRam)+"/"+Formatter.formatFileSize(this, totalRam));
		
		
	}
	
	

}
