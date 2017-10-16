package com.zxz.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.mysql.jdbc.StringUtils;
import com.zxz.config.utils.Config;
import com.zxz.controller.GameManager;
import com.zxz.controller.RoomManager;
import com.zxz.dao.OneRoomDao;
import com.zxz.dao.UserDao;
import com.zxz.dao.UserScoreDao;
import com.zxz.domain.Game;
import com.zxz.domain.OneRoom;
import com.zxz.domain.User;
import com.zxz.domain.UserScore;
import com.zxz.redis.RedisUtil;
import com.zxz.utils.CardsMap;
import com.zxz.utils.Constant;
import com.zxz.utils.CountDownThread;
import com.zxz.utils.EmojiUtil;
import com.zxz.utils.HuPai;
import com.zxz.utils.MathUtil;
import com.zxz.utils.NotifyTool;
import com.zxz.utils.RoomNumberUtil;
import com.zxz.utils.WeiXinUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class UserService extends BasePlay implements PlayOfHongZhong, Constant {

	private static Logger logger = Logger.getLogger(UserService.class);

	OneRoomDao roomDao = OneRoomDao.getInstance();
	static UserDao userDao = UserDao.getInstance();
	PlayGameService playGameService = new PlayGameService();
	DateServiceImpl dateService = new DateServiceImpl();// 统计相关

	/**
	 * 登录
	 * 
	 * @param jsonObject
	 * @param session
	 * @return
	 */
	public boolean login(JSONObject jsonObject, ChannelHandlerContext session) {
		boolean hasUnionid = jsonObject.has("unionid");// 是否含有hasUnionid
		boolean loginResult = false;
		boolean addLoginUserData = true;// 是否添加用户数
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		if (user != null) {// 已经登录
			addLoginUserData = false;
			String roomId = user.getRoomId();
			boolean result = loginWithUnionid(jsonObject, session, roomId);
			doOtherThings(session);
			return result;
		}
		if (hasUnionid) {
			loginResult = loginWithUnionid(jsonObject, session, null);
		} else {
			loginResult = loginWithCode(jsonObject, session);
		}
		// 用户登录的时候添加用户数
		if (loginResult) {
			// 1.添加用户数
			if (addLoginUserData) {
				dateService.addLoginUser();
			}
			// 2.检测用户是否在房间里面,如果在房间里面,下载用户的信息,并替换用户的session
			doOtherThings(session);
		}
		return loginResult;
	}

	/**
	 * @param user
	 * @return
	 */
	public JSONObject createLoginJsonObjectWithUser(User user) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("method", "login");
		jsonObject.put("unionid", user.getUnionid());
		return jsonObject;
	}

	/**
	 * 检测用户是否在房间里面,如果在房间里面,通知用户进入房间,如果没有
	 */
	public void doOtherThings(ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		SettingService settingService = new SettingService();
		boolean isInRoom = settingService.cheekUserInRoom(user);
		if (isInRoom) {
			downGameInfo(session);
		}
	}

	private void downGameInfo(ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		JSONObject roomInfo = getRoomInfo(user);
		if (roomInfo == null) {// 游戏没开始，通知用户进入房间
			// 得到房间里的用户信息
			OneRoom oneRoom = RoomManager.getRoomWithRoomId(user.getRoomId());
			JSONObject outJsonObject = new JSONObject();
			outJsonObject.put("method", "enterRoom");
			outJsonObject.put("code", "success");
			getRoomInfo(outJsonObject, oneRoom);
			session.write(outJsonObject.toString());
			replaceUserIoSession(user, oneRoom);
			return;
		}
		nofiyUserRoomInfo(roomInfo, user);
		// Game game = GameManager.getGameWithRoomNumber(user.getRoomId());
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(user.getRoomId());
		replaceUserIoSession(user, oneRoom);
	}

	/**
	 * 用unionid登录,说明用户已经注册过
	 * 
	 * @param jsonObject
	 * @param session
	 * @param roomId
	 * @return
	 */
	private boolean loginWithUnionid(JSONObject jsonObject, ChannelHandlerContext session, String roomId) {
		String unionid = jsonObject.getString("unionid");
		switch (unionid) {
		case "obhqFxAmLRLMv1njQnWFsl_npjPw":// 顾双
		case "obhqFxIRabSd9B2qhT_ThzsXMU58":// 周益雄
		case "obhqFxCzFVH5UkKJRIH-AqePEnZ8":// 张森
		case "obhqFxHtB3emb506Q-FsZwW4_Py4":// 尤海涛s
			User user = new User();
			user.setUnionid(unionid);
			;
			user = userDao.findUser2(user);
			testLogin(unionid, session, roomId);
			return true;
		}
		User user = new User();
		user.setUnionid(unionid);
		user = userDao.findUser2(user);
		if (user != null) {// 用户不存在
			String refreshToken = user.getRefreshToken();
			try {
				String accesstoekn = WeiXinUtil.getAccessTokenWithRefreshToken(refreshToken);
				JSONObject userInfo = WeiXinUtil.getUserInfo(accesstoekn, user.getOpenid());
				setUserWithUserInfoJson(userInfo, user);// 封装用户的信息
				if (!StringUtils.isNullOrEmpty(roomId)) {
					user.setRoomId(roomId);
				}
				notifyUserLoginSuccess(user, session, false);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				notifyUserLoginFail(session, "errorRefreshToken", "微信refreshToken过期请重新授权");
				logger.fatal("微信refreshToken过期");
				return false;
			}
		} else {
			notifyUserLoginFail(session, "errorUnionId", "unionId不存在");
			return false;
		}

	}

	private void testLogin(String unionid, ChannelHandlerContext session, String roomId) {
		User user = new User();
		user.setUnionid(unionid);
		;
		user = userDao.findUser2(user);
		if (user != null) {
			try {

				if (!StringUtils.isNullOrEmpty(roomId)) {
					user.setRoomId(roomId);
				}

				JSONObject userInfo = new JSONObject();
				user.setHeadimgurl(
						"http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1212/06/c1/16396010_1354784049718.jpg");
				userInfo.put("userId", user.getId());
				userInfo.put("userName", user.getNickName());
				// userInfo.put("headimgurl","http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1212/06/c1/16396010_1354784049718.jpg");
				userInfo.put("roomCard", user.getRoomCard());// 剩余的房卡数
				userInfo.put("unionid", user.getUnionid());// 唯一的unionid
				userInfo.put("nickname", user.getUserName());// 唯一的unionid
				notifyUserLoginSuccess(user, session, false);
			} catch (Exception e) {
				e.printStackTrace();
				notifyUserLoginFail(session, "errorRefreshToken", "微信refreshToken过期请重新授权");
				logger.fatal("微信refreshToken过期");
			}
		} else {
			notifyUserLoginFail(session, "errorUnionId", "unionId不存在");
		}
	}

	/**
	 * 用code登录
	 * 
	 * @param jsonObject
	 * @param session
	 * @return
	 */
	private boolean loginWithCode(JSONObject jsonObject, ChannelHandlerContext session) {
		String code = jsonObject.getString("code");
		try {
			JSONObject accessTokenJson = WeiXinUtil.getAccessTokenJson(code);
			String refreshToken = accessTokenJson.getString("refresh_token");
			String openid = accessTokenJson.getString("openid");
			String accesstoken = accessTokenJson.getString("access_token");
			JSONObject userInfoJson = WeiXinUtil.getUserInfo(accesstoken, openid);
			String openId = userInfoJson.getString("openid");
			User findUser = new User();
			findUser.setOpenid(openId);
			User user = userDao.findUser2(findUser);
			findUser.setRefreshToken(refreshToken);
			setUserWithUserInfoJson(userInfoJson, findUser);// 封装用户的信息
			boolean isFirstLogin = true;
			if (user == null) {// 没有注册
				registUser(userInfoJson, findUser);
			} else {// 已经注册获取用户的房卡数量
					// 修改用户的refreshToken
				User modifyUser = new User();
				modifyUser.setId(user.getId());
				modifyUser.setRefreshToken(refreshToken);
				userDao.modifyUser(modifyUser);
				findUser.setId(user.getId());
				findUser.setRoomCard(user.getRoomCard());
				findUser.setRoomId(user.getRoomId());
				isFirstLogin = false;// 不是第一次登陆
			}
			session.channel().attr(AttributeKey.<User>valueOf("user")).set(findUser);
			notifyUserLoginSuccess(findUser, session, isFirstLogin);
		} catch (Exception e) {
			logger.info("微信登录失败");
			logger.fatal(e);
			notifyUserLoginFail(session, "errorCode", "CODE传递不正确");
			return false;
		}
		return true;
	}

	/**
	 * 通知用户登录失败
	 * 
	 * @param session
	 */
	private void notifyUserLoginFail(ChannelHandlerContext session, String errorCode, String discription) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("login", false);
		outJsonObject.put("method", "login");
		outJsonObject.put("errorCode", errorCode);
		outJsonObject.put(UserService.discription, discription);
		session.write(outJsonObject.toString());
	}

	/**
	 * 通知用户登录成功
	 * 
	 * @param findUser
	 * @param session
	 * @param isFirstLogin
	 *            是否第一次注册
	 */
	private void notifyUserLoginSuccess(User findUser, ChannelHandlerContext session, boolean isFirstLogin) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("method", "login");
		findUser.setIoSession(session);
		session.channel().attr(AttributeKey.<User>valueOf("user")).set(findUser);
		outJsonObject.put("login", true);
		outJsonObject.put("userId", findUser.getId());
		outJsonObject.put("userName", findUser.getNickName());
		outJsonObject.put("headimgurl", findUser.getHeadimgurl());
		outJsonObject.put("roomCard", findUser.getRoomCard());// 剩余的房卡数
		outJsonObject.put("unionid", findUser.getUnionid());// 唯一的unionid
		outJsonObject.put("isFirstLogin", isFirstLogin);// 是否第一次登陆，也就是注册
		outJsonObject.put("sex", findUser.getSex());// 性别
		outJsonObject.put("description", "登录成功!");
		String remoteAddress = session.channel().remoteAddress().toString();
		outJsonObject.put("ip", remoteAddress.replaceAll("/", ""));// ip地址
		session.write(outJsonObject.toString());
	}

	/**
	 * 注册用户
	 * 
	 * @param userInfoJson
	 * @param findUser
	 */
	private void registUser(JSONObject userInfoJson, User findUser) {
		// findUser.setNickName(""); //过滤掉特殊字符
		// 设置用户默认的房卡数量
		findUser.setRoomCard(DEFAULT_USER_REGIST_ROOMCARD);
		findUser.setCreateDate(new Date());
		userDao.saveUser(findUser);
	}

	/**
	 * 封装用户的信息
	 * 
	 * @param userInfoJson
	 * @param findUser
	 */
	private void setUserWithUserInfoJson(JSONObject userInfoJson, User findUser) {
		logger.info("userInfoJson:" + userInfoJson);
		String nickName = userInfoJson.getString("nickname");// 昵称
		String unionid = userInfoJson.getString("unionid");
		String city = userInfoJson.getString("city");// 城市
		String headimgurl = userInfoJson.getString("headimgurl");// 头像
		String province = userInfoJson.getString("province");// 省份
		int sex = userInfoJson.getInt("sex");// 性别
		// String refreshToken = userInfoJson.getString("refresh_token");
		findUser.setCity(city);
		findUser.setHeadimgurl(headimgurl);
		findUser.setUnionid(unionid);
		// findUser.setNickName(nickName);
		findUser.setNickName(EmojiUtil.resolveToByteFromEmoji(nickName)); // 过滤掉特殊字符
		findUser.setProvince(province);
		findUser.setSex(sex + "");
		// findUser.setRefreshToken(refreshToken);
	}

	private boolean login1(JSONObject jsonObject, ChannelHandlerContext session) {
		String userName = jsonObject.getString("userName");
		String password = jsonObject.getString("password");
		User user = new User(userName, password);
		User findUser;
		if (!userName.equals("")) {// 线上
			findUser = userDao.findUser2(user);
		} else {
			findUser = userDao.findUser(user);
		}
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("method", "login");
		if (findUser != null) {
			// SocketManager.addIoSession(user.getId()+"", session);
			findUser.setIoSession(session);
			session.channel().attr(AttributeKey.<User>valueOf("user")).set(findUser);
			outJsonObject.put("login", true);
			outJsonObject.put("userId", findUser.getId());
			outJsonObject.put("userName", findUser.getUserName());
			outJsonObject.put("description", "登录成功!");
			System.out.println(session.getClass());
			session.write(outJsonObject.toString());
			// 检测用户是否掉线
			// new UserDroppedService(session);
			return true;
		} else {
			outJsonObject.put("login", false);
			outJsonObject.put("description", "登录失败,用户名或密码错误!");
			session.write(outJsonObject.toString());
			return false;
		}
	}

	/**
	 * 创建房间
	 * 
	 * @param jsonObject
	 * @param session
	 */
	public void createRoom(JSONObject jsonObject, ChannelHandlerContext session) {
		int userid = jsonObject.getInt("ui");
		Map<String, String> hashMap = RedisUtil.getHashMap("uid"+userid,REDIS_DB);
		String userInfo = hashMap.get("baseInfo");
		User user = new User();
		if (userInfo != null) {
			User infoUser = getUserFromUserInfo(userInfo, userid + "");
			if (infoUser != null) {
				user = infoUser;
			} else {
				user.setId(userid);
				user = userDao.findUser2(user);
			}
		} else {
			user.setId(userid);
			user = userDao.findUser2(user);
		}
		if (user != null) {
			int total = jsonObject.getInt("total");// 局数
			boolean isCanCreateRoom = checkUserCanCreateRoom(user, total);
			if (isCanCreateRoom) {
				// 用unionid从微信获取用户的信息
//				changeUserHeadimageWithUserUnionid(user);
				User u = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
				if (u == null) {
					session.channel().attr(AttributeKey.<User>valueOf("user")).set(user);
					user.setIoSession(session);
					dateService.addLoginUser();
				}
				realCreateRoom(jsonObject, session);
			}
		}
	}

	/**
	 * 从userInfo中得到用户
	 * 
	 * @param userInfo
	 * @return
	 */
	private User getUserFromUserInfo(String userInfo, String unionid) {
		try {
			JSONObject jsonObject = new JSONObject(userInfo);
			User user = new User();
			user.setId(jsonObject.getInt("userId"));
			user.setUnionid(jsonObject.getString("unionid"));
			user.setUserName(jsonObject.getString("userName"));
			user.setNickName(jsonObject.getString("userName"));
			user.setHeadimgurl(jsonObject.getString("headimgurl"));
			user.setRoomCard(jsonObject.getInt("roomCard"));
			user.setSex(jsonObject.getString("sex"));
			user.setRefreshToken(jsonObject.getString("refreshToken"));
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			RedisUtil.delKey(unionid,1);
		}
		return null;
	}

	/**
	 * 用unionid从微信获取用户的信息
	 * 
	 * @param user
	 */
	private void changeUserHeadimageWithUserUnionid(User user) {
		// String unionid = user.getUnionid();
		// switch (unionid) {
		// case "obhqFxAmLRLMv1njQnWFsl_npjPw"://顾双
		// case "obhqFxIRabSd9B2qhT_ThzsXMU58"://周益雄
		// case "obhqFxCzFVH5UkKJRIH-AqePEnZ8"://张森
		// case "obhqFxHtB3emb506Q-FsZwW4_Py4"://尤海涛s
		// return;
		// }

		String refreshToken = user.getRefreshToken();
		try {
			String accesstoekn = WeiXinUtil.getAccessTokenWithRefreshToken(refreshToken);
			JSONObject userInfo = WeiXinUtil.getUserInfo(accesstoekn, user.getOpenid());
			setUserWithUserInfoJson(userInfo, user);// 封装用户的信息
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal("微信refreshToken过期");
		}
	}

	/**
	 * 检测用户是否可以创建房间
	 * 
	 * @param session
	 * @return true 可以 false 不可以
	 */
	private boolean checkUserCanCreateRoom(User user, int total) {
		int id = user.getId();
		User findUser = userDao.findUserByUserId(id);
		int roomCard = findUser.getRoomCard();// 房卡的数量
		user.setRoomCard(roomCard);
		int consumeCardNum = 1;
		if (total >= 16) {
			consumeCardNum = 2;
		}
		if (roomCard >= consumeCardNum) {
			return true;
		}
		return false;
	}

	/**
	 * 通知用户不可以创建房间
	 * 
	 * @param session
	 */
	private void notifyUserCanNotCreateRoom(ChannelHandlerContext session) {
		JSONObject outJSONObject = new JSONObject();
		outJSONObject.put(code, error);
		outJSONObject.put(method, "createRoom");
		outJSONObject.put(discription, "房卡数量不足");
		session.write(outJSONObject.toString());
	}

	/**
	 * 检测用户是否可以创建房间
	 * 
	 * @param session
	 * @return true 可以 false 不可以
	 */
	private boolean checkUserCanCreateRoom(JSONObject jsonObject, User user) {

		int total = jsonObject.getInt("total");// 局数
		int id = user.getId();
		User findUser = userDao.findUserByUserId(id);
		int roomCard = findUser.getRoomCard();// 房卡的数量
		user.setRoomCard(roomCard);
		int consumeCardNum = 1;
		if (total >= 16) {
			consumeCardNum = 2;
		}
		if (roomCard >= consumeCardNum) {
			return true;
		}
		return false;
	}

	/**
	 * 在进行验证完之后,用户可以创建房间
	 * 
	 * @param jsonObject
	 * @param session
	 */
	private void realCreateRoom(JSONObject jsonObject, ChannelHandlerContext session) {
		OneRoom room = setRoomAttribute(jsonObject);
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		room.setCreateUserId(user.getId());// 创建人的ID,房主
		room.setInvertal(Config.getConfig().getInterval());
		int roomId = RoomNumberUtil.getOneRoomNumber();
		room.setId(roomId);
//		roomDao.saveRoom(room);
		room.setRoomNumber(roomId);// 设置房间号
		room.setCreateUser(user);// 创建房间的人
		user.setDirection("east");
//		user.setBanker(true);// 房主 庄家
		user.setRoomId(roomId + "");
		user.setCurrentGame(0);
		user.setAuto(false);// 不是自动准备
		user.setFangZhu(true);
		user.setCurrentScore(room.getZhama());
		Set<String> directionSet = room.getDirectionSet();
		directionSet.add("east");
		room.addUser(user);
		RoomManager.addRoomMap(roomId + "", room);
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("method", "createRoom");
		outJsonObject.put("isFz", user.isFangZhu());// 是否是房主
		outJsonObject.put(code, true);// 创建成功
		outJsonObject.put("roomId", roomId);
		outJsonObject.put(direction, user.getDirection());
		outJsonObject.put("playerScoreByAdd", user.getCurrentScore());
		outJsonObject.put("description", "创建房间成功!");
		session.write(outJsonObject.toString());
		RedisUtil.setKey("usRoomId"+user.getId(), roomId+"", REDIS_DB);
		Map<String, String> hashMap = RedisUtil.getHashMap("uid"+user.getId(),REDIS_DB);
		hashMap.put("roomId", roomId+"");
		RedisUtil.setHashMap("uid"+user.getId(), hashMap, REDIS_DB,TIME_TO_USER_ROOM);
		autoDisbandRoom(room, user);
		putRoomInfoToRedis(roomId);
	}

	private OneRoom setRoomAttribute(JSONObject jsonObject) {
		int chouma = jsonObject.getInt("chouma");// 筹码数
		int total = jsonObject.getInt("total");// 局数
		int end = jsonObject.getInt("end");// 几家光
		int wanf = jsonObject.getInt("wanf");// 玩法
		int chong = jsonObject.getInt("chong");// 玩法
		int auto = 0;
		if (jsonObject.has("auto")) {
			auto = jsonObject.getInt("auto");
		}
		OneRoom room = new OneRoom();
		room.setCreateDate(new Date());// 房间创建时间
		room.setTotal(total);
		room.setZhama(chouma);
		room.setAuto(auto);
		room.setEnd(end);
		room.setWanFa(wanf);
		room.setChong(chong);
		return room;
	}



	/**
	 * 把房间信息放入redis中
	 * 
	 * @param roomId
	 */
	private void putRoomInfoToRedis(int roomId) {
		// 将房间的地址放入redis中
		JSONObject roomJsonObject = new JSONObject();
		Config config = Config.getConfig();
		roomJsonObject.put("bestServer", config.getLocalIp());
		roomJsonObject.put("port", config.getPort());
		roomJsonObject.put("localRPCPort", config.getRPcPort());
		RedisUtil.setKey("zjRoomId"+roomId, roomJsonObject.toString(),REDIS_DB);
	}

	/**
	 * 如果10分钟游戏开没有开始则自动解散房间
	 * 
	 * @param game
	 */
	private static void autoDisbandRoom(final OneRoom room, final User user) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (room != null) {
					boolean disband = room.isDisband();// 并且房间没有解散
					if (!room.isPay() && !disband) {// 房间还没有完，并且还没有解散
						int roomNumber = room.getRoomNumber();
						realDisbandRoom(user, roomNumber, room);
						logger.info("房间" + TIME_TO_DISBAND_ROOM + "s未使用,自动解散房间" + roomNumber);
					}
				}
			}
		}, TIME_TO_DISBAND_ROOM);
	}

	/**
	 * 进入房间
	 * 
	 * @param jsonObject
	 * @param session
	 */
	public void enterRoom(JSONObject jsonObject, ChannelHandlerContext session) {
		enterRoomWithUnionid(jsonObject, session);
		// User user = (User) session.getAttribute("user");
		// boolean isUserInRoom = checkUserIsInRoom(user);
		// if(!isUserInRoom){//用户没有在房间里
		// //realEnterRoom(jsonObject, session);//可以进入房间
		// }
	}

	/**
	 * 得到房间里的信息，并且替换掉死去的session,并且通知玩家当前的游戏信息
	 * 
	 * @param user
	 */
	public static JSONObject getRoomInfo(User user) {
		Game game = GameManager.getGameWithRoomNumber(user.getRoomId());
		JSONObject roomInfoJson = null;
		if (game != null) {// 游戏已经开始
			int status = game.getStatus();
			if (status == GAGME_STATUS_OF_IS_GAMING) {
				roomInfoJson = getRoomInfoGaming(user, game);
			} else if (status == GAGME_STATUS_OF_WAIT_START) {
				roomInfoJson = getRoomInfoGameWait(user, game);
			} else {
				logger.fatal("得到游戏信息时候除了这两个难道还有别的吗?应该永远不会进入到这个逻辑里面吧");
			}
		}
		return roomInfoJson;
	}

	/**
	 * 游戏准备中，得到房间的信息
	 * 
	 * @param user
	 * @param game
	 * @return
	 */
	private static JSONObject getRoomInfoGameWait(User user, Game game) {
		List<User> userList = game.getRoom().getUserList();
		JSONArray userJsonArray = new JSONArray();
		int bankUserId = getBankUserId(userList);// 庄家的userid
		OneRoom room = game.getRoom();
		for (int i = 0; i < userList.size(); i++) {
			JSONObject userJSONObject = new JSONObject();
			User u = userList.get(i);
			boolean ready = u.isReady();
			userJSONObject.put("ready", ready);
			boolean auto = u.isAuto();
			userJSONObject.put("isAuto", auto);
			userJSONObject.put("userid", u.getId());
			userJSONObject.put("userName", u.getUserName());
			userJSONObject.put("headimgurl", u.getHeadimgurl());
			userJSONObject.put("direction", u.getDirection());
			userJSONObject.put("ready", u.isReady());
			userJSONObject.put("isFz", u.isFangZhu());
			userJSONObject.put("isD", u.isDropLine());
			// int playerScoreByAdd = getUserCurrentGameScore(u);
			int playerScoreByAdd = u.getCurrentScore();
			userJSONObject.put("playerScoreByAdd", playerScoreByAdd);
			userJSONObject.put("sex", u.getSex());
			userJSONObject.put("ip", u.getIp());
			// 得到用户当前的分数
			userJsonArray.put(userJSONObject);
		}
		JSONObject infoJsonObject = new JSONObject();
		int totalGame = game.getTotalGame();// 总共的局数
		int alreadyTotalGame = game.getAlreadyTotalGame();// 已经玩的局数
		infoJsonObject.put("totalGame", totalGame);
		infoJsonObject.put("alreadyTotalGame", alreadyTotalGame);
		infoJsonObject.put("users", userJsonArray);
		infoJsonObject.put(method, "userDropLine");
		infoJsonObject.put(type, "gameForReady");
		infoJsonObject.put("bankUserId", bankUserId);
		infoJsonObject.put("roomId", room.getRoomNumber()); // 房间号
		infoJsonObject.put("zhamaNum", room.getZhama());// 扎码数
		infoJsonObject.put("wanf", room.getWanFa());//玩法
		infoJsonObject.put("chong", room.getChong());// 1包冲 2陪冲
		infoJsonObject.put("remainCardsTotal", game.getRemainCards().size());// 剩余的牌数
		return infoJsonObject;
	}

	/**
	 * 得到当前用户当前分数
	 * 
	 * @param u
	 *            当前用户
	 * @return 当前的分数
	 */
	public static int getUserCurrentGameScore(User u) {
		UserScoreDao userScoreDao = UserScoreDao.getInstance();// 用户分数
		UserScore userScore = new UserScore();
		userScore.setUserid(u.getId());
		userScore.setRoomid(Integer.parseInt(u.getRoomId()));
		// int playerScoreByAdd =
		// userScoreDao.selectUserScoreByCurrentRoomNumber(userScore);//用户当前的分数
		int playerScoreByAdd = u.getCurrentGameSore();// 用户当前的分数
		return playerScoreByAdd;
	}

	/**
	 * 替换掉用户的ioSession
	 * 
	 * @param user
	 * @param oneRoom
	 *            房间
	 */
	private static void replaceUserIoSession(User user, OneRoom oneRoom) {
		List<User> userList = oneRoom.getUserList();
		boolean isDropLine = false;
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			if (u.getId() == user.getId()) {
				ChannelHandlerContext newIoSession = user.getIoSession();
				ChannelHandlerContext oldIoSession = u.getIoSession();
				if (oldIoSession.hashCode() != newIoSession.hashCode()) {
					// oldIoSession.close();
					isDropLine = true;
				}
				u.setDropLine(false);
				u.setIoSession(user.getIoSession());
				user.setDirection(u.getDirection());// 方向也变
				user.setAuto(u.isAuto());// 是否托管
				user.setRoomId(u.getRoomId());// 房间号传过来
				user.setCards(u.getCards());// 牌改变过来
			}
		}
		if (isDropLine) {
			JSONObject dropLine = new JSONObject();
			dropLine.put("method", "uState");
			dropLine.put("id", user.getId());
			dropLine.put("d", user.getDirection());
			dropLine.put("type", 1);
			for (int i = 0; i < userList.size(); i++) {
				User u = userList.get(i);
				u.getIoSession().write(dropLine.toString());
			}
		}
	}

	/**
	 * 游戏进行中，通知玩家
	 * 
	 * @param roomInfoJson
	 * @param user
	 */
	private static void nofiyUserRoomInfo(JSONObject roomInfoJson, User user) {
		NotifyTool.notify(user.getIoSession(), roomInfoJson);
		;
	}

	/**
	 * 断线重连的时候游戏没有开始,
	 * 
	 * @param user
	 * @param oneRoom
	 */
	private static void gameNotStartReplaceUserSession(User user, OneRoom oneRoom) {
		JSONObject outJsonObject = new JSONObject();
		getRoomInfo(outJsonObject, oneRoom);
		outJsonObject.put("method", "enterRoom");
		outJsonObject.put("code", "success");
		NotifyTool.notify(user.getIoSession(), outJsonObject);// 通知他本人房间里的信息
		replaceRoomSession(oneRoom, user);
	}

	/**
	 * 替换掉原来用户
	 * 
	 * @param oneRoom
	 * @param user
	 */
	public static void replaceRoomSession(OneRoom oneRoom, User user) {
		List<User> userList = oneRoom.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			if (u.getId() == user.getId()) {
				u.setIoSession(user.getIoSession());// 替换它的ioSession
			}
		}
	}

	/**
	 * 游戏已经开始(游戏进行中)
	 * 
	 * @param user
	 * @param game
	 */
	private static JSONObject getRoomInfoGamingOld(User user, Game game) {
		OneRoom room = game.getRoom();
		JSONObject roomInfoJSONObject = new JSONObject();// 房间信息
		JSONArray userInfoJsonArray = new JSONArray();
		String direc = game.getDirec();
		roomInfoJSONObject.put("nowDirection", direc);
		Date lastChuPaiDate = game.getSeatMap().get(direc).getLastChuPaiDate();
		Date nowDate = new Date();
		long interval = CountDownThread.getInterval(lastChuPaiDate, nowDate);
		int status = game.getGameStatus();
		long intervalOfDate = getIntervalOfDate(status, interval);// 倒计时从哪里开始
		int d = (int) (intervalOfDate / 1000);
		roomInfoJSONObject.put("countDownTime", d);
		roomInfoJSONObject.put(method, "userDropLine");
		roomInfoJSONObject.put(type, "gameStart");
		roomInfoJSONObject.put("roomId", room.getRoomNumber()); // 房间号
		roomInfoJSONObject.put("zhamaNum", room.getZhama());// 扎码数
		roomInfoJSONObject.put("remainCardsTotal", game.getRemainCards().size());// 剩余的牌数
		List<User> userList = room.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			JSONObject myInfo;
			if (u.getId() == user.getId()) {
				myInfo = u.getMyInfo(true);
			} else {
				myInfo = u.getMyInfo(true);
			}
			userInfoJsonArray.put(myInfo);
		}
		int totalGame = game.getTotalGame();// 总共的局数
		int bankUserId = getBankUserId(userList);// 庄家的userid
		int alreadyTotalGame = game.getAlreadyTotalGame();// 已经玩的局数
		roomInfoJSONObject.put("totalGame", totalGame);
		roomInfoJSONObject.put("alreadyTotalGame", alreadyTotalGame);
		roomInfoJSONObject.put("users", userInfoJsonArray);
		roomInfoJSONObject.put("bankUserId", bankUserId);
		roomInfoJSONObject.put(discription, "掉线重连,玩的过程中掉线");
		return roomInfoJSONObject;
	}

	/**
	 * 游戏已经开始(游戏进行中)
	 * 
	 * @param user
	 * @param game
	 */
	private static JSONObject getRoomInfoGaming(User user, Game game) {
		OneRoom room = game.getRoom();
		JSONObject roomInfoJSONObject = new JSONObject();// 房间信息
		JSONArray userInfoJsonArray = new JSONArray();
		String direc = game.getDirec();
		roomInfoJSONObject.put("nowDirection", direc);
		Date lastChuPaiDate = game.getSeatMap().get(direc).getLastChuPaiDate();
		Date nowDate = new Date();
		long interval = CountDownThread.getInterval(lastChuPaiDate, nowDate);
		int status = game.getGameStatus();
		long intervalOfDate = getIntervalOfDate(status, interval);// 倒计时从哪里开始
		int d = (int) (intervalOfDate / 1000);
		roomInfoJSONObject.put("countDownTime", d);
		roomInfoJSONObject.put(method, "userDropLine");
		roomInfoJSONObject.put(type, "gameStart");
		roomInfoJSONObject.put("roomId", room.getRoomNumber()); // 房间号
		roomInfoJSONObject.put("zhamaNum", room.getZhama());// 扎码数
		roomInfoJSONObject.put("wanf", room.getWanFa());//玩法
		roomInfoJSONObject.put("chong", room.getChong());// 1包冲 2陪冲
		roomInfoJSONObject.put("remainCardsTotal", game.getRemainCards().size());// 剩余的牌数
		// 得到游戏的状态碰牌、出牌、和杠牌、胡牌
		roomInfoJSONObject.put("status", status);// 游戏状态
		switch (status) {
		case GAGME_STATUS_OF_CHUPAI:// 出牌
			Map<String, User> seatMap = game.getSeatMap();
			User chuUser = seatMap.get(direc);
			roomInfoJSONObject.put("cUser", chuUser.getId());
			break;
		case GAGME_STATUS_OF_PENGPAI:// 碰牌
			User canPengUser = game.getCanPengUser();
			User fangPengUser = game.getFangPengUser();
			Integer autoPengCardId = game.getAutoPengCardId();// 自动碰牌的集合
			roomInfoJSONObject.put("pUser", canPengUser.getId());// 游戏状态
			roomInfoJSONObject.put("cUser", fangPengUser.getId());// 放碰的玩家
			roomInfoJSONObject.put("pId", autoPengCardId);// 碰牌的牌号
			break;
		case GAGME_STATUS_OF_GANGPAI:// 接杠
			User canGangUser = game.getCanGangUser();
			Integer autoGangCardId = game.getAutoGangCardId();
			User fangGangUser = game.getFangGangUser();
			roomInfoJSONObject.put("gUser", canGangUser.getId());
			roomInfoJSONObject.put("cUser", fangGangUser.getId());// 放杠的用户
			roomInfoJSONObject.put("gId", autoGangCardId);
			break;
		case GAGME_STATUS_OF_ANGANG:// 暗杠
			User canAnGangUser = game.getCanAnGangUser();
			List<Integer> anGangCards = game.getAnGangCards();
			roomInfoJSONObject.put("agUser", canAnGangUser.getId());
			roomInfoJSONObject.put("agId", anGangCards.get(0));
			break;
		case GAGME_STATUS_OF_GONG_GANG:// 公杠
			User gongGang = game.getCanGongGangUser();
			Integer cardId = game.getGongGangCardId();
			roomInfoJSONObject.put("ggUser", gongGang.getId());
			roomInfoJSONObject.put("ggId", cardId);
			break;
		case GAGME_STATUS_OF_WAIT_HU:// 等待胡牌
			User huUser = game.getCanHuUser();
			Integer myGrabCard = huUser.getMyGrabCard();
			roomInfoJSONObject.put("hUser", huUser.getId());
			int isCanGangType = game.getIsCanGangType();
			roomInfoJSONObject.put("gangType", isCanGangType);
			if (isCanGangType == 0) {// 公杠
				roomInfoJSONObject.put("ggId", myGrabCard);
			} else if (isCanGangType == 1) {// 暗杠
				roomInfoJSONObject.put("agIds", game.getAnGangCards());
				roomInfoJSONObject.put("agId", game.getAnGangCards().get(0));
			}
			break;
		case GAGME_STATUS_OF_WAIT_HU_NEW://新的胡牌
			User canHuUser = game.getCanHuUser();
			roomInfoJSONObject.put("hUser",canHuUser.getId());// 游戏状态
			if(!canHuUser.isZiMo()){//如果是放炮
				int fangPaoId = game.getFangPaoUser().getId();
				Integer autoHuCardId = game.getAutoHuCardId();
				roomInfoJSONObject.put("cUser",fangPaoId);//放炮人的id
				roomInfoJSONObject.put("hId",autoHuCardId);//放炮人的id
				int canPengOrCanGang = isCanPengOrCanGang(autoHuCardId, canHuUser.getCards());
				if(canPengOrCanGang==2){
					roomInfoJSONObject.put("isPeng",true);
					roomInfoJSONObject.put("isGang",true);
				}else if(canPengOrCanGang==1){
					roomInfoJSONObject.put("isPeng",true);
					roomInfoJSONObject.put("isGang",false);
				}else{
					roomInfoJSONObject.put("isPeng",false);
					roomInfoJSONObject.put("isGang",false);
				}
			}
			break;
		}
		List<User> userList = room.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			JSONObject myInfo;
			if (u.getId() == user.getId()) {
				myInfo = u.getMyInfo(true);
				myInfo.put("cpTotal", u.getChuPaiCiShu());
			} else {
				myInfo = u.getMyInfo(true);
			}
			userInfoJsonArray.put(myInfo);
		}
		int totalGame = game.getTotalGame();// 总共的局数
		int bankUserId = getBankUserId(userList);// 庄家的userid
		int alreadyTotalGame = game.getAlreadyTotalGame();// 已经玩的局数
		roomInfoJSONObject.put("totalGame", totalGame);
		roomInfoJSONObject.put("alreadyTotalGame", alreadyTotalGame);
		roomInfoJSONObject.put("users", userInfoJsonArray);
		roomInfoJSONObject.put("bankUserId", bankUserId);
		roomInfoJSONObject.put("bd", game.getBaida());
		roomInfoJSONObject.put(discription, "掉线重连,玩的过程中掉线");
		return roomInfoJSONObject;
	}

	
	
	/**
	 * @param cardId 出的那张牌
	 * @param cards 手中的牌
	 * @return 2 可以杠 1可以碰
	 */
	public static int isCanPengOrCanGang(int cardId,List<Integer> cards){
		int number =  cardId /4;
		int total = 0;
		for(int i=0;i<cards.size();i++){
			Integer card = cards.get(i);
			if(card/4==number){
				total++;
			}
			if(total==4){
				break;
			}
		}
		if(total==3){
			return 2;
		}else if(total==2){
			return 1;
		}
		return 0;
	}
	
	
	
	/**
	 * 得到断线重连的时间，倒计时从哪里开始
	 * 
	 * @param status
	 * @param interval
	 * @return
	 */
	public static long getIntervalOfDate(int status, long interval) {
		long time = 0;
		switch (status) {
		case GAGME_STATUS_OF_CHUPAI:
			time = 30000 - interval - 200;
			break;
		case GAGME_STATUS_OF_PENGPAI:
		case GAGME_STATUS_OF_GANGPAI:
		case GAGME_STATUS_OF_ANGANG:
		case GAGME_STATUS_OF_GONG_GANG:
			time = 10000 - interval - 200;
			break;
		}
		return time;
	}

	/**
	 * 得到房间里的庄家id
	 * 
	 * @param list
	 * @return
	 */
	public static int getBankUserId(List<User> list) {
		for (int i = 0; i < list.size(); i++) {
			User user = list.get(i);
			if (user.isBanker() == true) {
				return user.getId();
			}
		}
		logger.fatal("难道说没有庄家吗??????????????");
		return -1;
	}

	/**
	 * 检测用户是否在房间里
	 * 
	 * @return
	 */
	public boolean checkUserIsInRoom(User user) {
		String roomId = user.getRoomId();
		if (roomId != null && !"".equals(roomId)) {
			OneRoom room = RoomManager.getRoomWithRoomId(roomId);
			if (room != null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 用unionid进入房间
	 * 
	 * @param jsonObject
	 * @param session
	 */
	private void enterRoomWithUnionid(JSONObject jsonObject, ChannelHandlerContext session) {
		int roomId = jsonObject.getInt("roomId");
		OneRoom oneRoom = RoomManager.getRoomMap().get(roomId + "");
		int userid = jsonObject.getInt("ui");
		Map<String, String> hashMap = RedisUtil.getHashMap("uid"+userid,REDIS_DB);
		String userInfo = hashMap.get("baseInfo");
		User user = new User();
		if (userInfo != null) {
			User infoUser = getUserFromUserInfo(userInfo, userid + "");
			if (infoUser != null) {
				user = infoUser;
			} else {
				user.setId(userid);
				user = userDao.findUser2(user);
			}
		} else {
			user.setId(userid);
			user = userDao.findUser2(user);
		}
		if (user == null) {
			return;
		} else {
			user.setIoSession(session);
		}
		boolean userCanEnterRoom = isUserCanEnterRoom(user, oneRoom, session);
		if (userCanEnterRoom) {// 如果可以进入房间
//			changeUserHeadimageWithUserUnionid(user);
			int size = oneRoom.getUserList().size();
			if (size < 4) {// 房间人数小于4人
				session.channel().attr(AttributeKey.<User>valueOf("user")).set(user);
				dateService.addLoginUser();
				setUserDircetion(user, oneRoom);// 设置用户的方向
				user.setRoomId(roomId + "");
				user.setCurrentGame(0);
				user.setCurrentScore(oneRoom.getZhama());
				user.setAuto(false);
				int createUserId = oneRoom.getCreateUserId();
				if (user.getId() == createUserId) {
					user.setFangZhu(true);
				}
				oneRoom.addUser(user);
				JSONObject outJsonObject = new JSONObject();
				outJsonObject.put("method", "enterRoom");
				getRoomInfo(outJsonObject, oneRoom);
				outJsonObject.put("code", "success");
				modifyUserRoomNumber(user);// 修改玩家的房间号
				NotifyTool.notify(session, outJsonObject);// 通知他本人房间里的信息
				notifyOtherUserEnterRoom(oneRoom, user);
			} else if (oneRoom.getUserList().size() >= 4) {
				JSONObject outJsonObject = new JSONObject();
				outJsonObject.put("method", "enterRoom");
				outJsonObject.put("code", "error");
				outJsonObject.put("description", "房间已满");
				session.write(outJsonObject.toString());
			}
		}
	}

	/**
	 * 真正的进入房间
	 * 
	 * @param jsonObject
	 * @param session
	 */
	private void realEnterRoom(JSONObject jsonObject, ChannelHandlerContext session) {
		Object sroomId = jsonObject.get("roomId");// 房间号
		if (sroomId == null || "".equals(sroomId)) {
			JSONObject errorJsonObject = new JSONObject();
			errorJsonObject.put(method, "enterRoom");
			errorJsonObject.put(discription, "请输入房间号");
			errorJsonObject.put(code, "error");
			NotifyTool.notify(session, errorJsonObject);
			return;
		}
		int roomId = jsonObject.getInt("roomId");
		OneRoom oneRoom = RoomManager.getRoomMap().get(roomId + "");
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("method", "enterRoom");
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		boolean userCanEnterRoom = isUserCanEnterRoom(user, oneRoom, session);
		if (userCanEnterRoom) {
			int size = oneRoom.getUserList().size();
			if (size < 4) {// 房间人数小于4人
				setUserDircetion(user, oneRoom);// 设置用户的方向
				user.setRoomId(roomId + "");
				user.setCurrentGame(0);
				user.setAuto(false);
				oneRoom.addUser(user);
				getRoomInfo(outJsonObject, oneRoom);
				outJsonObject.put("code", "success");
//				modifyUserRoomNumber(user);// 修改玩家的房间号
				String setKey = RedisUtil.setKey("usRoomId"+user.getId(),oneRoom.getId()+"",REDIS_DB);
				NotifyTool.notify(session, outJsonObject);// 通知他本人房间里的信息
				notifyOtherUserEnterRoom(oneRoom, user);
			} else if (oneRoom.getUserList().size() >= 4) {
				outJsonObject.put("code", "error");
				outJsonObject.put("description", "房间已满");
				session.write(outJsonObject.toString());
			}
		}
	}

	/**
	 * 修改玩家的房间号
	 * 
	 * @param user
	 */
	private static void modifyUserRoomNumber(User user) {
		User modifyUser = new User();
		modifyUser.setId(user.getId());
		modifyUser.setRoomId(user.getRoomId());
		userDao.modifyUser(user.getId(),user.getRoomId()+"");// 记录下用户的房间号
		Map<String, String> hashMap = RedisUtil.getHashMap("uid"+user.getId(),REDIS_DB);
		String roomId = user.getRoomId();
		boolean nullOrEmpty = StringUtils.isNullOrEmpty(roomId);
		if (nullOrEmpty) {
			roomId = 0 + "";
		}
		hashMap.put("roomId", roomId);
		RedisUtil.setHashMap("uid"+user.getId(), hashMap,1,TIME_TO_USER_ROOM);
	}

	/**
	 * 通知其他玩家，有人进入房间
	 * 
	 * @param oneRoom
	 * @param currentUser
	 */
	public static void notifyOtherUserEnterRoom(OneRoom oneRoom, User currentUser) {
		List<User> userList = oneRoom.getUserList();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(method, "notifyOtherUserEnterRoom");
		int userId = currentUser.getId();
		jsonObject.put("userId", userId);
		String userName = currentUser.getUserName();
		boolean ready = currentUser.isReady();
		jsonObject.put("userName", userName);
		jsonObject.put("ready", ready);
		jsonObject.put("headimgurl", currentUser.getHeadimgurl());
		jsonObject.put("dirction", currentUser.getDirection());
		jsonObject.put("sex", currentUser.getSex());
		jsonObject.put("isFz", currentUser.isFangZhu());
		jsonObject.put("playerScoreByAdd", currentUser.getCurrentScore());
		String ipAddress = currentUser.getIoSession().channel().remoteAddress().toString();
		jsonObject.put("ip", ipAddress.replaceAll("/", ""));
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			if (user.getId() != currentUser.getId()) {
				ChannelHandlerContext ioSession = user.getIoSession();
				NotifyTool.notify(ioSession, jsonObject);
			}
		}
	}

	/**
	 * 检测用户是否可以进入房间
	 * 
	 * @param user
	 *            被检测的用户
	 * @param oneRoom
	 *            房间
	 * @param session
	 * @return
	 */
	public boolean isUserCanEnterRoom(User user, OneRoom oneRoom, ChannelHandlerContext session) {
		boolean result = true;// 可以进入房间
		JSONObject outJsonObject = new JSONObject();
		if (oneRoom == null) {
			outJsonObject.put("method", "enterRoom");
			outJsonObject.put("code", "error");
			outJsonObject.put("description", "房间不存在");
			session.write(outJsonObject.toString());
			// session.close();//房间不存在主动关闭session
			return false;
		}

		boolean userInRoom = isUserInRoom(oneRoom, user);
		if (userInRoom) {
			getRoomInfo(outJsonObject, oneRoom);
			outJsonObject.put("method", "enterRoom");
			// outJsonObject.put("code", "error");
			// outJsonObject.put("method", "enterRoom");
			// outJsonObject.put("description", "你已经进入房间，何必重复进入");
			session.write(outJsonObject.toString());
			return false;
		}
		return result;
	}

	/**
	 * 检验用户是否在这个房间里
	 * 
	 * @param oneRoom
	 * @param user
	 * @return
	 */
	public boolean isUserInRoom(OneRoom oneRoom, User user) {
		// 查看该用户是否已经在房间里
		List<User> userList = oneRoom.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			if (user.getId() == u.getId()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 设置用户在房间的方向
	 * 
	 * @param user
	 * @param userSize
	 */
	public void setUserDircetion(User user, OneRoom oneRoom) {
		Set<String> directionSet = oneRoom.getDirectionSet();
		if (!directionSet.contains("east")) {
			user.setDirection("east");
			directionSet.add("east");
		} else if (!directionSet.contains("north")) {
			user.setDirection("north");
			directionSet.add("north");
		} else if (!directionSet.contains("west")) {
			user.setDirection("west");
			directionSet.add("west");
		} else if (!directionSet.contains("south")) {
			user.setDirection("south");
			directionSet.add("south");
		}
	}

	/**
	 * 得到房间的信息
	 * 
	 * @param outJsonObject
	 * @param session
	 * @param oneRoom
	 */
	private static void getRoomInfo(JSONObject outJsonObject, OneRoom oneRoom) {
		List<User> userList = oneRoom.getUserList();
		JSONArray userArray = new JSONArray();
		for (int i = 0; i < userList.size(); i++) {
			JSONObject userJsonObject = new JSONObject();
			User user = userList.get(i);
			int userId = user.getId();
			userJsonObject.put("userId", userId);
			String userName = user.getUserName();
			boolean ready = user.isReady();
			userJsonObject.put("userName", userName);
			userJsonObject.put("headimgurl", user.getHeadimgurl());// 头像
			userJsonObject.put("isFz", user.isFangZhu());// 是否房主
			userJsonObject.put("ready", ready);
			userJsonObject.put("dirction", user.getDirection());
			userJsonObject.put("playerScoreByAdd", user.getCurrentScore());
			userJsonObject.put("sex", user.getSex());
			userJsonObject.put("isD", user.isDropLine());
			userJsonObject.put("ip", user.getIoSession().channel().remoteAddress());// IP地址
			userArray.put(userJsonObject);
		}
		outJsonObject.put("users", userArray);
		outJsonObject.put("zhama", oneRoom.getZhama());
		outJsonObject.put("total", oneRoom.getTotal());
		outJsonObject.put("roomId", oneRoom.getRoomNumber());
		outJsonObject.put("wanf", oneRoom.getWanFa());
		outJsonObject.put("chong", oneRoom.getChong());
	}

	/**
	 * 准备游戏
	 * 
	 * @param jsonObject
	 * @param session
	 */
	public void readyGame(JSONObject jsonObject, ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		Game game = PlayGameService.getGame(user);
		if (game != null) {
			if (game.getStatus() == GAGME_STATUS_OF_IS_GAMING) {
				logger.fatal("游戏正在进行中用户点击了准备:" + user.getNickName() + " userId:" + user.getId());
				JSONObject outJsonObejct = new JSONObject();
				outJsonObejct.put(method, "gameError");
				session.write(outJsonObejct.toString());
				return;
			}
		}
		String roomId = user.getRoomId();
		if (roomId == null || "".equals(roomId)) {
			NotifyTool.notifyUserErrorMessage(session, "请先进入房间");
			return;
		}
		OneRoom oneRoom = RoomManager.getRoomMap().get(roomId);
		List<User> userList = oneRoom.getUserList();
		for (User u : userList) {
			if (user.getId() == u.getId()) {
				u.setReady(true);
				break;
			}
		}
		JSONObject outJsonObject = getReadyJsonObject(user);
		NotifyTool.notifyIoSessionList(oneRoom.getUserIoSessionList(), outJsonObject);
		int totalReady = getTotalReady(userList);
		if (totalReady == 4) {// 开始游戏
			startGame(oneRoom, session);
		}
	}

	/**
	 * 得到准备jsonObject
	 * 
	 * @param user
	 * @return
	 */
	public static JSONObject getReadyJsonObject(User user) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("direction", user.getDirection());
		outJsonObject.put("ready", true);
		outJsonObject.put("method", "readyGame");
		outJsonObject.put(discription, "准备游戏");
		return outJsonObject;
	}

	/**
	 * 开始游戏
	 * 
	 * @param oneRoom
	 */
	private void startGame(OneRoom oneRoom, ChannelHandlerContext session) {
		boolean deductionBankUserCard = true;
		if (!oneRoom.isPay()) {
			// 扣除房卡
			deductionBankUserCard = deductionBankUserCard(oneRoom);
			oneRoom.setPay(true);
		}
		if (deductionBankUserCard) {
			beginGame(oneRoom);// 开始游戏
		}
	}

	/**
	 * 扣除房主的放开,如果是16局扣除2张,8局扣除1张
	 * 
	 * @param oneRoom
	 */
	private boolean deductionBankUserCard(OneRoom oneRoom) {
		User createUser = oneRoom.getCreateUser();
		int total = oneRoom.getTotal();
		boolean canCreateRoom = checkUserCanCreateRoom(createUser, total);
		if (!canCreateRoom) {// 房主房卡不足
			realDisbandRoom(createUser, oneRoom.getRoomNumber(), oneRoom);
			return false;
		}
		int userCard = 0;
		HashMap<String, Object> map = new HashMap<>();
		map.put("roomId", oneRoom.getRoomNumber());// 房间号
		map.put("userId", oneRoom.getCreateUserId());// 用户id
		map.put("totalGame", oneRoom.getTotal());// 总局数
		map.put("type", 1);// 充值0,消费 1
		map.put("total", 0);// 充值的房卡数量
		userCard = roomDao.userConsumeCard(map);
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "getUserCard");
		outJsonObject.put(discription, "剩余的房卡数");
		outJsonObject.put("userCard", userCard);
		notifyUserInfo(outJsonObject, createUser.getIoSession());
		try {
			// 更改缓存里面用户的房卡数
			Map<String, String> hashMap = RedisUtil.getHashMap("uid"+createUser.getId(),REDIS_DB);
			String userInfo = hashMap.get("baseInfo");
			if (!StringUtils.isNullOrEmpty(userInfo)) {
				JSONObject userInfoJson = new JSONObject(userInfo);
				userInfoJson.put("roomCard", userCard);
				hashMap.put("baseInfo", userInfoJson.toString());
				RedisUtil.setHashMap("uid"+createUser.getId(), hashMap,1,TIME_TO_USER_ROOM);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e);
		}
		return true;
	}

	/**
	 * 通知用户的房卡信息
	 * 
	 * @param outJsonObject
	 * @param ioSession
	 */
	private void notifyUserInfo(JSONObject outJsonObject, ChannelHandlerContext ioSession) {
		ioSession.write(outJsonObject.toString());
	}

	/**
	 * 开始游戏
	 * 
	 * @param oneRoom
	 */
	public void beginGame(OneRoom oneRoom) {
		Game game = getGame(oneRoom);
		if(game.getSeatMap()==null){
			game.generateSeat();//生成座次
			dingDongFeng(game);
		}
		dealCard(oneRoom, game);
	}

	/**定东风
	 * @param game
	 */
	private void dingDongFeng(Game game) {
		// 定东风
//		System.out.println("12");
		int dongFengCard = MathUtil.getDongFeng();
		String eastDirection = getEastWithDongFengNumber(dongFengCard);
			setGameSeat(game, eastDirection);
		JSONObject dongFengJsonObject =  new JSONObject();
		dongFengJsonObject.put("discription", "定东风");
		dongFengJsonObject.put("method", "setUserDirection");
		dongFengJsonObject.put("dirEastId", dongFengCard);
		JSONArray userDir =  new JSONArray();
		List<User> userlist = game.getUserList();
		for(int i=0;i<userlist.size();i++){
			User user = userlist.get(i);
			String direction = user.getDirection();
			int userid = user.getId();
			JSONObject userJson =  new JSONObject();
			userJson.put("userId", userid);
			userJson.put("direction", direction);
			userDir.put(userJson);
		}
		dongFengJsonObject.put("userDir", userDir);
		NotifyTool.notifyIoSessionList(game.getIoSessionList(), dongFengJsonObject);
	}

	private void setGameSeat(Game game, String eastDirection) {
		User eastUser = game.getGamingUser("east");
		User northUser = game.getGamingUser("north");
		User westUser = game.getGamingUser("west");
		User southUser = game.getGamingUser("south");
		Map<String, User> seatMap = game.getSeatMap();
		switch (eastDirection) {
		case "east":
			eastUser.setBanker(true);
			break;
		case "north":
			northUser.setDirection("east");
			westUser.setDirection("north");
			southUser.setDirection("west");
			eastUser.setDirection("south");
			northUser.setBanker(true);
			seatMap.put("east", northUser);
			seatMap.put("north", westUser);
			seatMap.put("west", southUser);
			seatMap.put("south", eastUser);
			break;
		case "west":
			westUser.setBanker(true);
			westUser.setDirection("east");
			southUser.setDirection("north");
			eastUser.setDirection("west");
			northUser.setDirection("south");
			seatMap.put("east", westUser);
			seatMap.put("north", southUser);
			seatMap.put("west", eastUser);
			seatMap.put("south", northUser);
			break;
		case "south":
			southUser.setBanker(true);
			southUser.setDirection("east");
			eastUser.setDirection("north");
			northUser.setDirection("west");
			westUser.setDirection("south");
			seatMap.put("east", southUser);
			seatMap.put("north", eastUser);
			seatMap.put("west", northUser);
			seatMap.put("south", westUser);
			break;
		}
	}

	public String getEastWithDongFengNumber(int dongFengCard) {
		String direction = "";
		switch (dongFengCard) {
		// 自已为东
		case 5:
		case 9:
			direction = "east";
			break;
		// 下家为东
		case 2:
		case 6:
		case 10:
			direction = "north";
			break;
		// 对家为东
		case 3:
		case 7:
		case 11:
			direction = "west";
			break;
		// 上家为东
		case 4:
		case 8:
		case 12:
			direction = "south";
			break;
		}
		return direction;
	}

	/**
	 * 发牌
	 * 
	 * @param oneRoom
	 * @param game
	 */
	public void dealCard(OneRoom oneRoom, Game game) {
		game.setGameStatus(GAGME_STATUS_OF_CHUPAI);// 游戏的状态
		game.setStatus(GAGME_STATUS_OF_IS_GAMING);// 游戏进行中
		game.playGame();
		String nowDirection = getFirstDrection(oneRoom);// 得到第一次出牌的方向,也就是庄家的方向
		User bankUser = game.getSeatMap().get(nowDirection);
		List<Integer> bankCards = bankUser.getCards();
		HuPai huPai = userWinTianHu(bankCards,game.getBaida());
		boolean win = huPai.isHu();
		notifyUserStartGame(game, win);// 通知用户开启游戏,并且设置游戏的状态
		if (win) {// 如果天胡
			int size = bankCards.size();
			Integer removeCard = bankCards.get(size - 1);
			PlayGameService.userWin(game, removeCard, bankUser, true);
			return;
		}
		// 设置第一次庄家的出牌时间
		setBankUserFirstPlayCard(oneRoom);
		// 设置用户的的准备状态为未准备
		setUserReadyFalse(oneRoom);
		// 开启一个线程监听该用户是否出牌
		game.getGameStatusMap().put(game.getAlreadyTotalGame() + 1, "START");
		boolean bankUserIsAuto = getBankUserIsAuto(oneRoom);
		if (oneRoom.getAuto() == 1) {// 如果是自动托管
			CountDownThread countDownThread = new CountDownThread(oneRoom.getId() + "", game.getAlreadyTotalGame() + 1);
			Thread thread = new Thread(countDownThread);
			thread.start();
		}
		// 查看第一个用户是否是托管状态
		if (bankUserIsAuto) {// 如果庄家托管
			int status = game.getGameStatus();
			if (status == GAGME_STATUS_OF_CHUPAI) {
				PlayGameService.autoChuPai(game);
			} else if (status == GAGME_STATUS_OF_ANGANG) {
				PlayGameService.autoAnGang(game);
			}
		}
	}

	/**
	 * 查看是否天胡,4个红中直接胡牌
	 * 
	 * @param bankCards
	 * @return
	 */
	public static HuPai userWinTianHu(List<Integer> bankCards,int baiDa) {
		HuPai huPai = new HuPai();
		boolean win = huPai.isHu(bankCards, baiDa);
		if (win) {
			return huPai;
		}
		int totalHongZhong = 0;
		if (!win) {
			for (int i = 0; i < bankCards.size(); i++) {
				Integer cardId = bankCards.get(i);
				if (cardId/4 == baiDa/4) {
					totalHongZhong++;
				}
			}
		}
		if (totalHongZhong == 4) {
			huPai.setHu(true);
			return huPai;
		}
		return huPai;
	}

	/**
	 * 得到游戏如果是第一句则创建游戏，否则从GameManager中获取
	 * 
	 * @param roomId
	 * @return
	 */
	private Game getGame(OneRoom oneRoom) {
		Game game = GameManager.getGameWithRoomNumber(oneRoom.getId() + "");
		if (game != null) {
			return game;
		} else {
			game = new Game(oneRoom);// 创建一个游戏
			GameManager.addGameMap(oneRoom.getId() + "", game);// 用
																// gameMap管理这个游戏
			return game;
		}
	}

	/**
	 * 得到庄家是不是托管
	 * 
	 * @param oneRoom
	 * @return
	 */
	public boolean getBankUserIsAuto(OneRoom oneRoom) {
		List<User> userList = oneRoom.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			if (user.isBanker()) {
				user.setUserCanPlay(true);
				return user.isAuto();
			}
		}
		return false;
	}

	/**
	 * 通知用户开启游戏
	 * 
	 * @param game
	 * @param isWin
	 *            是否天胡
	 */
	public static void notifyUserStartGame(Game game, boolean isWin) {
		OneRoom oneRoom = game.getRoom();
		List<User> userList = oneRoom.getUserList();
		Map<String, User> seatMap = game.getSeatMap();
		User firstUser = seatMap.get(game.getDirec());
		game.setGameStatus(GAGME_STATUS_OF_CHUPAI);// 出牌的状态
		for (int i = 0; i < userList.size(); i++) {
			JSONObject outJsonObject = new JSONObject();
			User user = userList.get(i);
			if (user.isBanker() && !isWin) {// 如果是庄家并且没有天胡,检测是否可以暗杠
				List<Integer> userCanAnGang = PlayGameService.isUserCanAnGang(user,game.getBaida(),game.getFanpai());
				if (userCanAnGang.size() > 0) {
					outJsonObject.put("isCanGangType", AN_GANG);
					outJsonObject.put("anGangcards", userCanAnGang);
					game.setGameStatus(GAGME_STATUS_OF_ANGANG);// 暗杠的状态
					game.setAnGangCards(userCanAnGang);
					game.setCanAnGangUser(user);// 可以暗杠的玩家
				}
			}
			List<Integer> cards = user.getCards();
			outJsonObject.put("userName", user.getUserName());
			outJsonObject.put("cards", cards);// 该用户的牌
			outJsonObject.put("userId", firstUser.getId());
			outJsonObject.put("direction", game.getDirec());
			outJsonObject.put(discription, "游戏已开始");
			outJsonObject.put("zhuang", firstUser.getDirection());// 庄
			outJsonObject.put("cpTotal", firstUser.getChuPaiCiShu());// 出牌次数
			outJsonObject.put("method", "startGame");
			outJsonObject.put("currentGame", game.getAlreadyTotalGame() + 1);
			outJsonObject.put("bd", game.getBaida());
			NotifyTool.notify(user.getIoSession(), outJsonObject);
		}
	}

	/**
	 * 设置庄家第一次出牌的时间
	 * 
	 * @param oneRoom
	 */
	private void setBankUserFirstPlayCard(OneRoom oneRoom) {
		List<User> userList = oneRoom.getUserList();
		for (User user : userList) {
			if (user.isBanker()) {
				user.setLastChuPaiDate(new Date());
				break;
			}
		}
	}

	/**
	 * 设置房间里的人的准备状态为未准备
	 * 
	 * @param oneRoom
	 */
	public void setUserReadyFalse(OneRoom oneRoom) {
		List<User> userList = oneRoom.getUserList();
		for (User user : userList) {
			user.setReady(false);
		}
	}

	/**
	 * 通知玩家游戏已经开始
	 * 
	 * @param session
	 */
	private void noticeUserGameAlreadyStart(ChannelHandlerContext session) {
		session.write("游戏已经开始，请不要在点击准备");
	}

	/**
	 * 检测房间是否已经使用
	 * 
	 * @param oneRoom
	 * @return
	 */
	private boolean checkRoomIsUse(OneRoom oneRoom) {

		boolean isUse = oneRoom.isUse();

		return isUse;
	}

	/**
	 * @param oneRoom
	 * @return
	 */
	public static String getFirstDrection(OneRoom oneRoom) {
		List<User> userList = oneRoom.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			if (user.isBanker()) {
				return user.getDirection();
			}
		}
		return "";
	}

	/**
	 * 通知其他玩家
	 * 
	 * @param userList
	 * @return
	 */
	private int getTotalReady(List<User> userList) {
		int totalReady = 0;
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			if (u.isReady() == true) {
				totalReady++;
			}
		}
		return totalReady;
	}

	/**
	 * 得到我自己的信息
	 * 
	 * @param jsonObject
	 * @param session
	 */
	public void getMyInfo(JSONObject jsonObject, ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		String roomId = user.getRoomId();
		if (roomId == null || "".equals(roomId)) {
			session.write("啥都没有");
			return;
		}
		Map<String, Game> gameMap = GameManager.getGameMap();
		Game game = gameMap.get(roomId);
		Map<String, User> seatMap = game.getSeatMap();
		User user2 = seatMap.get(user.getDirection());
		List<Integer> cards1 = user.getCards();
		List<Integer> cards2 = user2.getCards();
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("sessionCards", cards1);
		outJsonObject.put("GameManagerCards", cards2);
		session.write(outJsonObject.toString());

		StringBuffer sb = new StringBuffer("  ");
		for (int i = 0; i < cards1.size(); i++) {
			Integer card = cards1.get(i);
			String cardType = CardsMap.getCardType(card);
			sb.append(cardType + "");
		}

		StringBuffer sb2 = new StringBuffer("  ");
		for (int i = 0; i < cards2.size(); i++) {
			Integer card = cards1.get(i);
			String cardType = CardsMap.getCardType(card);
			sb2.append(cardType + "");
		}
		session.write("  sessionCards:" + sb + "   GameManagerCards:" + sb2);
	}

	/**
	 * 离开房间
	 * 
	 * @param jsonObject
	 * @param session
	 */
	public void leaveRoom(JSONObject jsonObject, ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		leave(user, session);
	}

	private void leave(User user, ChannelHandlerContext session) {
		OneRoom room = getUserRoom(user);
		if (room == null) {
			// FIXME
			// 这里需要封装成解散房间的算法-------------------------------------------------
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(method, "disbandRoom");
			jsonObject.put(code, "success");
			jsonObject.put(discription, "离开房间成功");
			NotifyTool.notify(user.getIoSession(), jsonObject);
			return;
		}
		Set<String> directionSet = room.getDirectionSet();
		directionSet.remove(user.getDirection());
		notifyUserLeaveRoom(room, user);
		user.setRoomId(null);
		room.userLeaveRoom(user);// 用户离开房间
		user.setDirection(null);
		user.setReady(false);
		
		
//		modifyUserRoomNumber(user);// 修改玩家的房间号
		
		RedisUtil.delKey("usRoomId"+user.getId(),1);
		if (session != null) {
			session.close();
		}
	}

	/**
	 * 用户离开房间
	 */
	public void leaveRoom(User user) {
		leave(user, null);
	}

	/**
	 * 得到当前游戏玩家的方向
	 * 
	 * @param user
	 * @return
	 */
	public OneRoom getUserRoom(User user) {
		String roomId = user.getRoomId();
		OneRoom oneRoom = RoomManager.getRoomMap().get(roomId);
		return oneRoom;
	}

	/**
	 * 通知房间的玩家离开游戏
	 * 
	 * @param room
	 *            当前的房间
	 * @param user
	 *            离开的玩家
	 */
	private void notifyUserLeaveRoom(OneRoom room, User user) {
		List<User> userList = room.getUserList();
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "leaveRoom");
		outJsonObject.put(direction, user.getDirection());
		outJsonObject.put("userId", user.getId());
		for (User u : userList) {
			ChannelHandlerContext ioSession = u.getIoSession();
			NotifyTool.notify(ioSession, outJsonObject);
		}
	}

	/**
	 * 房主解散房间
	 * 
	 * @param jsonObject
	 * @param session
	 */
	public void disbandRoom(JSONObject jsonObject, ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		disband(session, user);
	}

	private void disband(ChannelHandlerContext session, User user) {
		int roomId = Integer.parseInt(user.getRoomId());
		OneRoom oneRoom = RoomManager.getRoomMap().get(roomId + "");
		// 检测房间是否存在
		if (oneRoom == null) {
			return;
		}
		if (oneRoom.isUse()) {
			JSONObject outJSONbject = new JSONObject();
			outJSONbject.put(discription, "游戏已开始，开心的玩吧");
			outJSONbject.put(method, "disbandRoom");
			outJSONbject.put(code, error);
			NotifyTool.notify(session, outJSONbject);
			;
			return;
		}
		int createUserId = oneRoom.getCreateUserId();
		if (createUserId != user.getId()) {
			JSONObject outJSONbject = new JSONObject();
			outJSONbject.put(discription, "只有房主可以解散房间");
			outJSONbject.put(method, "disbandRoom");
			outJSONbject.put(code, error);
			NotifyTool.notify(session, outJSONbject);
			;
			return;
		}

		boolean disband = oneRoom.isDisband();
		if (!disband) {
			realDisbandRoom(user, roomId, oneRoom);
		}
	}

	/**
	 * 解散房间
	 * 
	 * @param user
	 *            房主
	 * @param roomId
	 * @param oneRoom
	 */
	public static void realDisbandRoom(User user, int roomId, OneRoom oneRoom) {
		oneRoom.setDisband(true);// 房间解散
		List<User> userList = oneRoom.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			u.setReady(false);// 不准备
			u.setAuto(false);// 不托管
			JSONObject outJSONbject = new JSONObject();
			outJSONbject.put(method, "disbandRoom");
			outJSONbject.put(code, success);
			if (u.getId() != user.getId()) {
				outJSONbject.put(discription, "房主已经解散房间");
			} else {
				outJSONbject.put(discription, "房间已解散不扣房卡");
			}
			ChannelHandlerContext ioSession = u.getIoSession();
			NotifyTool.notify(ioSession, outJSONbject);
			// ioSession.close();//主动关闭所有session
		}
		// 从RoomManager中移除房间
		RoomManager.removeOneRoomByRoomId(roomId + "");
		user.setRoomId("");
		user.setBanker(false);
//		modifyUserRoomNumber(user);// 修改玩家的房间号
		RedisUtil.delKey("usRoomId"+user.getId(),1);
		// redis移除房间信息
		RedisUtil.delKey("zjRoomId"+roomId,1);
	}

	/**
	 * 继续游戏
	 * 
	 * @param jsonObject
	 * @param session
	 */
	public void continueGame(JSONObject jsonObject, ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		String roomId = user.getRoomId();
		Game game = PlayGameService.getGame(user);
		OneRoom oneRoom = RoomManager.getRoomMap().get(roomId);
		List<User> userList = oneRoom.getUserList();
		JSONObject outJSONObject = new JSONObject();
		JSONArray userArray = new JSONArray();
		for (User u : userList) {
			JSONObject userJson = new JSONObject();
			userJson.put("userId", u.getId());
			userJson.put(direction, u.getDirection());
			userJson.put("userName", u.getNickName());
			userJson.put("ready", u.isReady());
			userArray.put(userJson);
		}
		outJSONObject.put("users", userArray);
		outJSONObject.put(method, "continueGame");
		NotifyTool.notify(session, outJSONObject);
	}

	/**
	 * 设置托管
	 * 
	 * @param jsonObject
	 * @param session
	 */
	public void settingAuto(JSONObject jsonObject, ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		// 如果游戏已经开始，则设置游戏里面的用户托管，否则设置,房间里的用户托管
		Game game = GameManager.getGameWithRoomNumber(user.getRoomId());
		if (game == null) {
			return;
		}
		User gamingUser = null;
		List<User> userList = game.getUserList();
		for(int i=0;i<userList.size();i++){
			User u = userList.get(i);
			if(u.getId()==user.getId()){
				gamingUser = u;
			}
		}
		if(gamingUser == null){
			return;
		}
		boolean auto = gamingUser.isAuto();
		if (!auto) {
			setGamingUserAuto(game, gamingUser);
		}
		JSONObject outJsonObject = getAutoJsonObject(gamingUser);
		OneRoom oneRoom = RoomManager.getRoomMap().get(user.getRoomId());
		// 如果房间为空，说明已经离开房间了
		if (oneRoom == null) {
			session.write(outJsonObject);
			return;
		}
		oneRoom.noticeUsersWithJsonObject(outJsonObject);
		// 如果当前游戏的方向移动到该玩家的方向,则自动替他出牌,或杠牌,或碰牌
		palyIfTheGameDirectionIsMyDirection(gamingUser);
	}

	/**
	 * 得到托管的返回数据
	 * 
	 * @param user
	 * @return
	 */
	public static JSONObject getAutoJsonObject(User user) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "settingAuto");
		outJsonObject.put(discription, "设置托管成功");
		outJsonObject.put("userId", user.getId());
		outJsonObject.put(direction, user.getDirection());
		return outJsonObject;
	}

	/**
	 * 设置游戏中的玩家准备
	 * 
	 * @param game
	 * @param user
	 */
	private void setGamingUserAuto(Game game, User user) {
		Map<String, User> seatMap = game.getSeatMap();
		if (seatMap != null) {
			User seatMapUser = seatMap.get(user.getDirection());
			seatMapUser.setAuto(true);
			user.setAuto(true);
		}
	}

	/**
	 * 设置房间里的玩家是否托管
	 * 
	 * @param user
	 * @param status
	 *            true准备,false取消准备
	 */
	private void setRoomUserStatus(User user, boolean status) {
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(user.getRoomId());
		if (oneRoom == null) {
			return;
		}
		List<User> userList = oneRoom.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			if (u.getId() == user.getId()) {
				u.setAuto(status);
				user.setAuto(status);
				user.setTotalNotPlay(0);// 用户没有出牌的次数清零
			}
		}
	}

	/**
	 * 如果当前游戏的方向移动到该玩家的方向,则自动替他出牌,或杠牌,或碰牌
	 * 
	 * @param user
	 */
	private void palyIfTheGameDirectionIsMyDirection(User user) {
		Game game = PlayGameService.getGame(user);
		if (game != null) {
			String gameDirection = game.getDirec();// 游戏的方向
			if (gameDirection != null && gameDirection.equals(user.getDirection())) {
				int status = game.getGameStatus();
				switch (status) {
				case GAGME_STATUS_OF_CHUPAI:// 出牌
					PlayGameService.autoChuPai(game);// 出牌s
					break;
				case GAGME_STATUS_OF_PENGPAI:// 碰牌
				case GAGME_STATUS_OF_GANGPAI:// 杠牌
					PlayGameService.autoPengOrGang(user, game);
					break;
				case GAGME_STATUS_OF_ANGANG:// 暗杠
					PlayGameService.autoAnGang(game);
					break;
				case GAGME_STATUS_OF_GONG_GANG:// 公杠
					PlayGameService.autoGongGang(game);
					break;
				}
			}
		}
	}

	/**
	 * 取消托管
	 * 
	 * @param jsonObject
	 * @param session
	 */
	public void cancelAuto(JSONObject jsonObject, ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		setRoomUserStatus(user, false);
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "cancelAuto");
		outJsonObject.put(discription, "取消托管成功");
		outJsonObject.put("userId", user.getId());
		outJsonObject.put(direction, user.getDirection());
		OneRoom oneRoom = RoomManager.getRoomMap().get(user.getRoomId());
		// 防止用户卡死在房间里面点击托管
		if (oneRoom == null) {
			session.write(outJsonObject);
			return;
		}
		oneRoom.noticeUsersWithJsonObject(outJsonObject);
	}

	@Test
	public void test() {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "cancelAuto");
		outJsonObject.put(discription, "设置托管成功");
		outJsonObject.put("userId", 1);
		outJsonObject.put(direction, "east");
		System.out.println(outJsonObject);
	}

	@Override
	public void playGame(JSONObject jsonObject, ChannelHandlerContext session) {
		playGameService.playGame(jsonObject, session);
	}

	@Override
	public void downGameInfoWithUnionid(JSONObject jsonObject, ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		int ui = jsonObject.getInt("ui");
		if (user == null) {// 用户还没有登录
			Map<String, String> hashMap = RedisUtil.getHashMap("uid"+ui,REDIS_DB);
			String userInfo = hashMap.get("baseInfo");
			if (!StringUtils.isNullOrEmpty(userInfo)) {
				User infoUser = getUserFromUserInfo(userInfo, ui+"");
				if (infoUser != null) {
					user = infoUser;
					user.setRoomId(hashMap.get("roomId"));
				}
			} else {
				user = new User();
				user.setId(ui);
				user = userDao.findUser2(user);
			}
			if (user != null) {
				session.channel().attr(AttributeKey.<User>valueOf("user")).set(user);
				user.setIoSession(session);
				dateService.addLoginUser();
			}
		}
		if (user == null) {
			// session.close();
			return;
		}
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(user.getRoomId());
		replaceUserIoSession(user, oneRoom);
		JSONObject roomInfo = getRoomInfo(user);// 得到房间信息
		if (roomInfo == null) {// 游戏还没开始
			// 得到房间里的用户信息
			JSONObject outJsonObject = new JSONObject();
			outJsonObject.put("method", "enterRoom");
			outJsonObject.put("code", "success");
			getRoomInfo(outJsonObject, oneRoom);
			session.write(outJsonObject.toString());
		} else {
			nofiyUserRoomInfo(roomInfo, user);
		}
	}

	@Override
	public void downGameInfo(JSONObject jsonObject, ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		JSONObject roomInfo = getRoomInfo(user);
		if (roomInfo == null) {
			// 修改玩家的房间号
			User modifyUser = new User();
			modifyUser.setId(user.getId());
			modifyUser.setRoomId("0");
			userDao.modifyUser(user.getId(),"0");// 记录下用户的房间号
			logger.info("在房间信息为空的时候修改用户的房间号:" + user.getRoomId());
			user.setRoomId("0");// 用户的ID置空
			return;
		}
		nofiyUserRoomInfo(roomInfo, user);
		// Game game = GameManager.getGameWithRoomNumber(user.getRoomId());
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(user.getRoomId());
		replaceUserIoSession(user, oneRoom);
	}

	@Override
	public void disbandRoom(User user) {
		disband(user.getIoSession(), user);
	}

}
