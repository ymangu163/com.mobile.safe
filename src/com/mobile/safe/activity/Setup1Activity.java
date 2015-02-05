package com.mobile.safe.activity;

import com.mobile.safe.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class Setup1Activity extends Activity {
	// ① 定义一个手势识别器
	private GestureDetector detector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup_one);		
		
		// ② 实例化
		detector=new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
			/*
			 *  当手指在屏幕上快速滑动时调用 
			 *   e1 --- 按下时的事件
			 *   e2 --- 滑动时的事件
			 *   velocityX  -- x轴速度 ，单位 像素
			 *   velocityY --- Y轴速度
			 */			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if((e2.getRawX()-e1.getRawX())>200){
					// 显示上一个页面，从左往右滑动
					return true;
				}
				if((e1.getRawX()-e2.getRawX())>200){
					// 显示下一个页面，从右往左滑动
					showNext();
					return true;
		}				
				return super.onFling(e1, e2, velocityX, velocityY);
			}
			
			
		});
		
		
	}
	
	// ③ 使用手势识别器
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		detector.onTouchEvent(event);
		
		return super.onTouchEvent(event);
	}
	
	
	
	
	// 下一步
	public void next(View view){
		showNext();
		
	}

	private void showNext() {
		Intent intent = new Intent(this,Setup2Activity.class);
		startActivity(intent);
		finish();	
		//要求在finish()或者startActivity(intent);后面执行；
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
	}
	


}
