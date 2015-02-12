package com.mobile.safe.service;

import com.lidroid.xutils.util.LogUtils;
import com.mobile.safe.R;
import com.mobile.safe.db.dao.NumberAddressQueryUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class AddressService extends Service {

	/**
	 * 窗体管理者
	 */
	private WindowManager wm;
	private View view;

	/**
	 * 电话服务
	 */
	private TelephonyManager tm;
	private MyListenerPhone listenerPhone;	
	private OutCallReceiver2 receiver;
	private WindowManager.LayoutParams params;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		// 监听来电
		listenerPhone = new MyListenerPhone();
		tm.listen(listenerPhone, PhoneStateListener.LISTEN_CALL_STATE);
		
		// 用代码去注册广播接收者
		receiver = new OutCallReceiver2();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		filter.addAction("android.intent.action.PHONE_STATE");
		filter.addCategory("android.intent.category.DEFAULT");
		registerReceiver(receiver, filter);
		LogUtils.d("生成了服务对象，注册了广播");
		
		//实例化窗体
	    wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// 取消监听来电
		tm.listen(listenerPhone, PhoneStateListener.LISTEN_NONE);
		listenerPhone = null;

		// 用代码取消注册广播接收者
		unregisterReceiver(receiver);
		receiver = null;
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	/*
	 * .广播接收者的生命周期和服务一样
	 */
	class OutCallReceiver2 extends BroadcastReceiver {
		public OutCallReceiver2() {
			LogUtils.d("生成广播对象");
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			// 这就是我们拿到的播出去的电话号码
			String phone = getResultData();
			LogUtils.d("phone:"+phone);
			if(phone!=null){
				// 查询数据库
				String address = NumberAddressQueryUtils.queryNumber(phone);
				myToast(address);			
				
			}else{				
				myToast("重庆网易");			
				
			}
		}
	}
	

	/**
	 * 自定义土司
	 * @param address
	 */
	public void myToast(String address) {
	     view =   View.inflate(this, R.layout.address_show, null);
	    TextView textview  = (TextView) view.findViewById(R.id.tv_address);
	    
	    //"半透明","活力橙","卫士蓝","金属灰","苹果绿"
	    int [] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue
	    ,R.drawable.call_locate_gray,R.drawable.call_locate_green};
	    
	    SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
	    view.setBackgroundResource(ids[sp.getInt("which", 0)]);
	    textview.setText(address);
	
	    // 给view对象设置一个触摸的监听器
	    view.setOnTouchListener(new OnTouchListener() {		
			// 定义手指的初始化位置
			int startX;
			int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// 手指按下屏幕
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					LogUtils.i( "手指摸到控件");
					break;
				case MotionEvent.ACTION_MOVE:// 手指在屏幕上移动
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					int dx = newX - startX;
					int dy = newY - startY;
					LogUtils.i("手指在控件上移动");
					params.x += dx;
					params.y += dy;
				
					
					
					wm.updateViewLayout(view, params);    //更新view的位置
					// 重新初始化手指的开始结束位置。
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
					
				
				}
				
				
				return true;// 事件处理完毕了。不要让父控件 父布局响应触摸事件了。
			}
		});
	    
	    
	    
	    
	    
	    
	    
		params = new WindowManager.LayoutParams();
		 
         params.height = WindowManager.LayoutParams.WRAP_CONTENT;
         params.width = WindowManager.LayoutParams.WRAP_CONTENT;
         
         
         
         // 去掉不可触摸项
         params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                 | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
         params.format = PixelFormat.TRANSLUCENT;
         
      // android系统里面具有电话优先级的一种窗体类型，记得添加权限。
 		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		wm.addView(view, params);
		
	}
	
	private class MyListenerPhone extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 来电铃声响起
				// 查询数据库的操作
				String address = NumberAddressQueryUtils.queryNumber(incomingNumber);
				myToast(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE:   //电话的空闲状态：挂电话、来电拒绝
				//把这个View移除
				if(view != null ){
					wm.removeView(view);   // 让自定义Toast消失
				}
				break;	
			
			default:
				break;			
			}
			
		}
	}
	
}
