package com.mobile.safe.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mobile.safe.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/*
 * ① 联系人数据被保存在 /data/data/com.android.providers.contacts的contacts2.db  SQLite数据库中。
 *    其实是用Content Provider 存贮的，所以读取要用 ContentResolver.
 * ② 主要关注3张表：data,view_data,raw_contacts.
 *  raw_contacts:    得到contact_id
 * 	 dara:    通过 contact_id  找到对应的联系人数据
 *  view_data:     mimeType 表示同一个联系的人姓名、电话等内容  
 *  mimetypes: 表示的是类型对应的id
 */
public class SelectContactActivity extends Activity {
	
	@ViewInject(R.id.list_select_contact)
	private ListView list_select_contact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		ViewUtils.inject(this);
		
		final List<Map<String, String>> data = getContactInfo();
		list_select_contact.setAdapter(new SimpleAdapter(this, data,
				R.layout.contact_item_view, new String[] { "name", "phone" },
				new int[] { R.id.tv_name, R.id.tv_phone }));
		list_select_contact.setOnItemClickListener(new OnItemClickListener() {
			/*
			 *  第1个参数 parent：相当于ListView
			 *  第2个参数 view: Item 的View
			 *  第3个参数 position：点击的Item的位置
			 *  第4个参数 id：点击的Item的行id
			 */
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				/*
				 *  注意：
				 *   这里data要定义成final, 只要外面的数据变化 会引起里面的点击数据变化的情况，外面的数据就要定义成
				 *   final
				 */
				
				String phone = data.get(position).get("phone");
				Intent data = new Intent();
				data.putExtra("phone", phone);
				setResult(0, data);   // 以 Intent的形式把数据返回
				//当前页面关闭掉
				finish();
			}
			
			
			
		});
		
	}

	
	/**
	 * 读取手里面的联系人
	 */
	private List<Map<String, String>> getContactInfo() {
		// 把所有的联系人保存起来
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		// 得到一个内容解析器
		ContentResolver resolver = getContentResolver();
		// raw_contacts uri
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri uriData = Uri.parse("content://com.android.contacts/data");
		
		// ① 查询得到联系人id
		Cursor cursor = resolver.query(uri, new String[] { "contact_id" },	null, null, null);
		while (cursor.moveToNext()) {
			String contact_id = cursor.getString(0);
			// ② 根据联系人id，得到具体联系人的数据
			if (contact_id != null) {
				//具体的某一个联系人
				Map<String, String> map = new HashMap<String, String>();
				
				Cursor dataCursor = resolver.query(uriData, new String[] {
						"data1", "mimetype" }, "contact_id=?",
						new String[] { contact_id }, null);
				
				while (dataCursor.moveToNext()) {
					String data1 = dataCursor.getString(0);
					String mimetype = dataCursor.getString(1);
					System.out.println("data1=="+data1+"==mimetype=="+mimetype);
					
					if("vnd.android.cursor.item/name".equals(mimetype)){
						//联系人的姓名
						map.put("name", data1);
					}else if("vnd.android.cursor.item/phone_v2".equals(mimetype)){
						//联系人的电话号码
						map.put("phone", data1);
					}
				}
				list.add(map);
				dataCursor.close();
				
			}		
			
		}	    
		cursor.close();
		return list;
	}
	
	
	
	
	
	
	
	

}
