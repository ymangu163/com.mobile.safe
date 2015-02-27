package com.mobile.safe.activity;

import com.mobile.safe.R;
import com.mobile.safe.utils.SmsUtils;
import com.mobile.safe.utils.SmsUtils.BackUpCallBack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

public class AtoolsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);		
	}
	
	/**
	 * 点击事件，进入号码归属地查询的页面
	 */
	public void numberQuery(View view){
		Intent intentv = new Intent(this,NumberAddressQueryActivity.class);
		startActivity(intentv);	
	}	
	
	//点击事件 :短信的备份
		public void smsBackup(View view){
			
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				final ProgressDialog pd = new ProgressDialog(this);
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.setMessage("正在备份。。。。");
				pd.show();
				
				new Thread(){
					@Override
					public void run() {
					try {
							SmsUtils.backupSms(AtoolsActivity.this,new BackUpCallBack(){

								@Override
								public void beforeSmsBackup(int total) {
									pd.setMax(total);
								}

								@Override
								public void onSmsBackup(int progress) {
									pd.setProgress(progress);
								}								
							});
							runOnUiThread(new Runnable() {								
								@Override
								public void run() {
									Toast.makeText(AtoolsActivity.this,"短信备份成功",0).show();									
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
								runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									Toast.makeText(AtoolsActivity.this,"短信备份失败",0).show();									
								}
							});
						}
							
					pd.dismiss();
					
					}
				}.start();	
			}
		}
		
	
		//点击事件 :短信的还原
		public void smsRestore(View view){
			try {
				SmsUtils.restoreSms(this,false);
				Toast.makeText(this,"还原成功！",0).show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}
		
}
