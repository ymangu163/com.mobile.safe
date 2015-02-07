package com.mobile.safe.receiver;

import com.lidroid.xutils.util.LogUtils;
import com.mobile.safe.R;
import com.mobile.safe.service.GPSService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
	private SharedPreferences sp;
	@Override
	public void onReceive(Context context, Intent intent) {
		// 写接收短信的代码
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		for(Object b:objs){
			//得到具体的某一条短信
			SmsMessage sms =SmsMessage.createFromPdu((byte[]) b);
			//得到 发送者
			String sender = sms.getOriginatingAddress();//15555555556
			Toast.makeText(context, sender, 1).show();
			String safenumber = sp.getString("safenumber", "");//5556
			String body = sms.getMessageBody();  //得到短信的内容
			
			if (sender.contains(safenumber)) {   //判断是不是来自安全号码

				switch (body) {
				case "#*location*#": // 得到手机的GPS
					LogUtils.i("得到手机的GPS");
					
					//启动服务
					Intent i = new Intent(context,GPSService.class);
					context.startService(i);
					SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
					String lastlocation = sp.getString("lastlocation", null);
					if(TextUtils.isEmpty(lastlocation)){
						//位置没有得到
						SmsManager.getDefault().sendTextMessage(sender, null, "geting loaction.....", null, null);
					}else{
						SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
					}
					
					

					// 把这个广播终止掉,不让手机用户看到短信
					abortBroadcast();
					break;
				case "#*alarm*#": // 播放报警影音
					LogUtils.i("播放报警影音");
					MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
					player.setLooping(false);//true的话就循环播放
					/*
					 * .如果没播放多媒体是调音量改变的是打电话等铃声音量；
					 *  如果在播放多媒体时，改变的是多媒体音量，这时才没有声音
					 */ 
					player.setVolume(1.0f, 1.0f);  //设置左右声道的音量大小 为最大 
					player.start();					
					
					abortBroadcast();
					break;
				case "#*wipedata*#": // 远程清除数据
					LogUtils.i("远程清除数据");

					abortBroadcast();
					break;
				case "#*lockscreen*#": // 远程锁屏
					LogUtils.i("远程锁屏");

					abortBroadcast();
					break;

				default:
					break;
				}

			}
		}
		
		
	}

}
