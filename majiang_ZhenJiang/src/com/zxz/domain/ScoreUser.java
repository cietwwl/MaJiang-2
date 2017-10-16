package com.zxz.domain;

public class ScoreUser {

	
	private int id;
	private int currentGame;
	private int score;
	private int roomNumber;
	
	
	public ScoreUser(int id, int currentGame, int score, int roomNumber) {
		super();
		this.id = id;
		this.currentGame = currentGame;
		this.score = score;
		this.roomNumber = roomNumber;
	}
	
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getCurrentGame() {
		return currentGame;
	}
	public void setCurrentGame(int currentGame) {
		this.currentGame = currentGame;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getRoomNumber() {
		return roomNumber;
	}
	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}


	@Override
	public String toString() {
		return "ScoreUser [id=" + id + ", currentGame=" + currentGame + ", score=" + score + ", roomNumber="
				+ roomNumber + "]";
	}
	
	
	
	
}
