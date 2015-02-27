package com.mobile.safe.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.mobile.safe.R;
import com.mobile.safe.adapter.CommonAdapter;
import com.mobile.safe.adapter.ViewHolder;
import com.mobile.safe.bean.BlackNumberInfo;
import com.mobile.safe.db.dao.BlackNumberDao;

public class CallSmsSafeActivity extends Activity {
	private ListView lv_callsms_safe;
	private List<BlackNumberInfo> infos;
	private BlackNumberDao dao;
	private CommonAdapter<BlackNumberInfo> mAdapter;
	private LinearLayout ll_loading;
	private int offset = 0;
	private int maxnumber = 20;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		dao = new BlackNumberDao(this);
		
		fillData();
//		infos = dao.findAll();
		lv_callsms_safe.setOnScrollListener(new OnScrollListener() {
			//当滚动的状态发生变化的时候
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE: //空闲状态
					System.out.println("空闲状态");
					//判断当前listview滚动的位置
					int lastposition = lv_callsms_safe.getLastVisiblePosition();//获取最后一个可见条目在集合里面的位置
					//集合里面有20item 位置从0开始 最后一个位置是19
					if(lastposition == (infos.size()-1)){   // 判断是否到末尾了
						offset += maxnumber;
						fillData();
					}
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: //手指触摸滚动
					break;
				case OnScrollListener.SCROLL_STATE_FLING: //惯性滑行状态
					break;
			}
				
				
			}
			//滚动的时候调用的方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}			
		});		
		
	}
	
	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread(){
			public void run() {
				if(infos == null){
					infos = dao.findPart(offset,maxnumber);
				}else{  //原来已经加载过数据了,则把新加载的数据append到末尾
					infos.addAll(dao.findPart(offset, maxnumber));
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);
						if(mAdapter == null){
							
							mAdapter=new CommonAdapter<BlackNumberInfo>(CallSmsSafeActivity.this,
									infos,R.layout.list_item_callsms) {
										@Override
										public void convert(ViewHolder holder, final BlackNumberInfo item) {
											switch(item.getMode()){
											case "1":
												 holder.setText(R.id.tv_block_mode, "电话拦截");
												break;
											case "2":
												holder.setText(R.id.tv_block_mode, "短信拦截");
												break;
											default:
												holder.setText(R.id.tv_block_mode, "全部拦截");
												break;						
											}	
											
											 holder.setText(R.id.tv_black_number, item.getNumber());
											 
											 /*
											 * 现象：点击ListView的Item,item中有一个ImageView.
												若ImageView设置了监听事件，则Item背景变化时，ImageView不变化；
												若ImageView未设置监听事件，则Item背景变化时，ImageView也变化；
												事件传递原理：
												  点击时实现点击的是手机屏幕硬件，硬件驱动程序解析点击的位置并告诉系统；系统把点击事件一层层向子view传递；若最终级的子view 设置了OnClickListener点击监听事件，则把该事件消费掉；
												  若最终级的子View未设置点击监听事件，则把该事件又一层层向上传递回去，看哪个设置了点击监听；
												  若都没有设置监听，则不处理。
											 */
											 
											 holder.getView(R.id.iv_delete).setOnClickListener(new OnClickListener() {
												@Override
												public void onClick(View v) {
													AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
													builder.setTitle("警告");
													builder.setMessage("确定要删除这条记录么？");
													builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
														@Override
														public void onClick(DialogInterface dialog, int which) {
															//删除数据库的内容
															dao.delete(item.getNumber());
															//更新界面。
															infos.remove(item);
															//通知listview数据适配器更新
															mAdapter.notifyDataSetChanged();
														}
													});
													builder.setNegativeButton("取消", null);
													builder.show();					
													
												}							 
											 });
											
										}
								
							};
							lv_callsms_safe.setAdapter(mAdapter);
						}else{
							mAdapter.notifyDataSetChanged();
						}
						
					}
				});
				
			};
			
		}.start();
		
	}

	private EditText et_blacknumber;
	private CheckBox cb_phone;
	private CheckBox cb_sms;
	private Button bt_ok;
	private Button bt_cancel;
	
	public void addBlackNumber(View view){
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View contentView = View.inflate(this, R.layout.dialog_add_blacknumber, null);
		et_blacknumber = (EditText) contentView.findViewById(R.id.et_blacknumber);
		cb_phone = (CheckBox) contentView.findViewById(R.id.cb_phone);
		cb_sms = (CheckBox) contentView.findViewById(R.id.cb_sms);
		bt_cancel = (Button) contentView.findViewById(R.id.cancel);
		bt_ok = (Button) contentView.findViewById(R.id.ok);
		dialog.setView(contentView, 0, 0, 0, 0);
		dialog.show();
		
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String blacknumber = et_blacknumber.getText().toString().trim();
				if(TextUtils.isEmpty(blacknumber)){
					Toast.makeText(getApplicationContext(), "黑名单号码不能为空", 0).show();
					return;
				}
				String mode ;
				if(cb_phone.isChecked()&&cb_sms.isChecked()){
					//全部拦截
					mode = "3";
				}else if(cb_phone.isChecked()){
					//电话拦截
					mode = "1";
				}else if(cb_sms.isChecked()){
					//短信拦截
					mode = "2";
				}else{
					Toast.makeText(getApplicationContext(), "请选择拦截模式", 0).show();
					return;
				}
				//数据被加到数据库
				dao.add(blacknumber, mode);
				//更新listview集合里面的内容。
				BlackNumberInfo info = new BlackNumberInfo();
				info.setMode(mode);
				info.setNumber(blacknumber);
				infos.add(0, info);
				//通知listview数据适配器数据更新了。
				mAdapter.notifyDataSetChanged();
				dialog.dismiss();				
			}			
		});		
	}
	
	
	
	
}
