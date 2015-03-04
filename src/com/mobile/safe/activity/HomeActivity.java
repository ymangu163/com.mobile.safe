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
import com.mobile.safe.utils.MD5Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

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

	private SharedPreferences sp;

	private AlertDialog dialog;

	private EditText et_setup_pwd;

	private EditText et_setup_confirm;

	private Button ok,cancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ViewUtils.inject(this);   //注入View 和事件
		initDatas();
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
	
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
				Intent intent=null;
				
				switch (position) {
				case 0:  //进入手机防盗页面
					showLostFindDialog();
					break;
				case 1://加载黑名单拦截界面
					intent = new Intent(HomeActivity.this,CallSmsSafeActivity.class);
					startActivity(intent);	
					break;
				case 2://加载应用管理
					intent = new Intent(HomeActivity.this,AppManagerActivity.class);
					startActivity(intent);	
					break;
				case 3://加载应用管理
					intent = new Intent(HomeActivity.this,TaskManagerActivity.class);
					startActivity(intent);	
					break;
				case 7://进入高级工具
					intent = new Intent(HomeActivity.this,AtoolsActivity.class);	
					startActivity(intent);		
					break;
				case 8://进入设置中心
					intent = new Intent(HomeActivity.this,SettingActivity.class);		
					startActivity(intent);		
					break;

				default:
					break;
				}
						
			}
		});
		
	}
	
	 //进入手机防盗页面
	protected void showLostFindDialog() {
		//判断是否设置过密码
		if(isSetupPwd()){
			//已经设置密码了，弹出的是输入对话框
			showEnterDialog();
		}else{
			//没有设置密码，弹出的是设置密码对话框
			showSetupPwdDialog();
		}
		}
	
	/**
	 * 设置密码对话框
	 */
	private void showSetupPwdDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		// 自定义一个布局文件
		View view = View.inflate(HomeActivity.this, R.layout.dialog_setup_password, null);
		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		et_setup_confirm = (EditText) view.findViewById(R.id.et_setup_confirm);
		ok = (Button) view.findViewById(R.id.ok);
		cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//把这个对话框取消掉
				dialog.dismiss();
			}
		});
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			//  取出密码
				String password = et_setup_pwd.getText().toString().trim();
				String password_confirm = et_setup_confirm.getText().toString().trim();	
				if(TextUtils.isEmpty(password) || TextUtils.isEmpty(password_confirm)){
					Toast.makeText(HomeActivity.this, "密码为空", 0).show();
					return;
				}
				//判断是否一致才去保存
				if(password.equals(password_confirm)){
					//一致的话，就保存密码，把对话框消掉，还要进入手机防盗页面
					Editor editor = sp.edit();
					editor.putString("password", MD5Utils.md5Password(password));//保存加密后的
					editor.commit();
					dialog.dismiss();
					
					LogUtils.i("一致的话，就保存密码，把对话框消掉，还要进入手机防盗页面");
					Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(intent);
					
				}else{
					Toast.makeText(HomeActivity.this, "密码不一致", 0).show();
					return ;					
				}
			}
			
		});	
		
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		
		
	}

	/**
	 * 输入密码对话框
	 */
	private void showEnterDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		// 自定义一个布局文件
		View view = View.inflate(HomeActivity.this, R.layout.dialog_enter_password, null);
		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		ok = (Button) view.findViewById(R.id.ok);
		cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//把这个对话框取消掉
				dialog.dismiss();
			}
		});
		
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 取出密码
				String password = et_setup_pwd.getText().toString().trim();
				String savePassword = sp.getString("password", "");// 取出加密后的
				if(TextUtils.isEmpty(password)){
					Toast.makeText(HomeActivity.this, "密码为空", 1).show();
					return;
				}
				
				if(MD5Utils.md5Password(password).equals(savePassword)){
					//输入的密码是我之前设置的密码
					//把对话框消掉，进入主页面；
					dialog.dismiss();
					Log.i("TAG", "把对话框消掉，进入手机防盗页面");
					Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(intent);
					
				}else{
					Toast.makeText(HomeActivity.this, "密码错误", 1).show();
					et_setup_pwd.setText("");
					return;
				}
				
			}			
		});
		
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		
	}

	/**
	 * 判断是否设置过密码
	 */	
	private boolean isSetupPwd() {
		String password = sp.getString("password", null);
		return !TextUtils.isEmpty(password);
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


