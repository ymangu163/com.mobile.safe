package com.mobile.safe.activity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mobile.safe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {
	
	private SharedPreferences sp;
	
	@ViewInject(R.id.tv_safenumber)
	private TextView tv_safenumber;
	@ViewInject(R.id.iv_protecting)
	private ImageView iv_protecting;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//判断一下，是否做过设置向导，如果没有做过，就跳转到设置向导页面去设置，否则就留着当前的页面
		boolean configed = sp.getBoolean("configed", false);
		if(configed){
			// 就在手机防盗页面
			setContentView(R.layout.activity_lost_find);
			ViewUtils.inject(this);
			//得到我们设置的安全号码
			String safenumber = sp.getString("safenumber", "");
			tv_safenumber.setText(safenumber);
			//设置防盗保护的状态
			boolean protecting = sp.getBoolean("protecting", false);
			if(protecting){
				//已经开启防盗保护
				iv_protecting.setImageResource(R.drawable.lock);
			}else{
				//没有开启防盗保护
				iv_protecting.setImageResource(R.drawable.unlock);
			}			
		}else{
			//还没有做过设置向导
			Intent intent = new Intent(this,Setup1Activity.class);
			startActivity(intent);
			//关闭当前页面
			finish();
		}
		
	}
	
	/**
	 * 重新进入手机防盗设置向导页面
	 * @param view
	 */
	public void reEnterSetup(View view){
		Intent intent = new Intent(this,Setup1Activity.class);
		startActivity(intent);
		//关闭当前页面
		finish();
	}

}
