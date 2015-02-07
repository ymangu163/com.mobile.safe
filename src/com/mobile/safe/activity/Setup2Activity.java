package com.mobile.safe.activity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mobile.safe.R;
import com.mobile.safe.ui.SettingItemView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class Setup2Activity extends BaseSetupActivity {
	@ViewInject(R.id.siv_setup2_sim)
	private SettingItemView  siv_setup2_sim;
	
	/**
	 * 读取手机sim的信息
	 */
	private TelephonyManager tm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		ViewUtils.inject(this);
		
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);		
		
		String sim = sp.getString("sim", null);
		if(TextUtils.isEmpty(sim)){
			//没有绑定
			siv_setup2_sim.setChecked(false);
		}else{
			//已经绑定
			siv_setup2_sim.setChecked(true);
		}
		
		
		siv_setup2_sim.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				
				if(siv_setup2_sim.isChecked()){
					siv_setup2_sim.setChecked(false);
					//保存sim卡的序列号
					editor.putString("sim", null);
				}else{
					siv_setup2_sim.setChecked(true);
					//保存sim卡的序列号
					String sim = tm.getSimSerialNumber();
					editor.putString("sim", sim);					
				}			
				editor.commit();
			}			
		});
		
	}
	
	

	@Override
	public void showNext() {
		// 取出是否绑定sim
		String sim = sp.getString("sim", null);
		if (TextUtils.isEmpty(sim)) {
			// 没有绑定
			Toast.makeText(this, "sim卡没有绑定", 1).show();
			return;
		}
		
		Intent intent = new Intent(this,Setup3Activity.class);
		startActivity(intent);
		finish();		
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(this,Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
		
	}
	

}
