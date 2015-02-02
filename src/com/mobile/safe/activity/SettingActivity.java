package com.mobile.safe.activity;

import com.mobile.safe.R;
import com.mobile.safe.ui.SettingItemView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	
	private SettingItemView siv_update;
	private SharedPreferences sp;

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
		}
		
	}
	


