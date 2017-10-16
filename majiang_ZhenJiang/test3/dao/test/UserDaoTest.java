package dao.test;

import java.util.Date;

import org.junit.Test;

import com.zxz.dao.UserDao;
import com.zxz.domain.User;
import com.zxz.domain.UserScore;

public class UserDaoTest {

	UserDao userDao = UserDao.getInstance();
	
	@Test
	public void testSave(){
		User user= new User();
		user.setUserName("ут");
		user.setPassword("dddd");
		user.setRoomId(1000+"d");
		user.setRoomCard(50);
//		user.setId(1);
		userDao.saveUser(user);
		System.out.println(user.getId());
	}
	
}
