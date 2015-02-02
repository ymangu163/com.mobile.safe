package com.mobile.safe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/*
 * 自定一个TextView 一出生就有焦点
 */
public class FocusedTextView extends TextView {

	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		
	}
	
	/**
	 * 当前并没有焦点，我只是欺骗了Android系统
	 */
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		return true;
	}
	
}
