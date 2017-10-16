package com.zxz.dao;

import java.util.List;
import java.util.Map;

import com.zxz.domain.SumScore;

public class SumScoreDao extends BaseDao<SumScore>{
	
	static SumScoreDao sumScoreDao;
	static int id=0;
	
	private SumScoreDao() {
	}
	
	public static SumScoreDao getInstance(){
		if(sumScoreDao!=null){
			return sumScoreDao;
		}else{
			synchronized (SumScoreDao.class) {
				sumScoreDao = new SumScoreDao();
				return sumScoreDao;
			}
		}
	}
	
	
	
	/**
	 * @param SumScore
	 * @return
	 */
	public List<SumScore> findSumScore(Map map) {
		return super.queryForList("SumScore.query", map);
	}
	
	
	/**保存用户
	 * @param SumScore
	 * @return
	 */
	public int saveSumScore(SumScore sumScore) {
		int id = super.insert("SumScore.save", sumScore);
		return id;
	}
	
	
	/**修改用户
	 * @param SumScore
	 * @return
	 */
	public int modifySumScore(SumScore sumScore) {
		int count = 0;
		count = super.update("SumScore.modify", sumScore);
		return count;
	}
	
	public List<SumScore> queryList(SumScore sumScore) {
		return super.queryForList("SumScore.queryList", sumScore);
	}
}
