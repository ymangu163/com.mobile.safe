package com.mobile.safe.bean;

public class HomeItemBean {

	private String str;
	private int imageId;
	
	public HomeItemBean(String str,int id){
		this.str=str;
		this.imageId=id;
	}
	
	public String getStr() {
		return str;
	}
	public void setStr(String str) {
		this.str = str;
	}
	public int getImageId() {
		return imageId;
	}
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	
	
	
}
