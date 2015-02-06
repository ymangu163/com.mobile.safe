package com.mobile.safe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	
		// ① 定义一个手势识别器
		private GestureDetector detector;
		
		protected SharedPreferences sp;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
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
					showPre();
					return true;
				}
				if((e1.getRawX()-e2.getRawX())>200){
					// 显示下一个页面，从右往左滑动
					showNext();
					return true;
				}		
				
				//屏蔽斜滑这种情况
				if(Math.abs((e2.getRawY() - e1.getRawY())) > 100){
					Toast.makeText(getApplicationContext(), "不能这样滑", 0).show();					
					return true;
				}
				
				//屏蔽在X滑动很慢的情形				
				if(Math.abs(velocityX)<200){
					Toast.makeText(getApplicationContext(), "滑动得太慢了", 0).show();
					return true;
				}
								
				return super.onFling(e1, e2, velocityX, velocityY);
			}			
			
			
			
			
		});
	
	
	}

	public abstract void showNext() ;
	// 下一步
		public void next(View view){
			showNext();			
		}
		public void pre(View view){
			showPre();			
		}

	public abstract void showPre() ;

	// ③ 使用手势识别器
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		detector.onTouchEvent(event);
		
		return super.onTouchEvent(event);
	}
	
	
}
