package com.mobile.safe.activity;

import com.mobile.safe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Setup2Activity extends BaseSetupActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		
	}
	
	

	@Override
	public void showNext() {
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
