package com.mobile.safe;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mobile.safe.utils.StreamTools;

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
		
		// 检查升级
		checkUpdate();
		
	}
	
	
	/**
	 * 检查是否有新版本，如果有就升级
	 */
	private void checkUpdate() {

		new Thread(){
			public void run() {
				// URL http://192.168.1.254:8080/updateinfo.html
				try {
					URL url=new URL(getString(R.string.servelurl));
					// 去联网，请求数据
					try {
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.setRequestMethod("GET");   //一定要大写
						conn.setConnectTimeout(4000);   //链接超时
						int code = conn.getResponseCode();  //得到响应码
						
						if (code == 200) { // 联网成功
							
							InputStream is = conn.getInputStream();   // 得到输入流
							// 把流转成String
							String result = StreamTools.readFromStream(is);
							LogUtils.i("联网成功了" + result);
						
						
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
				
			};
			
			
			
		}.start();
		
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
