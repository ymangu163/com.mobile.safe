package com.mobile.safe.utils;

import java.io.File;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

/**
 * 短信的备份和还原 工具类
 * 短信保存格式
 * <? xml  version>
<smss>
	<sms>
		<body>你好啊</body>
		<date>124451566</date>
		<type>1</type>
		<address>55568445</address>
	</sms>

</smss>
 * 
 */
public class SmsUtils {
	
	public interface BackUpCallBack{
		/**
		 * 短信调用前调用的方法
		 */
		public void beforeSmsBackup(int total);
		
		/**
		 * 短信备份中调用的方法 
		 * @param progress 当前备份的进度。
		 */
		public void onSmsBackup(int progress);
		
	}
	
	
	/**
	 * 短信的备份
	 * @param context 上下文
	 * @param pd 进度条对话框
	 */
	public static void backupSms(Context context,BackUpCallBack backupCallback) throws Exception{
		ContentResolver resolver = context.getContentResolver();
		File file = new File(Environment.getExternalStorageDirectory(),"backuo.xml");
		FileOutputStream fos = new FileOutputStream(file);
		//把用户的短信一条一条读出来,按照一定的格式写到文件里
		XmlSerializer serializer = Xml.newSerializer();//获取xml文件的生存期(系列器)
		//初始化生成器
				serializer.setOutput(fos,"utf-8");
				serializer.startDocument("utf-8",true);//true 是否独立
				serializer.startTag(null,"smss");
				Uri uri = Uri.parse("content://sms/");
				Cursor cursor = resolver.query(uri,new String[]{"body","address","type","date"}, null, null, null);
				//开始备份的时候,设置进度条的最大值
//				int max=cursor.getCount();
//				pd.setMax(max);
				
				backupCallback.beforeSmsBackup(cursor.getCount());
				int progress = 0;
				while (cursor.moveToNext()) {
					Thread.sleep(100);
					String body = cursor.getString(0);
					String address = cursor.getString(1);
					String type = cursor.getString(2);
					String date = cursor.getString(3);
					
					serializer.startTag(null,"sms");
					
					serializer.startTag(null,"body");
					serializer.text(body);
					serializer.endTag(null,"body");
					
					serializer.startTag(null,"address");
					serializer.text(address);
					serializer.endTag(null,"address");
					
					serializer.startTag(null,"type");
					serializer.text(type);
					serializer.endTag(null,"type");
					
					serializer.startTag(null,"date");
					serializer.text(date);
					serializer.endTag(null,"date");
					
					serializer.endTag(null,"sms");
					//备份过程中,增加进度
					progress++;
//					pd.setProgress(progress);
					backupCallback.onSmsBackup(progress);
				}
				serializer.endTag(null,"smss");
				serializer.endDocument();
				fos.close();
				cursor.close();
	}

}
