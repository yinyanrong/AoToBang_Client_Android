package com.aotobang.app.entity;


public class ImgMsgBody extends FileMsgBody {
	private String thumbnailUrl;
	private int heigh;
	private int width;
	//是否发送原图
	private boolean sendOriginalImage;
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public int getHeigh() {
		return heigh;
	}
	public void setHeigh(int heigh) {
		this.heigh = heigh;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public boolean isSendOriginalImage() {
		return sendOriginalImage;
	}
	public void setSendOriginalImage(boolean sendOriginalImage) {
		this.sendOriginalImage = sendOriginalImage;
	}
	
}
