package com.mobile.safe.activity;

import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.mobile.safe.R;
import com.mobile.safe.adapter.CommonAdapter;
import com.mobile.safe.adapter.ViewHolder;
import com.mobile.safe.bean.BlackNumberInfo;
import com.mobile.safe.bean.HomeItemBean;
import com.mobile.safe.db.dao.BlackNumberDao;

public class CallSmsSafeActivity extends Activity {
	private ListView lv_callsms_safe;
	private List<BlackNumberInfo> infos;
	private BlackNumberDao dao;
	private CommonAdapter<BlackNumberInfo> mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
		dao = new BlackNumberDao(this);
		infos = dao.findAll();
		lv_callsms_safe.setAdapter( mAdapter=new CommonAdapter<BlackNumberInfo>(CallSmsSafeActivity.this,
				infos,R.layout.list_item_callsms) {

					@Override
					public void convert(ViewHolder holder, BlackNumberInfo item) {
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
						
						
					}
			
		});
		
		
	}
	
	
	public void addBlackNumber(View view){
		long basenumber = 13500000000l;
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			dao.add(String.valueOf(basenumber+i), String.valueOf(random.nextInt(3)+1));
		}
		
		mAdapter.notifyDataSetChanged();
	}
	
	
	
	
}
