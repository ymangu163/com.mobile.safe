package com.mobile.safe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/*
 *   http://blog.csdn.net/ruijc/article/details/6210024
 */
public class OutCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		String number = this.getResultData(); 
		if(number==null){
			number=intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER); 			
		}		
	     System.out.println("哈哈，有电话打出去了"+number); 

	}

}
