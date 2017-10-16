package com.zxz.utils;

import java.util.List;

import com.zxz.dao.UserScoreDao;
import com.zxz.dao.VedioDao;
import com.zxz.domain.ScoreUser;
import com.zxz.domain.User;
import com.zxz.domain.UserScore;
import com.zxz.domain.Vedio;

public class RecordScoreThread implements Runnable{
	
	private List<ScoreUser> userList;
	private StringBuffer luxiang;
	static UserScoreDao userScoreDao = UserScoreDao.getInstance();
	
	public RecordScoreThread(List<ScoreUser> userList,StringBuffer luxiang) {
		this.userList = userList;
		this.luxiang = luxiang;
	}
	
	@Override
	public void run() {
		java.util.Date createDate = new java.util.Date();
		VedioDao vedioDao = VedioDao.getInstance();
		Vedio vedio = new Vedio();
		vedio.setRecord(luxiang.toString());
		vedioDao.saveVedio(vedio);
		for(int i=0;i<userList.size();i++){
			ScoreUser user = userList.get(i);
			int score = user.getScore();
//			user.setCurrentScore(user.getCurrentScore()+score);
			int userid = user.getId();//用户的ID
			int roomNumber = user.getRoomNumber();
			int currentGame = user.getCurrentGame();
			UserScore userScore = new UserScore(userid, roomNumber,currentGame,score,createDate,vedio.getId());
			userScoreDao.saveUserScore(userScore);
		}
	}

}
