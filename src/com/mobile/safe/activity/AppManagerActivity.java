package com.mobile.safe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
		
		private AppManagerAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		ViewUtils.inject(this);
		showAvailableSize();//显示存储的剩余空间
		fillListViewData();
		// 给listview注册一个滚动的监听器
				lv_app_manager.setOnScrollListener(new OnScrollListener() {
					//当前滚动的状态
					@Override
					public void onScrollStateChanged(AbsListView view,int scrollState) {
						switch (scrollState) {
						case OnScrollListener.SCROLL_STATE_FLING: //开始滚动
							break;
						case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://正在滚动
							break;
						case OnScrollListener.SCROLL_STATE_IDLE://停止滚动
							break;
					}					
					}
					// 滚动的时候调用的方法。
					// firstVisibleItem 第一个可见条目在listview集合里面的位置。
					@Override
					public void onScroll(AbsListView view,int firstVisibleItem, int visibleItemCount,
							int totalItemCount) {
						if(userAppInfos != null && systemAppInfos != null){
							if(firstVisibleItem > userAppInfos.size()){
								tv_status.setText("系统程序:"+systemAppInfos.size()+"个");
							}else{
								tv_status.setText("用户程序:"+userAppInfos.size()+"个");
							}
						}
					}					
				});
		
		
		
		
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
			userAppInfos = new ArrayList<AppInfo>();
			systemAppInfos = new ArrayList<AppInfo>();
			for (AppInfo info : appInfos) {
				if(info.isUserApp()){ //如果是 用户程序
					userAppInfos.add(info);
				}else{
					systemAppInfos.add(info);
				}
			}
			
				
			// 加载listview的数据适配器
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(adapter == null){ //如果适配器为空 则创建适配器对象 为listview设置adapter
						adapter = new AppManagerAdapter();
						lv_app_manager.setAdapter(adapter);
					}else{//
						adapter.notifyDataSetChanged(); //动态更新ListView
					}
					ll_loading.setVisibility(View.INVISIBLE);	
					tv_status.setVisibility(View.VISIBLE);
				}				
			});				
			};

		}.start();
		
		
		
		
		
		
	}
	
	/**
	 * ListView的适配器
	 */
	private class AppManagerAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return userAppInfos.size()+1+systemAppInfos.size()+1;
		}		
		
		@Override
		public Object getItem(int position) {
			return null;
		}
		@Override
		public long getItemId(int position) {
			return 0;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appInfo;
			if(position == 0){ //显示用户程序有多少个的小标签
				TextView tv = new TextView(getApplication());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("用户程序:"+userAppInfos.size()+"个");
				return tv;
			}else if(position == (userAppInfos.size()+1)){
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("系统程序:"+systemAppInfos.size()+"个");
				return tv;
			}else if(position <= userAppInfos.size()){
				int newPosition = position-1;
				appInfo = userAppInfos.get(newPosition);
			}else{
				int newPosition = position-1-userAppInfos.size()-1;
				appInfo = systemAppInfos.get(newPosition);
			}
			View view;
			ViewHolder holder;
			// 不仅需要检查是否为空，还要判断是否是合适的类型去复用
			if(convertView != null && convertView instanceof RelativeLayout){
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else{
				view = View.inflate(getApplicationContext(),R.layout.list_item_appinfo,null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				holder.tv_location = (TextView) view.findViewById(R.id.tv_app_location);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
				view.setTag(holder);
			}
			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_name.setText(appInfo.getName());
			if(appInfo.isInRom()){
				holder.tv_location.setText("手机内存");
			}else{
				holder.tv_location.setText("外部存储");
			}
			return view;
		}
	}
	
	static class ViewHolder {
		TextView tv_name;
		TextView tv_location;
		ImageView iv_icon;
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
