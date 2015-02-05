package com.mobile.safe.activity;

import com.mobile.safe.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;

public class Setup1Activity extends BaseSetupActivity {
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup_one);		
			
	}
	
	@Override		
	public void showNext() {
		Intent intent = new Intent(this,Setup2Activity.class);
		startActivity(intent);
		finish();	
		//要求在finish()或者startActivity(intent);后面执行；
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
	}



	@Override
	public void showPre() {
		
	}
	


}
