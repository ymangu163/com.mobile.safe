package com.mobile.safe.activity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mobile.safe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity {
	
	private SharedPreferences sp;
	@ViewInject(R.id.cb_proteting)
	private CheckBox cb_proteting;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		ViewUtils.inject(this);
		
		boolean  protecting = sp.getBoolean("protecting", false);
		if(protecting){
			//手机防盗已经开启了
			cb_proteting.setText("手机防盗已经开启");
			cb_proteting.setChecked(true);
		}else{
			//手机防盗没有开启
			cb_proteting.setText("手机防盗没有开启");
			cb_proteting.setChecked(false);
			
		}
		cb_proteting.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){
					cb_proteting.setText("手机防盗已经开启");
				}else{
					cb_proteting.setText("手机防盗没有开启");
				}
				
				//保存选择的状态
				Editor editor = sp.edit();
				editor.putBoolean("protecting", isChecked);
				editor.commit();
			}
		});
		
	}

	@Override
	public void showNext() {
		Editor editor = sp.edit();
		editor.putBoolean("configed", true);
		editor.commit();
		
		Intent intent = new Intent(this,LostFindActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(this,Setup3Activity.class);
		startActivity(intent);
		finish();	
		overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
	}
	

}
