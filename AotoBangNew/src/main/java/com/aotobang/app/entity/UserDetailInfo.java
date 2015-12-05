package com.aotobang.app.entity;

public class UserDetailInfo {
	@Override
	public String toString() {
		return "UserDetailInfo [name=" + name + ", age=" + age + ", birthday=" + birthday + ", email=" + email + ", remark=" + remark + ", level=" + level + ", location=" + location + ", distance=" + distance + ", coordinate=" + coordinate + "]";
	}
	private String name;
	private String age;
	private String birthday;
	private String email;
	private String remark;
	private int level;
	private String location;
	//距离。单位米
	private long distance;
	//坐标
	private String coordinate;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public long getDistance() {
		return distance;
	}
	public void setDistance(long distance) {
		this.distance = distance;
	}
	public String getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}
	
}
