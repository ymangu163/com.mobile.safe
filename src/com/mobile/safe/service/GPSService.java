package com.mobile.safe.service;

import java.io.IOException;
import java.io.InputStream;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;


/*
 * 定位的3种方式：
	① 网络定位：通过Ip地址，ip数据库定位
		缺点：动态ip时 不太准
	② 基站定位：
	  精确度 与基站数量有关，精确度：几十米到几公里不等；
	③ GPS 定位
	  通过卫星定位，不需要网络。
	  缺点： 头顶必须是空旷的；   影响条件：云层、大厦、大树。
	A-GPS 使用了卫星定位 需要联网辅助修正位置
	使用网络获取上次保存的卫星轨道等信息使得连接GPS卫星更快
 */
public class GPSService extends Service {  //写在服务里是要让它没有界面
		// 用到位置服务
		private LocationManager lm;
		private MyLocationListener listener;
	
	@Override
	public void onCreate() {
		super.onCreate();
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		listener = new MyLocationListener();
		// 注册监听位置服务
		// 给位置提供者设置条件
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// 设置参数细化：
		// criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最大精度
		// criteria.setAltitudeRequired(false);//不要求海拔信息
		// criteria.setBearingRequired(false);//不要求方位信息
		// criteria.setCostAllowed(true);//是否允许付费
		// criteria.setPowerRequirement(Criteria.POWER_LOW);//对电量的要求

		String proveder = lm.getBestProvider(criteria, true);   //得到最合适的定位方式
		lm.requestLocationUpdates(proveder, 0, 0, listener);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 取消监听位置服务
		lm.removeUpdates(listener);
		listener = null;
		
	}
	
	
	class MyLocationListener implements LocationListener {

		/**
		 * 当位置改变的时候回调
		 */
		@Override
		public void onLocationChanged(Location location) {
			String longitude = "j:" + location.getLongitude() + "\n";   // 经度
			String latitude = "w:" + location.getLatitude() + "\n";   //纬度
			String accuracy = "a" + location.getAccuracy() + "\n";  //精确度
			

			// 把标准的GPS坐标转换成火星坐标
			InputStream is;
			try {
				is = getAssets().open("axisoffset.dat");
				ModifyOffset offset = ModifyOffset.getInstance(is);
				PointDouble double1 = offset.s2c(new PointDouble(location
						.getLongitude(), location.getLatitude()));
				longitude ="j:" + offset.X+ "\n";
				latitude =  "w:" +offset.Y+ "\n";
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// 发短信给安全号码
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("lastlocation", longitude + latitude + accuracy);
			editor.commit();

		}

		/**
		 * 当状态发生改变的时候回调 开启--关闭 ；关闭--开启
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		/**
		 * 某一个位置提供者可以使用了
		 */
		@Override
		public void onProviderEnabled(String provider) {

		}

		/**
		 * 某一个位置提供者不可以使用了
		 */
		@Override
		public void onProviderDisabled(String provider) {

		}		
	}
	
	
}
