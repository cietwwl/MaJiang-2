package com.zxz.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.zxz.controller.StartServer;
import com.zxz.domain.User;
import com.zxz.redis.RedisUtil;
import com.zxz.utils.Constant;

public class UserDao extends BaseDao<User>{
	
	private static Logger logger = Logger.getLogger(UserDao.class);
	static UserDao userDao;
	static int id=0;
	
	private UserDao() {
	}
	
	public static UserDao getInstance(){
		if(userDao!=null){
			return userDao;
		}else{
			synchronized (UserDao.class) {
				userDao = new UserDao();
				return userDao;
			}
		}
	}
	
	
	
	public User findUser(User user) {
		User user2 = new User();
		if(id == 0){
			user2.setId(id);
			user2.setUserName("赵");
			//user2.setAuto(true);
			id ++ ;
		}else if(id == 1){
			user2.setId(id);
			user2.setUserName("钱");
			//user2.setAuto(true);
			id ++ ;
		}else if(id == 2){
			user2.setId(id);
			user2.setUserName("孙");
			//user2.setAuto(true);
			id ++ ;
		}else if(id == 3){
			user2.setId(id);
			user2.setUserName("李");
			//user2.setAuto(true);
			id ++ ;
		}else{
			user2.setId(id);
			user2.setUserName("游客"+id);
			//user2.setAuto(true);
			id ++ ;
		}
		return user2;
//		return super.queryForObject("User.query", user);
	}

	
	/**线上登录
	 * @param user
	 * @return
	 */
	public User findUser2(User user) {
		return (User) super.queryForObject("User.query", user);
	}
	
	
	/**
	 * @param user
	 * @return
	 */
	public User findUserByUserId(int id){
		return (User)super.queryForObject("User.selectByPrimaryKey", id);
	}
	
	
	/**保存用户
	 * @param user
	 * @return
	 */
	public int saveUser(User user) {
		int id = 0;
		try {
			id = super.insert("User.save", user);
		} catch (Exception e) {
			logger.info("注册的时候出错"+user.getNickName()+e);
			user.setNickName("");//昵称里肯能有特殊字符
			id = super.insert("User.save", user);
		}
		return id;
	}
	
	
	/**修改用户
	 * @param user
	 * @return
	 */
	public int modifyUser(User user) {
		int count = 0;
		count = super.update("User.modify", user);
		return count;
	}
	
	
	
	
	/**redis中修改用户的房间号
	 * @param userId
	 * @param roomNumber
	 * @return
	 */
	public int modifyUser(int userId,String roomNumber) {
		String setKey = RedisUtil.setKey("usRoomId"+userId, roomNumber,Constant.REDIS_DB);
		if("OK".equals(setKey)){
			return 1;
		}
		return 0;
	}
	
	
	
	
	
	public static void main(String[] args) {
		UserDao userDao = new UserDao();
		userDao.findUserByUserId(10020);
	}
	
	
	
	@Test
	public void testModifyUser()
	{
	
	}
	
	public List<User> queryList(User user) {
		return super.queryForList("User.queryList", user);
	}

	
	
	@Test
	public void testSaveUser()
	{
		User u= new User();
		u.setUserName("hahah");
		u.setPassword("dddd");
		//saveUser(u);
//		User findUser = findUser(u);
		u.setRoomCard(10000);
		List<User> queryList = queryList(u);
		System.out.println(queryList);
	}
	
}
