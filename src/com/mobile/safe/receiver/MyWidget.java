package com.mobile.safe.receiver;

import com.mobile.safe.service.UpdateWidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/*
 * 创建Widget的步骤：
① 创建一个类继承 AppWidgetProvider.
② 因为AppWidgetProvider 是一个广播接收者，要在清单文件中注册这个类.
③ 在res/下新建一个 xml文件夹，并创建一个<appwidget-provider>为根节点的xml配置文件.
 */

public class MyWidget extends AppWidgetProvider {
	
	// 创建和移除时都调用,时间片到了也会调用 
	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("MyWidget  onReceive.");
		super.onReceive(context, intent);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		System.out.println("MyWidget  onUpdate.");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	// 创建第一个时调用 
	@Override
	public void onEnabled(Context context) {
		Intent i = new Intent(context,UpdateWidgetService.class);
		context.startService(i);
		System.out.println("MyWidget  onEnabled.");
		super.onEnabled(context);
	}
	// 移除最后一个时调用 
	@Override
	public void onDisabled(Context context) {
		Intent intent = new Intent(context,UpdateWidgetService.class);
		context.stopService(intent);
		System.out.println("MyWidget  onDisabled.");
		super.onDisabled(context);
	}
}
