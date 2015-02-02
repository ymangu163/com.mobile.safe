package com.mobile.safe.activity;

import com.mobile.safe.R;
import com.mobile.safe.ui.SettingItemView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	
	private SettingItemView siv_update;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		// 找到自定义的RelativeLayout
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		
		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断是否有选中
				if(siv_update.isChecked()){//已经打开自动升级了
					
					siv_update.setChecked(false);
					siv_update.setDesc("自动升级已经关闭");
					
				}else{	// 没有打开自动升级
				
					siv_update.setChecked(true);
					siv_update.setDesc("自动升级已经开启");
				}
				
				
			}			
		});
		}
		
	}
	


