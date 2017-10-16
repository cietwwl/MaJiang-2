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
	
	/**�����û�
	 * @param user
	 * @return
	 */
	public int saveUserScore(UserScore userScore) {
		int id = super.insert("UserScore.save", userScore);
		return id;
	}
	
	
	/**��ѯ�û��ĳɼ� 
	 * @param map
	 * @return
	 */
	public List<UserScore> findUserScore(Map<String, Object> map){
		
		List<UserScore> list = super.queryForList("UserScore.queryForMap", map);
		
		return list;
		
	}
	
	/**��ѯ�û��ĳɼ� 
	 * @param map
	 * @return
	 */
	public List<Map<String,Object>> findUserSumScore(Map<String, Object> map){
		List<Map<String,Object>> queryForList = super.queryForList("UserScore.querySumForMap", map);
		return queryForList;
	}
	
	
	/**�õ��û���ǰ����Ϸ�еķ���
	 * @param user
	 * @return
	 */
	public int selectUserScoreByCurrentRoomNumber(UserScore userScore) {
		int score = (int) super.queryForObject("UserScore.selectUserScoreByCurrentRoomNumber", userScore);
		return score;
	}
	
}
