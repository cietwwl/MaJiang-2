package com.zxz.algorithm;

public class ChuPaiType {

	private String chupaiDirection;//���Ƶķ���
	private int chupaiType;// ���Ƶ�����  1.���� 2.����
	
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
