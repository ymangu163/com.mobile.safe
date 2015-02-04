package com.mobile.safe.activity;

import com.mobile.safe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Setup2Activity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		
		
		
		
	}
	
	public void next(View view){
		Intent intent = new Intent(this,Setup3Activity.class);
		startActivity(intent);
		finish();		
	}
	
	public void pre(View view){
		Intent intent = new Intent(this,Setup1Activity.class);
		startActivity(intent);
		finish();
		
	}
	

}
