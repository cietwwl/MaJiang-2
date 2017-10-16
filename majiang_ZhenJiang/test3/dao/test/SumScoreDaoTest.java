package dao.test;

import org.junit.Test;

import com.zxz.dao.SumScoreDao;
import com.zxz.domain.SumScore;

public class SumScoreDaoTest {
	
	static SumScoreDao sumScoreDao = SumScoreDao.getInstance();
	
	public static void main(String[] args) {
		SumScore sumScore = new SumScore();
		sumScore.setRoomNumber("11111");
		sumScore.setZhongMaTotal(1);
		sumScoreDao.saveSumScore(sumScore);
	}
	
	@Test
	public void testSave(){
	}
	
}
