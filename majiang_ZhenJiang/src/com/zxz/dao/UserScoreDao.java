package com.zxz.dao;

import java.util.List;
import java.util.Map;

import com.zxz.domain.UserScore;

public class UserScoreDao extends BaseDao<UserScore>{

	static UserScoreDao userScoreDao;
	
	private UserScoreDao() {
	}
	
	public static UserScoreDao getInstance(){
		if(userScoreDao!=null){
			return userScoreDao;
		}else{
			synchronized (UserScoreDao.class) {
				userScoreDao = new UserScoreDao();
				return userScoreDao;
			}
		}
	}
	
	/**保存用户
	 * @param user
	 * @return
	 */
	public int saveUserScore(UserScore userScore) {
		int id = super.insert("UserScore.save", userScore);
		return id;
	}
	
	
	/**查询用户的成绩 
	 * @param map
	 * @return
	 */
	public List<UserScore> findUserScore(Map<String, Object> map){
		
		List<UserScore> list = super.queryForList("UserScore.queryForMap", map);
		
		return list;
		
	}
	
	/**查询用户的成绩 
	 * @param map
	 * @return
	 */
	public List<Map<String,Object>> findUserSumScore(Map<String, Object> map){
		List<Map<String,Object>> queryForList = super.queryForList("UserScore.querySumForMap", map);
		return queryForList;
	}
	
	
	/**得到用户当前的游戏中的分数
	 * @param user
	 * @return
	 */
	public int selectUserScoreByCurrentRoomNumber(UserScore userScore) {
		int score = (int) super.queryForObject("UserScore.selectUserScoreByCurrentRoomNumber", userScore);
		return score;
	}
	
}
