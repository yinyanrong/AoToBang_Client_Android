package com.aotobang.app.device.control;

public interface DeviceControl {
	public void setRealMode(boolean real);
	public boolean isRealMode();
	public void realControl(String data);
	public void control(String data);
	public void stopDevice();
	public void setDeviceType(int type);
	public int  getDeviceType();
}
