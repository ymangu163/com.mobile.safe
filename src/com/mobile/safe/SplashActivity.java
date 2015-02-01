package com.mobile.safe;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mobile.safe.activity.HomeActivity;
import com.mobile.safe.utils.StreamTools;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

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
	
	protected static final int SHOW_UPDATE_DIALOG = 0;
	protected static final int ENTER_HOME = 1;
	protected static final int URL_ERROR = 2;
	protected static final int NETWORK_ERROR = 3;
	protected static final int JSON_ERROR = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		ViewUtils.inject(this);
		tv_splash_version.setText("版本号" + getVersionName());
		
		// 检查升级
		checkUpdate();
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(1000);
		findViewById(R.id.rl_root_splash).startAnimation(aa);  // 给RelativeLayout 设置动画
		
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_UPDATE_DIALOG:// 显示升级的对话框
				LogUtils.i("显示升级的对话框");
//				showUpdateDialog();
				break;
			case ENTER_HOME:// 进入主页面
				enterHome();
				break;
			case URL_ERROR:// URL错误
				enterHome();
				Toast.makeText(getApplicationContext(), "URL错误", 0).show();

				break;

			case NETWORK_ERROR:// 网络异常
				enterHome();
				Toast.makeText(SplashActivity.this, "网络异常", 0).show();
				break;

			case JSON_ERROR:// JSON解析出错
				enterHome();
				Toast.makeText(SplashActivity.this, "JSON解析出错", 0).show();
				break;

			default:
				break;
			
			
			}
			
			
		};
	};
	
	
	/**
	 * 检查是否有新版本，如果有就升级
	 */
	private void checkUpdate() {

		new Thread(){
			private String description;
			private String apkurl;

			public void run() {
				Message mes = Message.obtain();
				long startTime = System.currentTimeMillis();
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
						
							// json解析
							JSONObject obj = new JSONObject(result);
							// 得到服务器的版本信息
							String version = (String) obj.get("version");
							description = (String) obj.get("description");
							apkurl = (String) obj.get("apkurl");
							
							// 校验是否有新版本
							if (getVersionName().equals(version)) {
								// 版本一致，没有新版本，进入主页面
								mes.what = ENTER_HOME;
							} else {
								// 有新版本，弹出一升级对话框
								mes.what = SHOW_UPDATE_DIALOG;

							}
						
						}						
					} catch (IOException e) {
						mes.what = NETWORK_ERROR;
						e.printStackTrace();
					} catch (JSONException e) {
						mes.what = JSON_ERROR;
						e.printStackTrace();
					}					
				} catch (MalformedURLException e) {
					mes.what = URL_ERROR;
					e.printStackTrace();
				}finally {
					long endTime = System.currentTimeMillis();
					// 我们花了多少时间
					long dTime = endTime - startTime;
					// 2000
					if (dTime < 2000) {
						try {
							Thread.sleep(2000 - dTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}					
					handler.sendMessage(mes);
				}
				
				
			};			 
		}.start();
		
	}



	protected void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// 关闭当前页面
		finish();		
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
