package com.mobile.safe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mobile.safe.activity.HomeActivity;
import com.mobile.safe.utils.StreamTools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
	@ViewInject(R.id.tv_update_info)
	private TextView tv_update_info;
	
	protected static final int SHOW_UPDATE_DIALOG = 0;
	protected static final int ENTER_HOME = 1;
	protected static final int URL_ERROR = 2;
	protected static final int NETWORK_ERROR = 3;
	protected static final int JSON_ERROR = 4;
	
	private String description;
	private String apkurl;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		ViewUtils.inject(this);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		tv_splash_version.setText("版本号" + getVersionName());
		boolean update = sp.getBoolean("update", false);
		installShortCut();
		//拷贝数据库
		copyDB("address.db");
		copyDB("antivirus.db");
		
		if(update){
			// 检查升级
			checkUpdate();
		}else{
			//自动升级已经关闭
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					//进入主页面
					enterHome();
					
				}
			}, 2000);
		
		}
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(1000);
		findViewById(R.id.rl_root_splash).startAnimation(aa);  // 给RelativeLayout 设置动画
		
	}
	
	private void installShortCut() {
		boolean shortcut = sp.getBoolean("shortcut", false);
		if(shortcut)
			return;
		//发送广播的意图，要创建快捷图标了
				Intent intent = new Intent();
				intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
				//快捷方式  要包含3个重要的信息 1，名称 2.图标 3.干什么事情
				intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");
				intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
				//桌面点击图标对应的意图。
				Intent shortcutIntent = new Intent();
				shortcutIntent.setAction("android.intent.action.MAIN");
				shortcutIntent.addCategory("android.intent.category.LAUNCHER");
				shortcutIntent.setClassName(getPackageName(), "com.qzd.mobilesafe.SplashActivity");
				intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
				sendBroadcast(intent);
				sendBroadcast(intent);
				Editor editor = sp.edit();
				editor.putBoolean("shortcut", true);
				editor.commit();
	}

	/**
	 * //path 把address.db这个数据库拷贝到data/data/《包名》/files/address.db
	 */
	private void copyDB(String dbfilename) {
		//只要你拷贝了一次，我就不要你再拷贝了
		
		File file = new File(getFilesDir(), dbfilename);
		try {
			if(file.exists()&&file.length()>0){
				//正常了，就不需要拷贝了
				LogUtils.i("正常了，就不需要拷贝了");
			}else{
				InputStream is = getAssets().open(dbfilename);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while((len = is.read(buffer))!= -1){
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_UPDATE_DIALOG:// 显示升级的对话框
				LogUtils.i("显示升级的对话框");
				showUpdateDialog();
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
	/*
	 *   this 与 getApplicationContext() 两种上下文的区别？
		① getApplicationContext();生命周期长，只要应用还存活它就存在；
		this 生命周期短，只要 Activity 不存在了，系统就会回收；
		② getBaseContext(),getApplication(),getApplicationContext();
		都不能放在 AlertDialog 做上下文；
		③ getApplicationContext（） 使用场景是比如频繁需要操作的数据库
		推荐用法:Activity.this
	 */

	/**
	 * 弹出升级对话框
	 * 实验的时候，要注意 如果要安装的版本比手机已安装版本低的话，安装不上。要先卸掉手机上的应用
	 */
	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(SplashActivity.this);
		builder.setTitle("提示升级");
//		builder.setCancelable(false);//强制升级
		builder.setMessage(description);
		builder.setPositiveButton("立刻升级", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载APK，并且替换安装
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// sdcard存在
					// afnal
					HttpUtils   http=new HttpUtils();
					http.download(apkurl, Environment
							.getExternalStorageDirectory().getAbsolutePath()+"/mobilesafe2.0.apk", 
							new RequestCallBack<File>() {
								
								@Override
								public void onSuccess(ResponseInfo<File> responseInfo) {
									installAPK(responseInfo.result);
								}
								
								@Override
								public void onFailure(HttpException arg0, String arg1) {
									arg0.printStackTrace();
									Toast.makeText(getApplicationContext(), "下载失败", 1).show();
								}
								
								/**
								 * 安装APK, 查看源码 PackageInstaller
								 * @param t
								 */
								private void installAPK(File file) {
									Intent intent = new Intent();
									  intent.setAction("android.intent.action.VIEW");
									  intent.addCategory("android.intent.category.DEFAULT");
									  intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");									  
									  startActivity(intent);									
								}
								
								@Override
								public void onLoading(long total, long current,
										boolean isUploading) {
									super.onLoading(total, current, isUploading);
									tv_update_info.setVisibility(View.VISIBLE);
									//当前下载百分比
									int progress = (int) (current * 100 / total);
									tv_update_info.setText("下载进度："+progress+"%");
									
								}
							});
					
					
					
				
				}else{
					Toast.makeText(getApplicationContext(), "没有sdcard，请安装上在试",0).show();
					return;
				}
				
			}
		
		});
		// 点击返回
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//进入主页面
				enterHome();
				dialog.dismiss();
				
			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				enterHome();// 进入主页面
			}
		});
		builder.show();
		
		
	}



	protected void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// 关闭当前页面
		finish();		
	}


	/*
	 *  给应用签名的步骤：
	 *  ① 选中项目，右键 Export ...
		② 导出应用：Export Android Application.
		③ 创建keystore,注意所有的密码都是同一个
		④ 填写城市名等信息，国家填86
		⑤ 最后选择应用的保存地址和名称，就成功鸟，这个应用就可以去发布了
	 */
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
