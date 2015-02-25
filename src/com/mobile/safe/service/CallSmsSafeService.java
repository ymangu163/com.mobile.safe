package com.mobile.safe.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.lidroid.xutils.util.LogUtils;
import com.mobile.safe.db.dao.BlackNumberDao;

public class CallSmsSafeService extends Service {
	private InnerSmsReceiver receiver;
	private BlackNumberDao dao;
	private TelephonyManager tm;
	private MyListener listener;
//	private MyListener listener;
	
	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}
	
	@Override
	public void onCreate() {
		dao = new BlackNumberDao(this);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		
		receiver = new InnerSmsReceiver();
		IntentFilter filter =  new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver,filter);
		
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;	
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		super.onDestroy();
	}
	
	
	private class MyListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch(state){
			case TelephonyManager.CALL_STATE_RINGING:  //响铃状态
				String result = dao.findMode(incomingNumber);
				if("1".equals(result)||"3".equals(result)){
					//删除呼叫记录
					//另外一个应用程序联系人的应用的私有数据库
//					deleteCallLog(incomingNumber);
					//观察呼叫记录数据库内容的变化
					Uri uri = Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(uri,true,new CallLogObserver(incomingNumber,new Handler()));
					
					LogUtils.i("挂断电话。。。。");
					////挂断电话 另外一个进程里面运行的 远程服务的方法。方法调用后,呼叫记录可能还没有生产。
					endCall();
				
				break;	
			}
			super.onCallStateChanged(state, incomingNumber);
		}	
	}


		public void endCall() {
			
			//通过反射，加载servicemanager的字节码
			try {
				// 通过具体类名得到类
				Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
				Method method = clazz.getDeclaredMethod("getService", String.class);  // 得到该类的方法
				IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
				ITelephony.Stub.asInterface(ibinder).endCall();
			
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}	
	}
	
	/**
	 * 利用内容提供者删除呼叫记录
	 */
	private void deleteCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		//呼叫记录uri的路径
		Uri uri = Uri.parse("content://call_log/calls");
		System.out.println("删除 "+incomingNumber);
		resolver.delete(uri,"number=?",new String[]{incomingNumber});
		
	}
	
	/**
	 * 监听通话记录
	 */
	private class CallLogObserver extends ContentObserver{
		private String incomingNumber;
		public CallLogObserver( String incomingNumber,Handler handler) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}
		@Override
		public void onChange(boolean selfChange) {
			System.out.println("数据库产生了呼叫记录");
			getContentResolver().unregisterContentObserver(this);  //只观察 一会，不用一直观察
			deleteCallLog(incomingNumber);  // 观察的数据库一变化 ，就删除掉记录
			super.onChange(selfChange);
		}
		
		
		
		
	}
	

	private class InnerSmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtils.i("内部广播接受者， 短信到来了");
			//检查发件人是否是黑名单号码，设置短信拦截全部拦截。
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for(Object obj:objs){
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				//得到短信发件人
				String sender = smsMessage.getOriginatingAddress();
				String result = dao.findMode(sender);
				if("2".equals(result)||"3".equals(result)){
					LogUtils.i("拦截短信");
					abortBroadcast();
				}
				//演示代码。
				String body = smsMessage.getMessageBody();
				if(body.contains("fapiao")){
					//你的头发票亮的很  语言分词技术。
					LogUtils.i("拦截发票短信");
					abortBroadcast();
				}
				
			}
			
		}
		
		
		
		
	}
	
	
	
	
	
	
}
