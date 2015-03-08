package com.mobile.safe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.mobile.safe.db.ApplockDBOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;


/**
 * 黑名单数据库的增删改查业务类
 */
public class ApplockDao {
	private ApplockDBOpenHelper helper;
	
	//构造方法
	public ApplockDao(Context context) {
		helper = new ApplockDBOpenHelper(context);	
	}
	
	// 查询黑名单号码是是否存在
	public boolean find(String packagename){
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor=db.query("applock", null, "packagename=?", new String[]{packagename}, null, null, null);
		
		if(cursor.moveToNext()){
			result = true;
		}		
		cursor.close();
		db.close();
		return result;
	}
	

	

			
	/**
	 * 添加黑名单号码
	 * @param number 黑名单号码
	 * @param mode 拦截模式 1.电话拦截 2.短信拦截 3.全部拦截
	 */
	public void add(String packagename){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();   //key为String类型
		values.put("packagename", packagename);
		db.insert("applock", null, values);
		db.close();	
		
	}
	
		
	/**
	 * 删除黑名单号码
	 * @param number 要删除的黑名单号码
	 */
	public void delete(String packagename){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("applock",  "packagename=?", new String[]{packagename});
		db.close();
	}	
	
}
