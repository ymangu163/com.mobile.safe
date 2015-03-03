package com.mobile.safe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mobile.safe.R;
import com.mobile.safe.adapter.CommonAdapter;
import com.mobile.safe.adapter.ViewHolder;
import com.mobile.safe.bean.AppInfo;
import com.mobile.safe.engine.AppInfoProvider;
import com.mobile.safe.utils.DensityUtil;

public class AppManagerActivity extends Activity implements OnClickListener {

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
		//被点击的条目
		private AppInfo appInfo;
		private LinearLayout ll_start;//开启
		private LinearLayout ll_uninstall;//卸载
		private LinearLayout ll_share;//分享
	
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
						dismissPopupWindow();
						if(userAppInfos != null && systemAppInfos != null){
							if(firstVisibleItem > userAppInfos.size()){
								tv_status.setText("系统程序:"+systemAppInfos.size()+"个");
							}else{
								tv_status.setText("用户程序:"+userAppInfos.size()+"个");
							}
						}
					}					
				});
		
				/**
				 * 设置listview的点击事件
				 */
				lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if(position == 0 || position == userAppInfos.size()+1){ //如果是"用户程序" 或者 "系统程序" 的小标签则直接返回
							return;
						}else if(position <= userAppInfos.size()){
							int newPosition = position - 1;
							appInfo = userAppInfos.get(newPosition);
						}else{
							int newPosition = position-1-userAppInfos.size()-1;
							appInfo = systemAppInfos.get(newPosition);
						}
						dismissPopupWindow();
						View contentView = View.inflate(getApplicationContext(),R.layout.popup_app_item,null);
						ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
						ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);
						ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);
						
						//为悬浮窗体的各项设置点击事件
						ll_uninstall.setOnClickListener(AppManagerActivity.this);
						ll_start.setOnClickListener(AppManagerActivity.this);
						ll_share.setOnClickListener(AppManagerActivity.this);
						
						//弹出悬浮窗体
						popupWindow = new PopupWindow(contentView,-2,-2);           
						// 动画效果的播放必须要求窗体有背景颜色
						
						
						popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
						int[] location = new int[2];
						view.getLocationInWindow(location);
						int dip = 60;
						int px = DensityUtil.dip2px(getApplicationContext(),dip);
						popupWindow.showAtLocation(parent,Gravity.LEFT|Gravity.TOP,px,location[1]);
						
						ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0.5f);
						sa.setDuration(300);
						AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
						aa.setDuration(300);
						AnimationSet set = new AnimationSet(false);
						set.addAnimation(aa);
						set.addAnimation(sa);
						contentView.startAnimation(set);
					}			
					
				});
		
		
	}
	//悬浮窗体
	private PopupWindow popupWindow;
	private void dismissPopupWindow() {
		//把旧的窗体关闭
				if(popupWindow != null && popupWindow.isShowing()){
					popupWindow.dismiss();
					popupWindow = null;
				}	
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
	
	@Override
	protected void onDestroy() {
		dismissPopupWindow();
		super.onDestroy();
	}
	
	
	/*
	 * . 开户一个应用程序
	 */
	private void startApplication(){
		// 查询这个应用程序的入口Activity,把它开启起来
		PackageManager pm=getPackageManager();
//		Intent intent=new Intent();
//		intent.setAction("android.intent.action.MAIN");
//		intent.addCategory("android.intent.category.LAUNCHER");
//		// 查询出来了所有的手机上具有启动能力的Activity
//		List<ResolveInfo> infos=pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackname());
		if(intent != null){
			startActivity(intent);
		}else{
			Toast.makeText(this,"不能开启当前应用！",0).show();
		}
		
	}


	/**
	 * 悬浮窗体的点击事件
	 */
	@Override
	public void onClick(View v) {
		dismissPopupWindow();
		switch (v.getId()) {
		case R.id.ll_uninstall: //卸载
			if(appInfo.isUserApp()){
				uninstallAppliation();
			}else{
				Toast.makeText(getApplicationContext(),"系统应用必须有root权限",0).show();
			}
			break;
		case R.id.ll_start: //开启
			startApplication();
			break;
		case R.id.ll_share: //分享
			shareApplication();
			break;
	}
		
		
	}



	private void shareApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,"推荐您使用一款软件,名称为: "+appInfo.getName());
		startActivity(intent);
		
	}

	private void uninstallAppliation() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.View");
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:"+appInfo.getPackname()));
		startActivityForResult(intent,0);	
		
	}
	//刷新界面
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			fillListViewData();
			super.onActivityResult(requestCode, resultCode, data);
		}
	
	
}
