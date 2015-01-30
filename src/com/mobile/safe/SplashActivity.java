package com.mobile.safe;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/*
	 * splash 界面的作用
	1、用来展现产品的 Logo；
	2、应用程序初始化的操作；
	3、检查应用程序的版本；
	4、检查当前应用程序是否合法注册；
 */
public class SplashActivity extends Activity {

	@ViewInject(R.id.tv_splash_version)
	private TextView tv_splash_version;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		ViewUtils.inject(this);
		tv_splash_version.setText("版本号" + getVersionName());
		
	}
	
	
	
	/**
	 * 得到应用程序的版本名称
	 */
	private String getVersionName() {
		// 用于管理手机的APK
		PackageManager pm = getPackageManager();
				
		try {
			// 得到知道APK的功能清单文件
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}			
	}
	
	
}
