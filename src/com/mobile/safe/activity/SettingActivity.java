package com.mobile.safe.activity;

import com.lidroid.xutils.util.LogUtils;
import com.mobile.safe.R;
import com.mobile.safe.service.AddressService;
import com.mobile.safe.service.CallSmsSafeService;
import com.mobile.safe.ui.SettingClickView;
import com.mobile.safe.ui.SettingItemView;
import com.mobile.safe.utils.ServiceUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	
	// 设置是否开启自动更新
	private SettingItemView siv_update;
	private SharedPreferences sp;

	// 设置是否开启显示归属地
	private SettingItemView siv_show_address;
	private Intent showAddress;
	
	//设置归属地显示框背景
		private SettingClickView scv_changebg;
		
		//黑名单拦截设置
		private SettingItemView siv_callsms_safe;
		private Intent callSmsSafeIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 找到自定义的RelativeLayout
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		boolean update = sp.getBoolean("update", false);   //取出数据
		if(update){
			//自动升级已经开启
			siv_update.setChecked(true);
		}else{
			//自动升级已经关闭
			siv_update.setChecked(false);
		}
		
		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				// 判断是否有选中
				if(siv_update.isChecked()){//已经打开自动升级了
					 
					siv_update.setChecked(false);
					editor.putBoolean("update", false);  //存数据
				}else{	// 没有打开自动升级
				
					siv_update.setChecked(true);
					editor.putBoolean("update", true);
				}
				editor.commit();   //记得要提交数据
				
			}			
		});
		
		//设置号码归属地的背景
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		scv_changebg.setTitle("归属地提示框风格");
		final String [] items = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
		int which = sp.getInt("which", 0);
		scv_changebg.setDesc(items[which]);
		scv_changebg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int dd = sp.getInt("which", 0);
				// 弹出一个对话框
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("归属地提示框风格");
				builder.setSingleChoiceItems(items,dd, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//保存选择参数
						Editor editor = sp.edit();
						editor.putInt("which", which);
						editor.commit();
						scv_changebg.setDesc(items[which]);
						
						//取消对话框
						dialog.dismiss();		
					}
				});
				builder.setNegativeButton("cancel", null);
					builder.show();
				
			}
		});
		
		
		//黑名单拦截设置
				siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);
				callSmsSafeIntent = new Intent(this, CallSmsSafeService.class);
				siv_callsms_safe.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (siv_callsms_safe.isChecked()) {
									// 变为非选中状态
									siv_callsms_safe.setChecked(false);
									stopService(callSmsSafeIntent);
								} else {
									// 选择状态
									siv_callsms_safe.setChecked(true);
									startService(callSmsSafeIntent);
								}

							}
						});
		
		
		
		// 设置号码归属地显示控件
		siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
		showAddress = new Intent(this, AddressService.class);
		boolean isServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,	"com.mobile.safe.service.AddressService");
		if(isServiceRunning){
			//监听来电的服务是开启的
			siv_show_address.setChecked(true);
		}else{
			siv_show_address.setChecked(false);
		}
		
		siv_show_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_show_address.isChecked()) {
					// 变为非选中状态
					siv_show_address.setChecked(false);
					stopService(showAddress);

				} else {
					// 选择状态
					siv_show_address.setChecked(true);					
					startService(showAddress); // 去开启服务
					LogUtils.d("开启了服务");					

				}

			}
		});
		
		
		}
	
	@Override
	protected void onResume() {
		super.onResume();
		showAddress = new Intent(this, AddressService.class);
		boolean isServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.mobile.safe.service.AddressService");
		LogUtils.d("isServiceRunning:"+isServiceRunning);
		
		
		if(isServiceRunning){
			//监听来电的服务是开启的
			siv_show_address.setChecked(true);
		}else{
			siv_show_address.setChecked(false);
		}
		// 电话拦截
		boolean iscallSmsServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.mobile.safe.service.CallSmsSafeService");
		siv_callsms_safe.setChecked(iscallSmsServiceRunning);
		
	}
	
	
		
	}
	


