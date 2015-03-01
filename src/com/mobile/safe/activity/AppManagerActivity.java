package com.mobile.safe.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mobile.safe.R;
import com.mobile.safe.adapter.CommonAdapter;
import com.mobile.safe.adapter.ViewHolder;
import com.mobile.safe.bean.AppInfo;
import com.mobile.safe.engine.AppInfoProvider;

public class AppManagerActivity extends Activity {

	@ViewInject(R.id.tv_status)
	private TextView tv_status;
	@ViewInject(R.id.tv_avail_rom)
	private TextView tv_avail_rom;
	@ViewInject(R.id.tv_avail_sd)
	private TextView tv_avail_sd;
	@ViewInject(R.id.lv_app_manager)
	private ListView lv_app_manager;
	@ViewInject(R.id.ll_loading)
	private LinearLayout ll_loading;
	
	//应用程序包集合
		private List<AppInfo> appInfos; //所有应用程序包集合
		private List<AppInfo> userAppInfos;//所有用户程序包集合
		private List<AppInfo> systemAppInfos;//所有系统程序包集合
		
		private CommonAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		ViewUtils.inject(this);
		showAvailableSize();//显示存储的剩余空间
		fillListViewData();
	}
	
	/**
	 * 填充Adapter数据
	 */
	private void fillListViewData() {
		ll_loading.setVisibility(View.VISIBLE);//让加载程序的界面显示出来
		new Thread(){
			public void run() {
			//所有应用程序包的集合
			appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				
			// 加载listview的数据适配器
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if(adapter == null){ //如果适配器为空 则创建适配器对象 为listview设置adapter
						adapter = new CommonAdapter<AppInfo>(AppManagerActivity.this,appInfos,
									R.layout.list_item_appinfo) {
							@Override
							public void convert(ViewHolder holder, AppInfo item) {
								
								holder.setImageDrawable(R.id.iv_app_icon, item.getIcon());
								holder.setText(R.id.tv_app_name, item.getName());							
								
							}
							
						};
						lv_app_manager.setAdapter(adapter);
					}else{//
						adapter.notifyDataSetChanged(); //动态更新ListView
					}
					ll_loading.setVisibility(View.INVISIBLE);	
				}
				
			});
				
			};

			
			
			
			
		}.start();
		
		
		
	}

	/**
	 * 显示存储的剩余空间
	 */
	private void showAvailableSize() {
		long romSize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());//手机内部存储大小
		long sdSize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());//外部存储大小
		tv_avail_rom.setText("内存可用空间: "+Formatter.formatFileSize(this,romSize));
		tv_avail_sd.setText("SD卡可用空间:"+Formatter.formatFileSize(this,sdSize));		
	}
	
	/**
	 * 获取某个目录的可用空间
	 */
	private long getAvailSpace(String path) {
		StatFs statfs = new StatFs(path);
		long size = statfs.getBlockSize();//获取分区的大小
		long count = statfs.getAvailableBlocks();//获取可用分区块的个数
		return size*count;
	
	}
	
	
	
	
	
	
	
	
}
