package com.mobile.safe.activity;

import java.util.List;

import com.mobile.safe.R;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;

/*
 * .pid:进程id  ；应用要跑起来才是进程  
uid:用户Id  ;操作系统分配级应用程序的一个固定的编号，不再改变
/proc/uid_statuts: 包含会产生流量的应用uid
tcp_rcv: 接收的  ；实时更新，单位byte,从开机开始记录，关机就清0了
tcp_snd: 发送的

//			File rcvFile=new File("/proc/uid_stat/"+uid+"tcp_rev");
//			File sndFile=new File("/proc/uid_stat/"+uid+"tcp_snd");
 */
public class TrafficManagerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 1.获取一个包管理器
		PackageManager pm=getPackageManager();
		// 2. 遍历手机操作系统获取所有的应用程序的uid
		List<ApplicationInfo> applicationInfos=pm.getInstalledApplications(0);
		for(ApplicationInfo info:applicationInfos){
			int uid=info.uid;
			long tx=TrafficStats.getMobileTxBytes(); //上传的流量，单位byte
			long rx=TrafficStats.getMobileRxBytes();// 下载的流量
			// 方法返回-1 代表的是应用程序没有产生流量或者操作系统不支持流量统计
		}
		TrafficStats.getMobileTxBytes();  //获取手机 3g/2g网络上传的总流量 
		TrafficStats.getMobileRxBytes();// 获取手机 3g/2g下载的流量
		
		TrafficStats.getTotalRxBytes(); //手机全部网络接口 包括wifi,3g,2g
		
		setContentView(R.layout.activity_traffic_manager);
		
	}
}
