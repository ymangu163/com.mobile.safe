package com.mobile.safe.receiver;

import com.lidroid.xutils.util.LogUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 写接收短信的代码
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		
		for(Object b:objs){
			//得到具体的某一条短信
			SmsMessage sms =SmsMessage.createFromPdu((byte[]) b);
			//得到 发送者
			String sender = sms.getOriginatingAddress();//15555555556
//			String safenumber = sp.getString("safenumber", "");//5556
			String body = sms.getMessageBody();  //得到短信的内容
			
			switch (body) {
			case "#*location*#":   //得到手机的GPS				
				LogUtils.i( "得到手机的GPS");
				
				//把这个广播终止掉,不让手机用户看到短信
				abortBroadcast();
				break;
			case "#*alarm*#":  	//播放报警影音
				LogUtils.i("播放报警影音");
				
				abortBroadcast();
				break;
			case "#*wipedata*#":  	//远程清除数据
				LogUtils.i("远程清除数据");
				
				abortBroadcast();
				break;
			case "#*lockscreen*#":  	//远程锁屏
				LogUtils.i("远程锁屏");
				
				abortBroadcast();
				break;
				
			default:
				break;
			}
			
		}
		
		
		
	}

}
