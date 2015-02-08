package com.mobile.safe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQueryUtils {
	
	private static String path = "data/data/com.mobile.safe/files/address.db";

	
	/**
	 * 传一个号码进来，返回一归属地回去
	 */
	public static String queryNumber(String number) {
		String address = number;
		// path 把address.db这个数据库拷贝到data/data/《包名》/files/address.db
				SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
						SQLiteDatabase.OPEN_READONLY);
	
				Cursor cursor = database.rawQuery(
								"select location from data2 where id = (select outkey from data1 where id = ?)",
								new String[] { number.substring(0, 7) });
				
				while (cursor.moveToNext()) {
					String location = cursor.getString(0);
					address = location;
				}
				cursor.close();
				
	
	return address;
	}

}
