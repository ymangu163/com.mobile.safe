package com.mobile.safe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {
	
	/**
	 * 校验某个服务是否还活着 
	 * serviceName :传进来的服务的名称
	 */
	public static boolean isServiceRunning(Context context,String serviceName){
		//校验服务是否还活着
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		/*
		 *  如果服务小于100个，就会问返回；如果服务大于100个，就只返回100个.
		 */
		List<RunningServiceInfo> infos =  am.getRunningServices(100);
		for(RunningServiceInfo info : infos){
			String name = info.service.getClassName();
			if(serviceName.equals(name)){
				return true;
			}
		}
		return false;
	
	}

}
