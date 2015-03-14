package com.mobile.safe.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.mobile.safe.bean.AppInfo;

/**
 * 业务方法，提供手机里面安装的所有的应用程序信息
 */
public class AppInfoProvider {
	
	/**
	 * 获取所有的安装的应用程序信息。
	 * @param context 上下文
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context){
		PackageManager pm = context.getPackageManager();//获取包的管理器
		List<AppInfo> appInfos = new ArrayList<AppInfo>();//存放所有应用程序信息集合
		AppInfo appInfo = null;
		// PackageInfo 相当于拿到了一个应用的AndroidManifest 清单文件
		List<PackageInfo> PackInfos = pm.getInstalledPackages(0);//所有的安装在系统上的应用程序包信息 0为不关心特殊的标记
		
		for (PackageInfo packageInfo : PackInfos) {
			String packName = packageInfo.packageName;//获取包名
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);//获取应用程序图标
			String name = packageInfo.applicationInfo.loadLabel(pm).toString();//获取应用程序名称
			int flag = packageInfo.applicationInfo.flags;
			int uid=packageInfo.applicationInfo.uid;
//			File rcvFile=new File("/proc/uid_stat/"+uid+"tcp_rev");
//			File sndFile=new File("/proc/uid_stat/"+uid+"tcp_snd");
			
			appInfo = new AppInfo();
			appInfo.setUid(uid);
			
			if((flag&ApplicationInfo.FLAG_SYSTEM) == 0){//用户程序
				appInfo.setUserApp(true);
			}else{ //系统程序
				appInfo.setUserApp(false);
			}
			if((flag&ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0){//手机内存
				appInfo.setInRom(true);
			}else{
				appInfo.setInRom(false);
			}
			appInfo.setPackname(packName);
			appInfo.setIcon(icon);
			appInfo.setName(name);
			appInfos.add(appInfo);
			
		}
		return appInfos;
	}
	

}
