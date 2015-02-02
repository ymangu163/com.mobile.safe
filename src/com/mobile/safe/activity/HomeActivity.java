package com.mobile.safe.activity;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mobile.safe.R;
import com.mobile.safe.adapter.CommonAdapter;
import com.mobile.safe.adapter.ViewHolder;
import com.mobile.safe.bean.HomeItemBean;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class HomeActivity extends Activity {
	
	@ViewInject(R.id.list_home)
	private GridView gridView;
	
	private List<HomeItemBean> mDatas = new ArrayList<HomeItemBean>();
	private static String [] names = {
		"手机防盗","通讯卫士","软件管理",
		"进程管理","流量统计","手机杀毒",
		"缓存清理","高级工具","设置中心"		
	};
	
	private static int[] ids = {
		R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,
		R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
		R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings		
	};

	private CommonAdapter<HomeItemBean> mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ViewUtils.inject(this);   //注入View 和事件
		initDatas();
		
	
		gridView.setAdapter(	mAdapter = new CommonAdapter<HomeItemBean>(
				getApplicationContext(),mDatas,R.layout.list_item_home){

			@Override
			public void convert(ViewHolder holder, HomeItemBean item) {
				holder.setText(R.id.tv_item, item.getStr());
				holder.setImageResource(R.id.iv_item, item.getImageId());						
			}
		});
		
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 8://进入设置中心
					Intent intent = new Intent(HomeActivity.this,SettingActivity.class);
					startActivity(intent);
					
					break;

				default:
					break;
				}
			}
		});
		
	}

	// 初始化Adapter的数据源
	private void initDatas() {
		HomeItemBean homeBean=null;
		for(int i=0;i<names.length;i++){
			homeBean=new HomeItemBean(names[i], ids[i]);
			mDatas.add(homeBean);		
			LogUtils.d("names"+names[i]+"  ,ids"+ids[i]);
		}		
	}
	
	
}


