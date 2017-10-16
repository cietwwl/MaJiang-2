package com.zxz.domain;

import java.util.Date;

public class UserScore {

	private int id;
	private int userid;//用户ID
	private int roomid;//游戏ID
	private int currentGame;//当前的局数
	private int score;//成绩
	private int vedioid;//录像ID
	private Date createDate;//创建的时间
	
	public UserScore() {
	}
	
	public UserScore(int userid, int roomid, int currentGame, int score, Date createDate,int vedioid) {
		super();
		this.userid = userid;
		this.roomid = roomid;
		this.currentGame = currentGame;
		this.score = score;
		this.createDate = createDate;
		this.vedioid = vedioid;
	}

	public int getCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(int currentGame) {
		this.currentGame = currentGame;
	}

	public int getVedioid() {
		return vedioid;
	}

	public void setVedioid(int vedioid) {
		this.vedioid = vedioid;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getRoomid() {
		return roomid;
	}
	public void setRoomid(int roomid) {
		this.roomid = roomid;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	@Override
	public String toString() {
		return "UserScore [id=" + id + ", userid=" + userid + ", roomid=" + roomid + ", score=" + score
				+ ", createDate=" + createDate + "]";
	}
}
