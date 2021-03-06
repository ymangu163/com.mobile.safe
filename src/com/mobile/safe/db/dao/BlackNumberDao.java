package com.mobile.safe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.mobile.safe.bean.BlackNumberInfo;
import com.mobile.safe.db.BlackNumberDBOpenHelper;

/**
 * 黑名单数据库的增删改查业务类
 */
public class BlackNumberDao {
	private BlackNumberDBOpenHelper helper;
	
	//构造方法
	public BlackNumberDao(Context context) {
		helper = new BlackNumberDBOpenHelper(context);	
	}
	
	// 查询黑名单号码是是否存在
	public boolean find(String number){
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacknumber where number=?", new String[]{number});
		if(cursor.moveToNext()){
			result = true;
		}		
		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 * 查询黑名单号码的拦截模式
	 * @param number
	 * @return 返回号码的拦截模式，不是黑名单号码返回null
	 */
	public String findMode(String number){
		String result = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select mode from blacknumber where number=?", new String[]{number});
		if(cursor.moveToNext()){
			result = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 * 查询全部黑名单号码
	 * @return
	 */
	public List<BlackNumberInfo> findAll(){
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc", null);
		while(cursor.moveToNext()){
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info.setMode(mode);
			info.setNumber(number);
			result.add(info);			
		}
		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 * 查询部分黑名单号码
	 * @param offset 从哪个位置获取数据
	 * @param maxnumber 一起最多获取多少条记录
	 * @return 返回查询出来的号码信息
	 * 
	 * 分页查找的 SQL语句：
	 select * from  blacknumber  limit 10  offset  20 ;   // 每次10条，从20 的地方开始
	 select * from  blacknumber  limit 10，20 ;   // 是一样的功能，  从20 的地方开始，每次10条
	 limit ， offset   只能加在 sql 语句的末尾
	 * 
	 */
	public List<BlackNumberInfo> findPart(int offset,int maxnumber){
		SystemClock.sleep(500);
		List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase(); 
		//limit 10 offset 10只能写到末尾
		Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc limit ? offset ?",
				new String[]{String.valueOf(maxnumber),String.valueOf(offset)});
		while(cursor.moveToNext()){
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info.setNumber(number);
			info.setMode(mode);
			result.add(info);
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
	public void add(String number,String mode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();   //key为String类型
		values.put("number", number);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		db.close();	
		
	}
	
	/**
	 * 修改黑名单号码的拦截模式
	 * @param number 要修改的黑名单号码
	 * @param newmode 新的拦截模式
	 */
	public void update(String number,String newmode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newmode);
		db.update("blacknumber", values, "number=?", new String[]{number});
		db.close();		
	}
		
	/**
	 * 删除黑名单号码
	 * @param number 要删除的黑名单号码
	 */
	public void delete(String number){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("blacknumber",  "number=?", new String[]{number});
		db.close();
	}	
	
}
