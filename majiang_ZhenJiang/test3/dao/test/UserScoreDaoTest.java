package dao.test;

import java.util.Date;

import org.junit.Test;

import com.zxz.dao.UserScoreDao;
import com.zxz.domain.UserScore;

public class UserScoreDaoTest {

	
	
	public static void main(String[] args) {
		long currentTimeMillis = System.currentTimeMillis();
		for(int i=0;i<1000;i++){
			UserScoreDao userScoreDao = UserScoreDao.getInstance();
            UserScore userScore = new UserScore();
			//			Date createDate = new Date();
//			UserScore userScore = new UserScore(1, 10, 100,99, createDate,1);
//			userScoreDao.saveUserScore(userScore);
			userScore .setUserid(1);
			userScore.setRoomid(10);
			int number = userScoreDao.selectUserScoreByCurrentRoomNumber(userScore);
			System.out.println(i+" "+number);
		}
		long currentTimeMillis2 = System.currentTimeMillis();
		System.out.println(currentTimeMillis2-currentTimeMillis);
	}
	
}
