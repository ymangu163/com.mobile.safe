package com.mobile.safe.activity;

import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobile.safe.R;

public class CleanCacheActivity extends Activity {
	protected static final int SCANING = 1;
	public static final int SHOW_CACHE_INFO = 2;
	protected static final int SCAN_FINISH = 3;
	private ProgressBar progressBar1;
	private LinearLayout ll_container;
	private TextView tv_status;
	private PackageManager pm;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANING:
				String text = (String) msg.obj;
				tv_status.setText("正在扫描："+text);
				break;
			case SHOW_CACHE_INFO:	
				View view = View.inflate(getApplicationContext(), R.layout.list_appcache_item, null);
				ImageView iv = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
				TextView tv_cache = (TextView) view.findViewById(R.id.tv_cache);
				final CacheInfo info = (CacheInfo) msg.obj;
				iv.setImageDrawable(info.icon);
				tv_name.setText(info.name);
				tv_cache.setText("缓存大小："+Formatter.formatFileSize(getApplicationContext(), info.size));
				ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				
				
				ll_container.addView(view, 0);
				break;
			case SCAN_FINISH:
				tv_status.setText("扫描完毕");
				break;	
			}	
		};
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		tv_status = (TextView) findViewById(R.id.tv_status);

		scanCache();
		
	}

	// 扫描每个应用的缓存信息
	private void scanCache() {
		new Thread() {			
			public void run() {
//				Method getPackageSizeInfoMethod;
//				Method[] methods=PackageManager.class.getMethods();
//				for(Method method:methods){
//					if("getPackageSizeInfo".equals(method.getName())){
//						getPackageSizeInfoMethod=method;
//					}					
//				}
				int total = 0;
				pm = getPackageManager();
				List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
				progressBar1.setMax(packageInfos.size());
				for(PackageInfo packinfo: packageInfos){
					String packname = packinfo.packageName;
					try {
						Method method = PackageManager.class.getMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
						method.invoke(pm, packname,new MyObserver());
						Message msg = Message.obtain();
						msg.what= SCANING;
						msg.obj = packinfo.applicationInfo.loadLabel(pm).toString();
						handler.sendMessage(msg);
					
					} catch (Exception e) {
						e.printStackTrace();
					}
					total ++;
					progressBar1.setProgress(total);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
				Message msg = Message.obtain();
				msg.what = SCAN_FINISH;
				handler.sendMessage(msg);
				
			};		
		}.start();
		
		
		
	}
	
	private class MyObserver extends IPackageStatsObserver.Stub {
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			long cache = pStats.cacheSize;
			long codeSize = pStats.codeSize;
			if(cache>0){
				//System.out.println("当前应用程序："+pStats.packageName+"有缓存："+Formatter.formatFileSize(getApplicationContext(), cache));
				try {
					Message msg = Message.obtain();
					msg.what = SHOW_CACHE_INFO;
					CacheInfo cacheInfo = new CacheInfo();
					cacheInfo.packname = pStats.packageName;
					cacheInfo.icon = pm.getApplicationInfo(pStats.packageName, 0).loadIcon(pm);
					cacheInfo.name = pm.getApplicationInfo(pStats.packageName, 0).loadLabel(pm).toString();
					cacheInfo.size = cache;
					msg.obj = cacheInfo;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}				
		}			
	}
	class CacheInfo{
		Drawable icon;
		String name;
		long size;
		String packname;
	}
}
