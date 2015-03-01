package com.mobile.safe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mobile.safe.R;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		ViewUtils.inject(this);
		showAvailableSize();//显示存储的剩余空间
		
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
