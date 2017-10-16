package com.zxz.algorithm;

import java.util.LinkedList;
import java.util.List;

public class Person {
	
	private int id;
	private String name;//昵称
	private List<Integer> paiList = new LinkedList<Integer>();//自己的牌
	private boolean isReady=false;//是否已经准备
	private String direction;//座次表  0 东 、1 北  、2 西  3、南
	
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isReady() {
		return isReady;
	}
	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Integer> getPaiList() {
		return paiList;
	}
	public void setPaiList(List<Integer> paiList) {
		this.paiList = paiList;
	}
	public Person(String name) {
		super();
		this.name = name;
	}
	public Person() {
	}
	public Person(int id) {
		super();
		this.id = id;
	}
	public Person(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	
}
