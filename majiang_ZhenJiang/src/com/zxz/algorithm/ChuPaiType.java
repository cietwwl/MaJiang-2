package com.zxz.algorithm;

public class ChuPaiType {

	private String chupaiDirection;//出牌的方向
	private int chupaiType;// 出牌的类型  1.碰牌 2.杠牌
	
	public String getChupaiDirection() {
		return chupaiDirection;
	}
	public void setChupaiDirection(String chupaiDirection) {
		this.chupaiDirection = chupaiDirection;
	}
	public int getChupaiType() {
		return chupaiType;
	}
	public void setChupaiType(int chupaiType) {
		this.chupaiType = chupaiType;
	}
	public ChuPaiType(String chupaiDirection, int chupaiType) {
		super();
		this.chupaiDirection = chupaiDirection;
		this.chupaiType = chupaiType;
	}
	
}
