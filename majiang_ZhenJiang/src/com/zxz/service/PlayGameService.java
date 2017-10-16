package com.zxz.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.jdbc.StringUtils;
import com.zxz.controller.GameManager;
import com.zxz.controller.RoomManager;
import com.zxz.dao.SumScoreDao;
import com.zxz.dao.UserScoreDao;
import com.zxz.domain.Game;
import com.zxz.domain.GangCard;
import com.zxz.domain.OneRoom;
import com.zxz.domain.PengCard;
import com.zxz.domain.Score;
import com.zxz.domain.ScoreUser;
import com.zxz.domain.SumScore;
import com.zxz.domain.User;
import com.zxz.redis.RedisUtil;
import com.zxz.utils.CardsMap;
import com.zxz.utils.Constant;
import com.zxz.utils.HuPai;
import com.zxz.utils.MathUtil;
import com.zxz.utils.NotifyTool;
import com.zxz.utils.RecordScoreThread;
import com.zxz.utils.RecordScoreThreadPool;
import com.zxz.utils.ScoreType;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class PlayGameService extends BaseService implements Constant{

	private static Logger logger = Logger.getLogger(PlayGameService.class);  
	static UserScoreDao userScoreDao = UserScoreDao.getInstance();
	static SumScoreDao sumScoreDao = SumScoreDao.getInstance();
	
	public void playGame(JSONObject jsonObject, ChannelHandlerContext session) {
		String type = jsonObject.getString("type");//出牌，杠牌，碰牌,胡牌
		if(type.equals("chupai")){//出牌
			chuPai(jsonObject, session);
		}else if(type.equals("peng")){//碰牌
			peng(jsonObject,session);
		}else if(type.equals("gang")){//杠牌
			gang(jsonObject,session);
		}else if(type.equals("fangqi")){//不碰也不杠
			fangqi(jsonObject,session);
		}else if(type.equals("gongGang")){//公杠  也称 明杠
			gongGang(jsonObject,session);
		}else if(type.equals("anGang")){//暗杠
			anGang(jsonObject,session);
		}else if(type.equals("huPai")){//胡牌
			huPai(jsonObject,session);
		}
	}
	
	/**用户选择胡牌
	 * @param jsonObject
	 * @param session
	 */
	private void huPai(JSONObject jsonObject, ChannelHandlerContext session) {
		Game game = getGame(session);
		User sessionUser = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		User gamingUser = game.getGamingUser(sessionUser.getDirection());
		boolean ziMo = gamingUser.isZiMo();
		if(ziMo){
			Integer myGrabCard = gamingUser.getMyGrabCard();//最后抓的那一张牌
			gamingUser.addZiMoTotal();//修改自摸的
			userWinNotAuto(game,myGrabCard,gamingUser,ziMo);
		}else{
			game.getFangPaoUser().setFangPao(true);
			game.getFangPaoUser().addFangChongTotal();//添加放炮的次数
			Integer autoHuCardId = game.getAutoHuCardId();
			List<Integer> cards = gamingUser.getCards();
			cards.add(autoHuCardId);
			Collections.sort(cards);
			userWinNotAuto(game,autoHuCardId,gamingUser,ziMo);
		}
	}

	/**暗杠
	 * @param jsonObject
	 * @param session
	 */
	private void anGang(JSONObject jsonObject, ChannelHandlerContext session) {
		Game game = getGame(session);
		Integer cardId = jsonObject.getInt("cardId");
		User sessionUser = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		User user = game.getUserInRoomList(sessionUser.getId());
		List<Integer> cards = user.getCards();
		List<Integer> gangCards = getGangList(cards, cardId);//杠的牌
		if(gangCards.size()<4){
			if(gangCards.size()!=3&&gangCards.get(0)/4!=game.getFanpai()/4){
				notifyUserError(session, "异常暗杠");
				return;
			}
		}
		user.setZiMo(false);
		//gangCards.add(cardId);
		user.userGangCards(gangCards);
		//记录玩家杠的牌
		user.recordUserGangCards(1, gangCards);
		notifyAllUserAnGang(game, gangCards,user);//通知所有的玩家杠的牌 
		modifyUserScoreForAnGang(game, user);//修改玩家暗杠得分
		notifyAllUserCurrentScore(game);//通知用户现在的成绩
		//记录玩家公杠
		HuiFangUitl.getAnGang(game.getHuiFang(), user, gangCards);
		if(gangCards.size()==4){
			//该玩家在抓一张牌 
			userDrawCard(game, user.getDirection(),true);
		}else{
			game.setDirec(user.getDirection());//把当前出牌的方向改变
			game.setGameStatus(GAGME_STATUS_OF_CHUPAI);//设置成出牌的状态
		}
	}

	
	/**修改玩家暗杠得分 
	 * @param game
	 * @param user //暗杠的用户
	 */
	public static void modifyUserScoreForAnGang(Game game, User user) {
		List<User> userList = game.getUserList();
		int totalScore = 0;
		for(int i=0;i<userList.size();i++){
			User u = userList.get(i);
			if(u.getId()!=user.getId()){//非杠牌的玩家减分
				int wanFa = game.getWanFa();
				int reduceScoreForAnGang = u.reduceScoreForAnGang(wanFa);
				totalScore =totalScore+ reduceScoreForAnGang;
			}
		}
		User anGangUser = game.getUserInRoomList(user.getId());
		anGangUser.addScoreForAnGang(totalScore);
	}

	/**公杠
	 * @param jsonObject
	 * @param session
	 */
	private void gongGang(JSONObject jsonObject, ChannelHandlerContext session) {
		int cardId = jsonObject.getInt("cardId");//得到的那张牌
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		Game game = getGame(session);
		User gamingUser = getGamingUser(game, user.getDirection());
		List<Integer> pengCards = gamingUser.getUserPengCardsId();
		List<Integer> removeList = getRemoveList(cardId, pengCards);
		if(removeList.size()<3){
			notifyUserError(session, "异常公杠");
			return;
		}
		//抢杠
		String direction = gamingUser.getDirection();//得到当前的座次
		User qiangGangUser = isOtherUserQiangGang(cardId, game, direction,gamingUser);
		if(qiangGangUser!=null){
			game.setQiangGangId(cardId);
			game.setWaitHuStatus(GAGME_STATUS_OF_WAIT_HU_NEW_WAIT_QIANG_GANG);
			game.setWaitDirection(gamingUser.getDirection());
			notifyUserQiangGangHu(game, qiangGangUser, cardId);
			return;
		}
		List<PengCard> pengs = gamingUser.getPengCards();
		String gongGangDirection = "";
		for(int i=0;i<pengs.size();i++){
			PengCard pengCard = pengs.get(i);
			List<Integer> cards = pengCard.getCards();
			if(cards.get(0)/4==removeList.get(0)/4){
				pengs.remove(pengCard);
				gongGangDirection = pengCard.getChuDir();
				break;
			}
		}
		gamingUser.setZiMo(false);
		removeList.add(cardId);
		//从自己的牌中移除公杠的那张牌
		gamingUser.removeCardFromGongGang(cardId);
		//记录玩家杠的牌
		gamingUser.recordUserGangCards(2, removeList);
		notifyAllUserGongGang(game, removeList,user);//通知所有的玩家杠的牌 
		modifyUserScoreForGongGang(game, gamingUser,gongGangDirection);//修改玩家公杠得分
		notifyAllUserCurrentScore(game);//通知用户现在的成绩
		//记录玩家公杠的牌
		HuiFangUitl.getGongGang(game.getHuiFang(), gamingUser, removeList);
		//该玩家在抓一张牌 
		userDrawCard(game, user.getDirection(),true);
	}
	
	
	/**通知用户可以胡牌
	 * @param game
	 * @param huUser
	 * @param cardId 别人出的那张牌
	 * @param fangPengUser 放碰的用户
	 */
	private static void notifyUserQiangGangHu( Game game,User huUser,int cardId) {
		game.setGameStatus(GAGME_STATUS_OF_WAIT_HU_NEW);
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("huUser", huUser.getDirection());
		outJsonObject.put(discription, "该用户可以胡牌");
		outJsonObject.put(method, "canHu");//可以杠
		outJsonObject.put(type, 1);//
		String nowDirection = game.getDirec();
		game.setBeforeTingOrGangOrHuDirection(nowDirection);//设置原来的方向
		game.setDirec(huUser.getDirection());//出牌的方向改变
		huUser.getIoSession().write(outJsonObject);//通知该用户可以胡牌
	}
	
	
	/**通知用户可以胡牌
	 * @param game
	 * @param huUser
	 * @param huType 0 自摸 1放炮
	 * @param cardId 别人出的那张牌
	 * @param fangPengUser 放碰的用户
	 */
	private static void notifyUserCanHu( Game game,User huUser,int huType,int cardId,boolean changeBeforDir,boolean canPeng,boolean canGang) {
		game.setGameStatus(GAGME_STATUS_OF_WAIT_HU_NEW);
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("huUser", huUser.getDirection());
		outJsonObject.put(discription, "该用户可以胡牌");
		outJsonObject.put(method, "canHu");//可以杠
		outJsonObject.put(type, huType);//
		if(huType==1){//判断胡牌的人是否可以碰牌或杠牌
			outJsonObject.put("isPeng", canPeng);
			outJsonObject.put("isGang", canGang);
		}
		String nowDirection = game.getDirec();
		if(changeBeforDir){
			game.setBeforeTingOrGangOrHuDirection(nowDirection);//设置原来的方向
		}
		game.setDirec(huUser.getDirection());//出牌的方向改变
		huUser.getIoSession().write(outJsonObject);//通知该用户可以胡牌
	}
	
	
	
	
	/**得到需要从碰的牌中移除的集合
	 * @param card
	 * @param pengCards
	 * @return
	 */
	public static List<Integer> getRemoveList(int card,List<Integer> pengCards){
		List<Integer> list = new ArrayList<>();
		for(int i=0;i<pengCards.size();i++){
			Integer pengCard = pengCards.get(i);
			if(card/4 == pengCard/4){
				list.add(pengCard);
			}
		}
		return list;
	}
	
	
	/**公杠的用户得分
	 * @param game
	 * @param user
	 */
	public static void modifyUserScoreForJieGang(Game game, User user) {
		OneRoom room = game.getRoom();
		int chong = room.getChong();
		int wanFa = game.getWanFa();
		if(chong==2){//陪冲
			//其它的三个玩家减1分
			List<User> userList = game.getUserList();
			int totalScore = 0;
			for (int i = 0; i < userList.size(); i++) {
				User u = userList.get(i);
				if(u.getId()!=user.getId()){//非杠牌用户减分
					int reduceScoreForMingGang = u.reduceScoreForMingGang(wanFa);
					totalScore = totalScore + reduceScoreForMingGang;
				}
			}
			user.addScoreForMingGang(totalScore);
		}else{
			User fangGangUser = game.getFangGangUser();
			int totalUser = game.getUserList().size()-1;
			int fenshu =  Math.abs(ScoreType.REDUCE_SCORE_FOR_MINGGANG) *totalUser;
			fangGangUser.reduceScoreForFengShu(wanFa, fenshu);
			user.addScoreForMingGang(fenshu);
		}
	}
	
	/**公杠的用户得分
	 * @param game
	 * @param user
	 */
	public static void modifyUserScoreForGongGangAndFangGang(Game game, User user) {
		//其它的三个玩家减1分
		List<User> userList = game.getUserList();
		int totalScore = 0;
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			if(u.getId()!=user.getId()){//非杠牌用户减分
				int wanFa = game.getWanFa();
				int reduceScoreForMingGang = u.reduceScoreForMingGang(wanFa);
				totalScore = totalScore + reduceScoreForMingGang;
			}
		}
		user.addScoreForMingGang(totalScore);
	}
	
	
	/**公杠的用户得分
	 * @param game
	 * @param user
	 */
	public static void modifyUserScoreForGongGang(Game game, User user,String gongGangDirection) {
		OneRoom room = game.getRoom();
		int chong = room.getChong();
		if(chong==2){//陪冲
			//其它的三个玩家减1分
			List<User> userList = game.getUserList();
			int totalScore = 0;
			for (int i = 0; i < userList.size(); i++) {
				User u = userList.get(i);
				if(u.getId()!=user.getId()){//非杠牌用户减分
					int wanFa = game.getWanFa();
					int reduceScoreForMingGang = u.reduceScoreForMingGang(wanFa);
					totalScore = totalScore + reduceScoreForMingGang;
				}
			}
			user.addScoreForMingGang(totalScore);
		}else{//包冲
			User fangGangUser = getGamingUser(game, gongGangDirection);
			int totalUser = game.getUserList().size()-1;
			int fenshu = Math.abs(ScoreType.REDUCE_SCORE_FOR_MINGGANG)*totalUser;
			int wanFa = game.getWanFa();
			fangGangUser.reduceScoreForFengShu(wanFa, fenshu);
			user.addScoreForMingGang(fenshu);
		}
	}

	/**不碰也不杠
	 * @param jsonObject
	 * @param session
	 */
	private void fangqi(JSONObject jsonObject, ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		Game game = GameManager.getGameWithRoomNumber(user.getRoomId());
		User gamingUser = getGamingUser(game, user.getDirection());
		setUserGiveUp(gamingUser);//用户放弃出牌
		int status = game.getGameStatus();
		if(status == GAGME_STATUS_OF_WAIT_HU_NEW){//如果是放炮胡
			doWithHuFangQi(user, game, gamingUser);
		}else{//如果是等待听牌胡杠牌
			//如果是接杠和碰的放弃,把牌放入到原来的用户里面
			if(status == GAGME_STATUS_OF_PENGPAI){
				Integer autoPengCardId = game.getAutoPengCardId();//自动碰牌的集合 
				game.getFangPengUser().getMyPlays().add(autoPengCardId);
				List<Integer> canNotPengList = gamingUser.getCanNotPengList();
				canNotPengList.add(autoPengCardId);
			}else if(status == GAGME_STATUS_OF_GANGPAI){
				Integer autoGangCardId = game.getAutoGangCardId();
				game.getFangGangUser().getMyPlays().add(autoGangCardId);
			}
			String beforeTingOrGangDirection = game.getBeforeTingOrGangOrHuDirection();
			String direction = getNextDirection(beforeTingOrGangDirection);
			notifyUserDirectionChange(user, direction);
			userDrawCard(game, direction,false);
		}
	}

	
	/**用户胡牌的时候放弃
	 * @param user
	 * @param game
	 * @param gamingUser
	 */
	private void doWithHuFangQi(User user, Game game, User gamingUser) {
		User fangPaoUser = game.getFangPaoUser();
		Integer autoHuCardId = game.getAutoHuCardId();
		String endDriection = fangPaoUser.getDirection();
		String canHuDirection = nextUserIsHu(game, gamingUser, autoHuCardId, endDriection);
		if(!StringUtils.isNullOrEmpty(canHuDirection)){//下一个人是否还可以胡 ,如果还可以胡
			nextUserHu(game, autoHuCardId, canHuDirection);
			return;
		}else{
			int waitHuStatus = game.getWaitHuStatus();
			if(waitHuStatus==GAGME_STATUS_OF_WAIT_HU_NEW_WAIT_PENG){//等待碰牌
				userHuButPeng(game, autoHuCardId);
				return;
			}else if(waitHuStatus==GAGME_STATUS_OF_WAIT_HU_NEW_WAIT_GANG){//等待杠牌
				userHuButGang(game, autoHuCardId);
				return;
			}else if(waitHuStatus==GAGME_STATUS_OF_WAIT_HU_NEW_WAIT_QIANG_GANG){
				userHuButQiangGang(game);
				return;
			}else{//下一个人抓牌
				game.setFangPaoUser(null);
				//判断其余的人是否可以碰牌或杠牌
				User canPengOrGangUser = getPengOrGangCardUser(autoHuCardId, game.getSeatMap(), fangPaoUser.getId(), game.getBaida(), fangPaoUser.getChuPaiCiShu());
				if(canPengOrGangUser!=null){
					//可以听牌或杠牌,该用户也没有托管才通知它
					boolean auto = canPengOrGangUser.isAuto();//用户是否托管
					if(auto){//如果托管自动碰
						List<Integer> cards = canPengOrGangUser.getCards();
						boolean isTing = canPengOrGangUser.isUserTingPaiOfPengOrGang(cards,game.getBaida());//用户是否听牌
						if(isTing){//如果该用户已经听牌
							nextUserDrawCards(autoHuCardId, fangPaoUser, game);
						}else{
							notifyUserCanPengOrGang(autoHuCardId, fangPaoUser, game, canPengOrGangUser);
							autoPengOrGang(canPengOrGangUser,game);
						}
						return;
					}else{
						notifyUserCanPengOrGang(autoHuCardId, fangPaoUser, game, canPengOrGangUser);
						return;
					}
				}else{//下一个人抓牌
					fangPaoUser.getMyPlays().add(autoHuCardId);
					fangPaoUser.setFangPao(false);
					String beforeTingOrGangDirection = game.getBeforeTingOrGangOrHuDirection();
					String direction = getNextDirection(beforeTingOrGangDirection);
					notifyUserDirectionChange(user, direction);
					userDrawCard(game, direction,false);
					return;
				}
			}
		}
	}

	
	
	
	
	/**自己不胡，通知下一个人胡
	 * @param game
	 * @param autoHuCardId
	 * @param canHuDirection
	 */
	private void nextUserHu(Game game, Integer autoHuCardId, String canHuDirection) {
		User huUser = game.getGamingUser(canHuDirection);
		int canPengOrCanGang = UserService.isCanPengOrCanGang(autoHuCardId, huUser.getCards());
		boolean isCanGang = false;
		boolean isCanPeng = false;
		if(canPengOrCanGang==2){
			huUser.setCanPeng(true);
			huUser.setCanGang(true);
			isCanGang = true;
			isCanPeng = true;
		}else if(canPengOrCanGang==1){
			huUser.setCanPeng(true);
			isCanPeng = true;
		}
		notifyUserCanHu(game, huUser, 1,autoHuCardId,false,isCanPeng,isCanGang);
	}

	
	
	

	
	
	
	/**用户放弃胡牌，上一个选择杠牌的用户杠牌
	 * @param game
	 * @param autoHuCardId
	 */
	private void userHuButQiangGang(Game game) {
		int qiangGangId = game.getQiangGangId();
		String waitDirection = game.getWaitDirection();
		User gamingUser = getGamingUser(game, waitDirection);
		List<Integer> pengCards = gamingUser.getUserPengCardsId();
		List<Integer> removeList = getRemoveList(qiangGangId, pengCards);
		if(removeList.size()<3){
			game.setWaitHuStatus(0);
			game.setWaitDirection("");
			return;
		}
		//抢杠
		String direction = gamingUser.getDirection();//得到当前的座次
		List<PengCard> pengs = gamingUser.getPengCards();
		for(int i=0;i<pengs.size();i++){
			PengCard pengCard = pengs.get(i);
			List<Integer> cards = pengCard.getCards();
			if(cards.get(0)/4==removeList.get(0)/4){
				pengs.remove(pengCard);
				break;
			}
		}
		gamingUser.setZiMo(false);
		removeList.add(qiangGangId);
		//从自己的牌中移除公杠的那张牌
		gamingUser.removeCardFromGongGang(qiangGangId);
		//记录玩家杠的牌
		gamingUser.recordUserGangCards(2, removeList);
		notifyAllUserGongGang(game, removeList,gamingUser);//通知所有的玩家杠的牌 
		modifyUserScoreForGongGangAndFangGang(game, gamingUser);//修改玩家公杠得分
		notifyAllUserCurrentScore(game);//通知用户现在的成绩
		//记录玩家公杠的牌
		HuiFangUitl.getGongGang(game.getHuiFang(), gamingUser, removeList);
		//该玩家在抓一张牌 
		userDrawCard(game, gamingUser.getDirection(),true);
		game.setWaitHuStatus(0);
		game.setWaitDirection("");
	}
	
	
	
	
	
	
	
	
	/**用户放弃胡牌，上一个选择杠牌的用户杠牌
	 * @param game
	 * @param autoHuCardId
	 */
	private void userHuButGang(Game game, Integer autoHuCardId) {
		String waitDirection = game.getWaitDirection();
		User waitGangUser = game.getGamingUser(waitDirection);
		List<Integer> cards = waitGangUser.getCards();
		List<Integer> gangCards = getGangList(cards, autoHuCardId);//杠的牌
		gangCards.add(autoHuCardId);
		waitGangUser.userGangCards(gangCards);
		
		//记录玩家杠的牌
		waitGangUser.recordUserGangCards(0, gangCards);
//		modifyUserScoreForGang(game, u);//修改玩家得分
		modifyUserScoreForGongGangAndFangGang(game, waitGangUser);
		notifyAllUserGang(game, gangCards,waitGangUser);//通知所有的玩家杠的牌
		notifyAllUserCurrentScore(game);//通知用户现在的成绩
		//记录玩家杠的牌
		HuiFangUitl.getGangPai(game.getHuiFang(), waitGangUser, gangCards);
		//该玩家在抓一张牌 
		userDrawCard(game, waitGangUser.getDirection(),true);
	}

	
	
	
	/**用户放弃胡牌，上一个选择碰牌的用户碰牌
	 * @param game
	 * @param autoHuCardId
	 */
	private void userHuButPeng(Game game, Integer autoHuCardId) {
		String waitDirection = game.getWaitDirection();
		User waitPengUser = game.getGamingUser(waitDirection);
		List<Integer> cards = waitPengUser.getCards();
		List<Integer> pengList = getPengList(cards, autoHuCardId);//得到可以碰的集合
		waitPengUser.userPengCards(pengList);//玩家碰牌
		pengList.add(autoHuCardId);
		waitPengUser.addUserPengCards(pengList,game.getFangPengUser().getDirection());//用户添加碰出的牌
		waitPengUser.setUserCanPlay(true);//该玩家可以出牌
		game.setGameStatus(GAGME_STATUS_OF_CHUPAI);//游戏的状态变为出牌
		game.setDirec(waitPengUser.getDirection());
		waitPengUser.setLastChuPaiDate(new Date());
		//记录玩家碰的牌
		HuiFangUitl.getPengPai(game.getHuiFang(), waitPengUser, pengList);
		notifyAllUserPeng(game, pengList,waitPengUser);
		waitPengUser.setCanSanDa(true);
		game.setWaitHuStatus(0);
		game.setWaitDirection("");
	}

	
	
	 
	
	/**下一个是否胡牌
	 * @param game
	 * @param gamingUser
	 * @param autoHuCardId
	 * @param endDriection
	 */
	private String nextUserIsHu(Game game, User gamingUser, Integer autoHuCardId, String endDriection) {
		String fangQiDriection = gamingUser.getDirection();
		String nextDirection = getNextDirection(fangQiDriection);
		User canHuUser = game.getSeatMap().get(nextDirection);
		List<Integer> cards = canHuUser.getCards();
		List<Integer> newCards = HuPai.getNewListFromOldList(cards);
		newCards.add(autoHuCardId);
		Collections.sort(newCards);
		HuPai huPai = new HuPai();
		String direction = "";
		while(!nextDirection.equals(endDriection)){
			boolean hu = huPai.isHu(newCards, 200);
			if(hu){//是否胡牌
				boolean diaoJiang = huPai.isDiaoJiang();
				if(diaoJiang){
					direction =  canHuUser.getDirection();
				}
				game.setCanHuUser(canHuUser);
				game.setAutoHuCardId(autoHuCardId);
				direction = canHuUser.getDirection();
				break;
			}else{
				nextDirection = getNextDirection(nextDirection);
			    canHuUser = game.getSeatMap().get(nextDirection);
			    cards = canHuUser.getCards();
			    newCards = HuPai.getNewListFromOldList(cards);
			    newCards.add(autoHuCardId);
			    Collections.sort(newCards);
			}
		}
		
		return direction;
		
	}
	
	
	
	/**用户放弃出牌
	 * @param user
	 */
	private void setUserGiveUp(User user){
		user.setCanGang(false);
		user.setCanPeng(false);
		user.setZiMo(false);
	}
	
	
	/**杠牌
	 * @param jsonObject
	 * @param session
	 */
	private void gang(JSONObject jsonObject, ChannelHandlerContext session) {
		int cardId = jsonObject.getInt("cardId");
		Game game = getGame(session);
		User sessionUser = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		User user = getGamingUser(game, sessionUser.getDirection());//game 中 oneRoom中 的user 得到游戏中的玩家
		boolean isUserCanGang = checkUserIsCanGang(user);
		if(!isUserCanGang){
			notifyUserError(session, "不可接杠");
			return;
		}
		user.setUserCanPlay(true);
		Map<String, User> seatMap = game.getSeatMap();
		User u = seatMap.get(user.getDirection());
		List<Integer> cards = u.getCards();
		List<Integer> gangCards = getGangList(cards, cardId);//杠的牌
		gangCards.add(cardId);
		if(gangCards.size()!=4){
			notifyUserError(session, "接杠异常");
			return;
		}
		u.userGangCards(gangCards);
		int status = game.getGameStatus();
		if(status == GAGME_STATUS_OF_WAIT_HU_NEW){//如果是自摸胡
			User fangPaoUser = game.getFangPaoUser();
			Integer autoHuCardId = game.getAutoHuCardId();
			String endDriection = fangPaoUser.getDirection();
			String canHuDirection = nextUserIsHu(game, user, autoHuCardId, endDriection);
			if(!StringUtils.isNullOrEmpty(canHuDirection)){//下一个人是否还可以胡 
				nextUserHu(game, autoHuCardId, canHuDirection);
				game.setWaitHuStatus(GAGME_STATUS_OF_WAIT_HU_NEW_WAIT_GANG);
				game.setWaitDirection(user.getDirection());
				return;
			}
		}
		user.setZiMo(false);
		//记录玩家杠的牌
		u.recordUserGangCards(0, gangCards);
		if(gangCards.get(0)/4==game.getBaida()/4){//如果杠的是首搭移除掉，放炮人移除掉首搭的那张牌 
			List<Integer> shouDaList = game.getFangGangUser().getShoDaList();
			shouDaList.remove(new Integer(cardId));
			//杠的人首搭增加
			List<Integer> gameUserShouDaList = user.getShoDaList();
			for(int i=0;i<gangCards.size();i++){
				gameUserShouDaList.add(gangCards.get(i));
			}
		}
		modifyUserScoreForJieGang(game, user);
		notifyAllUserGang(game, gangCards,user);//通知所有的玩家杠的牌
		notifyAllUserCurrentScore(game);//通知用户现在的成绩
		//记录玩家杠的牌
		HuiFangUitl.getGangPai(game.getHuiFang(), u, gangCards);
		//该玩家在抓一张牌 
		userDrawCard(game, user.getDirection(),true);
	}
	
	
	/**通知用户现在的成绩
	 * @param game
	 */
	private static void notifyAllUserCurrentScore(Game game) {
		JSONObject jsonObject =  new JSONObject();
		jsonObject.put("method", "changeChip");
		JSONArray userArray =  new JSONArray();
		List<User> userList = game.getUserList();
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			JSONObject userJson =  new JSONObject();
			userJson.put("userId", user.getId());
			userJson.put("userDirection", user.getDirection());
			userJson.put("userChip", user.getCurrentScore());
			userArray.put(userJson);
		}
		jsonObject.put("changeUsersChip", userArray);
		NotifyTool.notifyIoSessionList(game.getIoSessionList(), jsonObject);
	}

//	/**得到游戏中的玩家
//	 * @param userId
//	 * @param roomId
//	 */
//	public static User getGamingUser(int userId,String roomId){
//		Game game = GameManager.getGameWithRoomNumber(roomId);
//		List<User> userList = game.getRoom().getUserList();
//		for(int i=0;i<userList.size();i++){
//			User user = userList.get(i);
//			if(user.getId()==userId){
//				return user; 
//			}
//		}
//		logger.fatal("未找到游戏中的人................");
//		return null;
//	}
	/**得到游戏中的玩家
	 * @param userId
	 * @param roomId
	 */
	public static User getGamingUser(Game game,String direction){
		User user = game.getSeatMap().get(direction);
		return user;
	}
	
	
	/**抓牌
	 * @param game
	 * @param direction
	 * @param isGang 是否可以杠上开花
	 */
	public static void userDrawCard(Game game,String direction,boolean isGang){
		List<Integer> remainCards = game.getRemainCards();
		int lastIndex  =remainCards.size()-1;
		//通知该玩家抓到的牌
//		Integer removeCard = 35;
		Integer removeCard = remainCards.remove(lastIndex);
		//System.out.println("当前的牌还有:"+remainCards);
		User user = game.getSeatMap().get(direction);
		game.setDirec(direction);//把当前出牌的方向改变
		game.setGameStatus(GAGME_STATUS_OF_CHUPAI);//设置成出牌的状态
		HuPai huPai = user.zhuaPai(removeCard,game.getBaida());//抓牌 
		boolean isWin = huPai.isHu();//抓牌 
		//胡牌的时候
		//1.如果用户托管的时候用户直接胡牌
		//2.如果用户没有托管，判断用户是否可以公杠和暗杠，如果可以公杠和暗杠给出用户：【杠】【胡】的提示
		if(!isWin){//没有赢牌
			userNotWin(game, remainCards, removeCard, user);
		}else{//赢牌
			game.setCanHuUser(user);
			user.setZiMo(true);//自摸胡牌
			if(user.isAuto()){
				if(isGang){
					user.setGangKai(true);
				}
				userWin(game,removeCard,user,false);
			}else{//
				List<Integer> gongGangCards = analysisUserIsCanGongGangEveryTime(user);
				if(gongGangCards.size()>0){//如果可以公杠
					game.setGameStatus(GAGME_STATUS_OF_WAIT_HU);//等待胡牌
					game.setCanHuUser(user);
					game.setIsCanGangType(0);
					user.setGangKai(true);
					notifyUserWaitGangAndHu(removeCard, user, null, 0);
				}else{
					List<Integer> anGangCards = isUserCanAnGang(user,game.getBaida(),game.getFanpai());
					if(anGangCards.size()>0){//可以暗杠
						game.setCanHuUser(user);
						game.setGameStatus(GAGME_STATUS_OF_WAIT_HU);//等待胡牌
						game.setIsCanGangType(1);
						game.setAnGangCards(anGangCards);
						user.setGangKai(true);
						notifyUserWaitGangAndHu(removeCard, user, anGangCards, 1);
					}else{//不可以公杠也不可以暗杠
						notifyUserDrawDirection(removeCard, user,null,-1);//通知抓牌的方向
						notifyUserCanHu(game, user, 0,0,true,false,false);
					}
				}
			}
		}
	}
	
	
	
	
	/**通知抓牌的方向
	 * @param removeCard 抓的牌是什么
	 * @param nextUser 抓牌的玩家
	 * @param cards 暗杠或者公杠的牌
	 * @param type 0、公杠 1、暗杠
	 */
	private static void notifyUserWaitGangAndHu(Integer removeCard, User nextUser, List<Integer> cards,int type) {
		Game game = getGame(nextUser);
		List<User> userList = getUserListWithGame(game);
		for(User  user : userList){
			JSONObject outJsonObject = new JSONObject();
			//通知他抓到的牌
			outJsonObject.put("description", "抓牌的方向");
			outJsonObject.put("type", "zhuapai");
			outJsonObject.put(method, "playGame");
			outJsonObject.put("direction", nextUser.getDirection());
			if(user.getId()==nextUser.getId()){
				outJsonObject.put("getCardId", removeCard);
				outJsonObject.put("isCanHu", true);
				outJsonObject.put("isCanGangType", type);
				if(type==0){
					int gangCard = nextUser.getGangCard();
					outJsonObject.put("gangCard", gangCard);
				}else if(type==1){
					outJsonObject.put("cards", cards);
				}
			}else{
				outJsonObject.put("getCardId", 1000);//返回一个不存在的数
			}
			NotifyTool.notify(user.getIoSession(), outJsonObject);
		}
	}
	
	

	/**用户没有赢牌
	 * @param game
	 * @param remainCards
	 * @param removeCard
	 * @param user
	 */
	private static void userNotWin(Game game, List<Integer> remainCards, Integer removeCard, User user) {
		if(chouZhuang(remainCards,game)){
			logger.info("臭庄");
			afterChouZhuang(game, user,removeCard);//臭庄之后处理
			return;
		}
		//分析用户是否可以公杠
		user.setUserCanPlay(true);//该用户可以打牌
		List<Integer> gongGangCards = analysisUserIsCanGongGangEveryTime(user);
		if(gongGangCards.size()>0){
			if(user.isAuto()){//如果该玩家托管,首先通知玩家抓到的牌,然后自动帮他公杠掉
				notifyUserDrawDirection(removeCard, user,gongGangCards,0);//通知抓牌的方向
				game.setCanGongGangUser(user);
				game.setGongGangCardId(removeCard);
				autoGongGang(game);
			}else{//如果没有托管
				noticeUserCanGongGang(game, removeCard, user, gongGangCards);//通知用户可以公杠
			}
		}else{
			//分析该用户是否可以暗杠
			List<Integer> anGangCards = isUserCanAnGang(user,game.getBaida(),game.getFanpai());
			if(anGangCards.size()>0){//设置出牌的状态为暗杠
				game.setAnGangCards(anGangCards);
				game.setCanAnGangUser(user);
				if(user.isAuto()){
					notifyUserDrawDirection(removeCard, user,anGangCards,1);//通知抓牌的方向
					autoAnGang(game);
				}else{
					game.setGameStatus(GAGME_STATUS_OF_ANGANG);//暗杠
					notifyUserDrawDirection(removeCard, user,anGangCards,1);//通知抓牌的方向
				}
			}else{//玩家不可以暗杠
				notifyUserDrawDirection(removeCard, user,null,-1);//通知抓牌的方向
				if(user.isAuto()){
					autoChuPai(game);//自动出牌
				}
			}
		}
	}
	
	/**自动暗杠
	 * @param game
	 * @param user
	 * @param anGangCards
	 */
	public static  void autoAnGang(Game game) {
		User canAnGangUser = game.getCanAnGangUser();
		List<Integer> anGangCards = game.getAnGangCards();
		canAnGangUser.userGangCards(anGangCards);
		//记录玩家暗杠的牌
		HuiFangUitl.getAnGang(game.getHuiFang(), canAnGangUser, anGangCards);
		//logger.info("自动出牌...暗杠.................:"+NewAI.showPai(anGangCards));
		canAnGangUser.recordUserGangCards(1, anGangCards);
		PlayGameService.notifyAllUserAnGang(game, anGangCards,canAnGangUser);//通知所有的玩家杠的牌 
		PlayGameService.modifyUserScoreForAnGang(game, canAnGangUser);//修改玩家暗杠得分
		notifyAllUserCurrentScore(game);//通知用户现在的成绩
		if(anGangCards.size()==4){//暗杠
			//该玩家在抓一张牌 
			PlayGameService.userDrawCard(game, canAnGangUser.getDirection(),true);
		}else{//翻牌杠,在出牌
			autoChuPai(game);
		}
	}

	/**托管自动公杠
	 * @param game
	 */
	public static  void autoGongGang(Game game){
		User user = game.getCanGongGangUser();
		Integer cardId = game.getGongGangCardId();
		List<Integer> pengCards = user.getUserPengCardsId();//用户碰的牌
		List<Integer> removeList = PlayGameService.getRemoveList(cardId, pengCards);
		List<PengCard> pengs = user.getPengCards();
		String gongGangDirection = "";
		for(int i=0;i<pengs.size();i++){
			PengCard pengCard = pengs.get(i);
			List<Integer> cards = pengCard.getCards();
			if(cards.get(0)/4==removeList.get(0)/4){
				pengs.remove(pengCard);
				gongGangDirection = pengCard.getChuDir();
				break;
			}
		}
		removeList.add(cardId);
		//logger.info("托管自动公杠....................:"+NewAI.showPai(removeList));
		//从自己的牌中移除公杠的那张牌
		user.removeCardFromGongGang(cardId);
		//记录玩家杠的牌
		user.recordUserGangCards(2, removeList);
		PlayGameService.notifyAllUserGongGang(game, removeList,user);//通知所有的玩家杠的牌 
		PlayGameService.modifyUserScoreForGongGang(game, user,gongGangDirection);//修改玩家公杠得分
		notifyAllUserCurrentScore(game);//通知用户现在的成绩
		HuiFangUitl.getGongGang(game.getHuiFang(), user, removeList);
		//该玩家在抓一张牌 
		userDrawCard(game, user.getDirection(),true);
	}
	
	/**通知用户可以公杠
	 * @param game
	 * @param removeCard
	 * @param user
	 * @param gongGangCards
	 */
	private static void noticeUserCanGongGang(Game game, Integer removeCard, User user, List<Integer> gongGangCards) {
		game.setGameStatus(GAGME_STATUS_OF_GONG_GANG);//公杠
		game.setGongGangCardId(removeCard);
		game.setCanGongGangUser(user);//可以公杠的玩家
		notifyUserDrawDirection(removeCard, user,gongGangCards,0);//通知抓牌的方向
	}
	
	
	/**
	 * 在本局臭庄之后，计算用户的得分，依然把最后的结算发送给用户
	 */
	public static void afterChouZhuang(Game game,User lastGetCardUser,int removeCard){
		//依然通知玩家抓的牌是什么
		notifyUserDrawDirection(removeCard, lastGetCardUser,null,-1);//通知抓牌的方向
		OneRoom room = game.getRoom();
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("type", "hupai");
		outJsonObject.put("isChouZhuang", true);
		outJsonObject.put("description", "臭庄了");
		outJsonObject.put("method", "playGame");
		JSONArray userJsonArray = getUserJSONArray(room);
		List<Integer> remainCards = game.getRemainCards();
		outJsonObject.put("remainCards", remainCards);//剩余的牌
		outJsonObject.put("users", userJsonArray);
		NotifyTool.notifyIoSessionList(GameManager.getSessionListWithRoomNumber(lastGetCardUser.getRoomId()+""), outJsonObject);
		initializeUser(lastGetCardUser,game);//初始化用户的数据
		setCurrentGameOver(game);//设置当前的游戏结束
		setNewBank(lastGetCardUser,game);//设置新的庄家
	}
	
	/**臭庄
	 * @return
	 */
	public static boolean chouZhuang(List<Integer> remainCards,Game game){
		if(remainCards.size()==0){
			return true;
		}else{
			return false;
		}
	}
	
	
	

	
	
	
	
	/**用户赢牌 
	 * @param game 
	 * @param removeCard 最后抓的牌
	 * @param user 赢牌的玩家
	 * @param isTianhu 是否天胡
	 */
	public static void userWinNotAuto(Game game, Integer removeCard, User user,boolean ziMo) {
		OneRoom room = game.getRoom();
		int chong = room.getChong();
		if(chong==2){//如果是陪冲,每个人都出钱
			modifyUserScoreByPeiChong(user, game,ziMo);//根据胡牌修改用户的得分
		}else{//包冲一个人出钱
			modifyUserScoreByBaoChong(user, game,ziMo);
		}
		HuiFangUitl.getHuPai(game.getHuiFang(), user, user.getCards(),game.getRemainCards());
		try {
			recordUserScore(game);
		} catch (Exception e) {
			logger.error(e);
		}finally{
			//记录下当前的局的战绩
			notifyUserWin(user,game,removeCard);//通知用户赢牌
			initializeUser(user,game);//初始化用户的数据
			setCurrentGameOver(game);//设置当前的游戏结束,并且等待游戏开局，8/16局结束的时候结束游戏
			setNewBank(user,game);//设置新的庄家
		}
	}
	
	
	
	/**包冲修改玩家的分数
	 * @param user
	 * @param game
	 * @param ziMo 是否自摸
	 */
	private static void modifyUserScoreByBaoChong(User user, Game game,boolean ziMo) {
		List<User> userList = game.getUserList();
		user.modifyUserHuPaiTotal();//修改赢牌玩家的胡牌次数
		//赢牌的玩家是否是跑搭
		boolean diaoJiang = false;
		Integer myGrabCard = user.getMyGrabCard();
		if(myGrabCard!=null){
			List<Integer> cards = user.getCards();
			List<Integer> newCards = getNewCards(cards, myGrabCard);
			List<Integer> tingList = HuPai.getTingList(newCards, game.getBaida());
			if(tingList.size()>=34){
				diaoJiang = true;
			}
		}
		int totalUser = userList.size()-1;
		User fangPaoUser = game.getFangPaoUser();
		if(fangPaoUser!=null){
			if(diaoJiang){
				modifyUserScoreForPaoDaOrPaoDaTuoDaBaoChong(totalUser, fangPaoUser, user);
			}else{
				modifyUserScoreForNotZiMoBaoChong(totalUser, fangPaoUser, user);
			}
			mofidyUserScoreForDaPaiBaoChong(totalUser,fangPaoUser, user);//根据搭牌修改用户的分数
		}else{//如果是自摸
			mofidyUserScoreForDaPaiBaoChongZiMo(user, game, ziMo);
		}
	}
	
	
	/**根据胡牌修改用户的得分 
	 * @param user 胡牌的用户
	 * @param game
	 */
	private static void mofidyUserScoreForDaPaiBaoChongZiMo(User user, Game game,boolean userZiMo) {
		List<User> userList = game.getUserList();
		List<User> shuUserList = new ArrayList<>();
		User winUser =  null;//赢牌的玩家
		for(User u:userList){
			if(u.getId()!=user.getId()){//输的玩家
				shuUserList.add(u);
			}else{
				winUser = u;
			}
		}
		winUser.modifyUserHuPaiTotal();//修改赢牌玩家的胡牌次数
		//赢牌的玩家是否是跑搭
		boolean diaoJiang = false;
		Integer myGrabCard = winUser.getMyGrabCard();
		if(myGrabCard!=null){
			List<Integer> cards = winUser.getCards();
			List<Integer> newCards = getNewCards(cards, myGrabCard);
			List<Integer> tingList = HuPai.getTingList(newCards, game.getBaida());
			if(tingList.size()>=34){
				diaoJiang = true;
			}
		}
		int wanFa = game.getWanFa();
		if(diaoJiang){
			modifyUserScoreForPaoDaOrPaoDaTuoDa(shuUserList, winUser,wanFa);
		}else{
			if(userZiMo){//是自摸并且不是跑搭 
				int baida = game.getBaida();
				modifyUserScoreForZiMo(baida, shuUserList, winUser,wanFa);
			}else {//不是自摸
				modifyUserScoreForNotZiMo(shuUserList, winUser,wanFa);
			}
		}
		boolean gangKai = winUser.isGangKai();
		if(gangKai){
			modifyUserScoreForGangKai(shuUserList, winUser,wanFa);
		}
		mofidyUserScoreForDaPai(shuUserList, winUser,wanFa,userZiMo,false);//根据搭牌修改用户的分数
	}
	
	
	

	/**用户赢牌
	 * @param game 
	 * @param removeCard 最后抓的牌
	 * @param user 赢牌的玩家
	 * @param isTianhu 是否天胡
	 */
	public static void userWin(Game game, Integer removeCard, User user,boolean isTianhu) {
		if(!isTianhu){
			//依然通知玩家抓的牌是什么
			notifyUserDrawDirection(removeCard, user,null,-1);//通知抓牌的方向
		}
		boolean ziMo = user.isZiMo();
		OneRoom room = game.getRoom();
		int chong = room.getChong();
		if(chong==2){//如果是陪冲
			modifyUserScoreByPeiChong(user, game,ziMo);//根据胡牌修改用户的得分
		}else{
			modifyUserScoreByBaoChong(user, game,ziMo);
		}
		HuiFangUitl.getHuPai(game.getHuiFang(), user, user.getCards(),game.getRemainCards());
		//记录下当前的局的战绩
		recordUserScore(game);
		notifyUserWin(user,game,removeCard);//通知用户赢牌
		initializeUser(user,game);//初始化用户的数据
		setCurrentGameOver(game);//设置当前的游戏结束,并且等待游戏开局，8/16局结束的时候结束游戏
		setNewBank(user,game);//设置新的庄家
	}
	
	
	/**记录下用户的得分
	 * @param game
	 */
	public static void recordUserScore(Game game){
//		List<User> userList = game.getUserList();
//		int roomid = game.getRoom().getId();//房间号
//		java.util.Date createDate = new java.util.Date();
//		VedioDao vedioDao = VedioDao.getInstance();
//		Vedio vedio = new Vedio();
//		vedio.setRecord(game.getHuiFang().toString());
//		vedioDao.saveVedio(vedio);
//		int currentGame = game.getAlreadyTotalGame();//当前的局数
//		for(int i=0;i<userList.size();i++){
//			User user = userList.get(i);
//			int score = user.getCurrentGameSore();
//			user.setCurrentScore(user.getCurrentScore()+score);
//			int userid = user.getId();//用户的ID
//			UserScore userScore = new UserScore(userid, roomid,currentGame,score,createDate,vedio.getId());
//			userScoreDao.saveUserScore(userScore);
//		}
		List<ScoreUser> scoreUserList = getScoreUserListFromGame(game);
		StringBuffer huiFang = game.getHuiFang();
		RecordScoreThread recordScoreThread = new RecordScoreThread(scoreUserList,huiFang);
		ExecutorService executorService = RecordScoreThreadPool.getExecutorService();
		executorService.execute(recordScoreThread);
	}
	
	
	private static List<ScoreUser> getScoreUserListFromGame(Game game){
		List<User> userList = game.getUserList();
		List<ScoreUser> scoreUsers = new ArrayList<>();
		int roomId = game.getRoom().getId();
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			int id = user.getId();
			int currentGame = user.getCurrentGame();
			int score = user.getCurrentGameSore();
			ScoreUser scoreUser = new ScoreUser(id, currentGame, score, roomId);
			scoreUsers.add(scoreUser);
		}
		return scoreUsers;
	}
	

	/**设置当前的游戏结束,并且等待游戏开局，8/16局结束的时候结束游戏
	 * @param game
	 */
	private static void setCurrentGameOver(Game game) {
		int alreadyTotalGame = game.getAlreadyTotalGame();
		game.getGameStatusMap().put(alreadyTotalGame, GAME_END);
		game.setGameStatus(GAGME_STATUS_OF_CHUPAI);//游戏的状态变成出牌
		game.setStatus(GAGME_STATUS_OF_WAIT_START);//游戏等待
		//判断当前的游戏是否结束
		OneRoom room = game.getRoom();
//		int roomTotal = room.getTotal();
		if(isGameOver(room)){//游戏结束
			summarizedAll(game);
			game.setOver(true);//游戏结束
			game = null;
		}else{//如果总的游戏还没有结束,30秒后还有未准备的玩家,则自动准备 
			if(game!=null){
				//目前在托管状态下，35秒才会自动点击“返回游戏”和“准备”这个时间要改短到3--5秒
				autoUserPrepare(game,alreadyTotalGame);	
				prepare(game,alreadyTotalGame);
			}
		}
	}
	
	/**判断游戏是否结束
	 * @param oneRoom
	 * @return
	 */
	private static boolean isGameOver(OneRoom oneRoom){
		List<User> userList = oneRoom.getUserList();
		int total = oneRoom.getTotal();
		int alreadyTotalGame = oneRoom.getAlreadyTotalGame();
		if(alreadyTotalGame>=total){
			return true;
		}
		int alreadyGuang = 0;
		int wanFa = oneRoom.getWanFa();
		boolean isOver = false;
		if(wanFa==1){
			//判断几家光
			int totalGuang =  oneRoom.getEnd();
			for(int i=0;i<userList.size();i++){
				User user = userList.get(i);
				int currentScore = user.getCurrentScore();
				if(currentScore<=0){
					alreadyGuang ++;
				}
			}
			if(alreadyGuang>=totalGuang){
				isOver = true;
			}
		}
		return isOver;	
	}
	
	
	
	/**目前在托管状态下，35秒才会自动点击“返回游戏”和“准备”这个时间要改短到3--5秒
	 * @param game
	 */
	private static void autoUserPrepare(final Game game,final int currentGame) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if((game.getStatus()==GAGME_STATUS_OF_WAIT_START)&&
						(!game.isOver())&&
						(!game.isDisband())//游戏没有解散
				){//等待开局
					OneRoom room = game.getRoom();
					List<User> userList = room.getUserList();
					for(int i=0;i<userList.size();i++){
						User user = userList.get(i);
						if(user.isAuto()){//如果用户是托管的状态
							//通知该玩家去掉结算的界面
							JSONObject autoStartJsonObject = new JSONObject();
							autoStartJsonObject.put(method, "autoStart");
							user.getIoSession().write(autoStartJsonObject.toString());//通知玩家自动开始,去掉结算的界面
							if(!user.isReady()){
								user.setReady(true);
								JSONObject readyJsonObject = UserService.getReadyJsonObject(user);
								NotifyTool.notifyIoSessionList(room.getUserIoSessionList(), readyJsonObject);
							}
						}
					}
					int totalReady = getTotalReady(userList);
					if(totalReady==4){//开始游戏
						//开始游戏
						UserService userService = new UserService();
						userService.beginGame(room);
					}
				}else{
					logger.info("这里没有自动准备");
				}
			}
		}, AUTO_USER_TIME_TO_START_GAME);
	}

	
	/**通知其他玩家
	 * @param userList
	 * @return
	 */
	private static int getTotalReady(List<User> userList) {
		int totalReady = 0;
		for(int i=0;i<userList.size();i++){
			User u = userList.get(i);
			if(u.isReady()==true){
				totalReady ++;
			}
		}
		return totalReady;
	}
	
	
	/**自动准备游戏,如果总的游戏还没有结束,30秒后还有未准备的玩家,则自动准备 
	 * @param game
	 */
	private static void prepare(final Game game,final int currentGame) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if((game.getStatus()==GAGME_STATUS_OF_WAIT_START)&&
						(!game.isOver())&&
						(!game.isDisband())//游戏没有解散
				){//等待开局
					OneRoom room = game.getRoom();
					List<User> userList = room.getUserList();
					for(int i=0;i<userList.size();i++){
						User user = userList.get(i);
						//通知该玩家去掉结算的界面
						JSONObject autoStartJsonObject = new JSONObject();
						autoStartJsonObject.put(method, "autoStart");
						user.getIoSession().write(autoStartJsonObject.toString());//通知玩家自动开始,去掉结算的界面
						if(!user.isReady()){
							user.setReady(true);
							JSONObject readyJsonObject = UserService.getReadyJsonObject(user);
							NotifyTool.notifyIoSessionList(room.getUserIoSessionList(), readyJsonObject);
						}
					}
					//开始游戏
					UserService userService = new UserService();
					userService.beginGame(room);
				}{
					logger.info("这里没有自动准备");
				}
			}
		}, TIME_TO_START_GAME);
	}

	/**总结算，房间解散 
	 * @param game
	 */
	private static void summarizedAll(Game game) {
		OneRoom room = game.getRoom();
		List<User> userList = room.getUserList();
		int total = room.getTotal();
		int zhama = room.getZhama();
		JSONObject outJSONObject = getSummarizeJsonObject(userList,total+1,zhama);
		NotifyTool.notifyIoSessionList(GameManager.getSessionListWithRoomNumber(room.getId()+""), outJSONObject);
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			user.clearAll();//清空用户所有的属性
			try {
				RedisUtil.delKey("usRoomId" + user.getId(),1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		RedisUtil.delKey("zjRoomId"+room.getId(), 1);
		//记录玩家的总成绩
		recoredUserScore(outJSONObject, game);
		//先移除游戏中的map,后移除房间中的map 否则有空指针异常,顺序不可颠倒
		GameManager.removeGameWithRoomNumber(room.getId()+"");
		RoomManager.removeOneRoomByRoomId(room.getId()+"");
	}
	
	
	/**
	 * 记录玩家的总成绩
	 */
	public static void recoredUserScore(JSONObject jsonObject,Game game){
		JSONArray userArray = jsonObject.getJSONArray("userScoreArray");
		int roomNumber = game.getRoom().getRoomNumber();
		Date createDate = new Date();
		for(int i=0;i<userArray.length();i++){
			JSONObject user = userArray.getJSONObject(i);
			SumScore sumScore = new SumScore();
			sumScore.setRoomNumber(roomNumber+"");
			sumScore.setUserid(user.getInt("userId"));
			sumScore.setHuPaiTotal(user.getInt("hupai"));//胡牌次数
			sumScore.setJieGangTotal(user.getInt("jieGang")); //接杠次数
			sumScore.setAnGangTotal(user.getInt("anGang"));//暗杠次数
			sumScore.setFinalScore(user.getInt("finallyScore"));//最终成绩
			sumScore.setFangGangTotal(user.getInt("fangGang"));//放杠次数
			sumScore.setMingGangtotal(user.getInt("gongGang"));//明杠也称公杠次数
			sumScore.setCreateDate(createDate);
			sumScoreDao.saveSumScore(sumScore);//保存用户的房间号
		}
	}
	
	

	/**得到结算的jsonObejct
	 * @param userList
	 * @param alreadyTotalGame 总圈数+1
	 * @return
	 */
	public static JSONObject getSummarizeJsonObject(List<User> userList,int alreadyTotalGame,int totalMa) {
		JSONObject outJSONObject = new JSONObject();
		JSONArray userScoreArray = new JSONArray();
		outJSONObject.put(method, "summarizedAll");
		int juNum = alreadyTotalGame; //当前圈数+1
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			Map<Integer, Score> scoreMap = user.getScoreMap();
			Iterator<Integer> iterator = scoreMap.keySet().iterator();
			int hupai = 0; //胡牌次数
			int gongGang = 0; //公杠次数
			int anGang = 0; //暗杠次数
			int finallyScore = 0; //最终成绩
			int fangGang = 0;//放杠次数
			int jieGang = 0;//接杠次数
			while(iterator.hasNext()){
				Integer key = iterator.next();
				Score score = scoreMap.get(key);
				int huPaiTotal = score.getHuPaiTotal();//胡牌次数
				hupai = hupai+huPaiTotal;
				int gongGangTotal = score.getMingGangtotal();//公杠也称明杠
				gongGang = gongGang + gongGangTotal;
				int anGangTotal = score.getAnGangTotal();//暗杠次数
				anGang = anGang + anGangTotal;
				int finalScore = score.getFinalScore();//总结算
				finallyScore = finallyScore + finalScore;
				int jieGangTotal = score.getZimoTotal();//接杠次数->自摸次数
				jieGang = jieGang + jieGangTotal;
				int fangGangTotal = score.getFangChongTotal();//放杠次数 -> 放冲次数
				fangGang = fangGang + fangGangTotal;
			}
			JSONObject userScoreJSONObject = new JSONObject();
			userScoreJSONObject.put("hupai", hupai);
			userScoreJSONObject.put("gongGang", gongGang);
			userScoreJSONObject.put("anGang", anGang);
			userScoreJSONObject.put("juNum", juNum);
			userScoreJSONObject.put("finallyScore", user.getCurrentScore()-totalMa);
			userScoreJSONObject.put("userId", user.getId());
			userScoreJSONObject.put(direction, user.getDirection());
			userScoreJSONObject.put("fangGang", fangGang);//放杠次数
			userScoreJSONObject.put("jieGang", jieGang);//接杠次数
			userScoreArray.put(userScoreJSONObject);
		}
		outJSONObject.put("userScoreArray", userScoreArray);
		return outJSONObject;
	}
	
	/**根据胡牌修改用户的得分 
	 * @param user 胡牌的用户
	 * @param game
	 */
	private static void modifyUserScoreByPeiChong(User user, Game game,boolean userZiMo) {
		List<User> userList = game.getUserList();
		List<User> shuUserList = new ArrayList<>();
		User winUser =  null;//赢牌的玩家
		for(User u:userList){
			if(u.getId()!=user.getId()){//输的玩家
				shuUserList.add(u);
			}else{
				winUser = u;
			}
		}
		winUser.modifyUserHuPaiTotal();//修改赢牌玩家的胡牌次数
		//赢牌的玩家是否是跑搭
		boolean diaoJiang = false;
		Integer myGrabCard = winUser.getMyGrabCard();
		if(myGrabCard!=null){
			List<Integer> cards = winUser.getCards();
			List<Integer> newCards = getNewCards(cards, myGrabCard);
			List<Integer> tingList = HuPai.getTingList(newCards, game.getBaida());
			if(tingList.size()>=34){
				diaoJiang = true;
			}
		}
		int wanFa = game.getWanFa();
		if(diaoJiang){
			modifyUserScoreForPaoDaOrPaoDaTuoDa(shuUserList, winUser,wanFa);
		}else{
			if(userZiMo){//是自摸并且不是跑搭 
				int baida = game.getBaida();
				modifyUserScoreForZiMo(baida, shuUserList, winUser,wanFa);
			}else {//不是自摸
				modifyUserScoreForNotZiMo(shuUserList, winUser,wanFa);
			}
		}
		boolean gangKai = winUser.isGangKai();
		if(gangKai){
			modifyUserScoreForGangKai(shuUserList, winUser,wanFa);
		}
		mofidyUserScoreForDaPai(shuUserList, winUser,wanFa,userZiMo,true);//根据搭牌修改用户的分数
	}

	
	public static List<Integer> getNewCards(List<Integer> myCards,int myGrabCard){
		List<Integer> myCardsCopyList =  new ArrayList<>();
		for(int i=0;i<myCards.size();i++){
			Integer card = myCards.get(i);
			if(card!=myGrabCard){
				myCardsCopyList.add(myCards.get(i));
			}
		}
		return myCardsCopyList;
	}
	
	/**刚上开花修改用户的成绩
	 * @param shuUserList
	 * @param winUser
	 */
	private static void modifyUserScoreForGangKai(List<User> shuUserList, User winUser,int wanFa) {
		int totalGangKai = 0;
		for(int i=0;i<shuUserList.size();i++){
			User shuUser = shuUserList.get(i);
			int reduceScoreForGangKai = shuUser.reduceScoreForGangKai(wanFa);
			totalGangKai = totalGangKai + reduceScoreForGangKai;
		}
		winUser.addScore(totalGangKai);
	}

	
	/**修改用户的成绩跑搭或者跑搭脱搭
	 * @param shuUserList
	 * @param winUser
	 */
	private static void modifyUserScoreForPaoDaOrPaoDaTuoDaBaoChong(int totalUser,User shuUser, User winUser) {
		List<Integer> cards = winUser.getCards();
		HuPai huPai = new HuPai();
		boolean hu = huPai.isHu(cards, 999);
		if(hu){//跑搭拖搭
			int totalPaoDaTuoDa = Math.abs(ScoreType.REDUCE_SCORE_FOR_PAODA_TUODA) *totalUser;
			winUser.addScore(totalPaoDaTuoDa);
			shuUser.reduceScoreForBaoChong(totalPaoDaTuoDa);
		}else{
			int totalPaoDa = Math.abs(ScoreType.REDUCE_SCORE_FOR_PAODA)*totalUser;
			winUser.addScore(totalPaoDa);
			shuUser.reduceScoreForBaoChong(totalPaoDa);
		}
	}
	
	
	
	/**修改用户的成绩跑搭或者跑搭脱搭
	 * @param shuUserList
	 * @param winUser
	 */
	private static void modifyUserScoreForPaoDaOrPaoDaTuoDa(List<User> shuUserList, User winUser,int wanFa) {
		List<Integer> cards = winUser.getCards();
		HuPai huPai = new HuPai();
		boolean hu = huPai.isHu(cards, 999);
		if(hu){//跑搭拖搭
			int totalPaoDaTuoDa = 0 ;
			for(int i=0;i<shuUserList.size();i++){
				User shuUser = shuUserList.get(i);
				int reduceScoreForPaoDaTuoDa = shuUser.reduceScoreForPaoDaTuoDa(wanFa);
				totalPaoDaTuoDa = totalPaoDaTuoDa + reduceScoreForPaoDaTuoDa;
			}
			winUser.addScore(totalPaoDaTuoDa);
		}else{
			int totalPaoDa = 0 ;
			for(int i=0;i<shuUserList.size();i++){
				User shuUser = shuUserList.get(i);
				int reduceScoreForPaoDa = shuUser.reduceScoreForPaoDa(wanFa);
				totalPaoDa = totalPaoDa + reduceScoreForPaoDa;
			}
			winUser.addScore(totalPaoDa);
		}
	}

	
	private static void modifyUserScoreForNotZiMoBaoChong(int tatalUser,User shuUser, User winUser) {
		int huPaiTotal = 0;
		huPaiTotal = Math.abs(ScoreType.REDUCE_SCORE_FOR_HUPAI)*tatalUser+2;
		shuUser.reduceScoreForBaoChong(huPaiTotal);
		winUser.addScore(huPaiTotal);
	}
	
	
	private static void modifyUserScoreForNotZiMo(List<User> shuUserList, User winUser,int wanFa) {
		int huPaiTotal = 0;
		for(User u:shuUserList){
			int reduceScoreForHuPai = u.reduceScoreForHuPai(wanFa);
			huPaiTotal = huPaiTotal+reduceScoreForHuPai;
		}
		winUser.addScore(huPaiTotal);
	}

	private static void modifyUserScoreForZiMo(int baiDa, List<User> shuUserList, User winUser,int wanFa) {
		List<Integer> cards = winUser.getCards();
		boolean isWuDaZiMo = isHaveDa(cards, baiDa);
		if(isWuDaZiMo){//无搭（嵌搭视同无搭）自摸每人六个筹码
			int totalWuDa = 0;
			for(int i=0;i<shuUserList.size();i++){
				User shuUser = shuUserList.get(i);
				int reduceScoreForWuDa = shuUser.reduceScoreForWuDa(wanFa);
				totalWuDa = totalWuDa + reduceScoreForWuDa;
			}
			winUser.addScore(totalWuDa);
		}else{ //有搭自摸每人三个筹码
			int totalYouDa = 0;
			for(int i=0;i<shuUserList.size();i++){
				User shuUser = shuUserList.get(i);
				int reduceScoreForYouDa = shuUser.reduceScoreForYouDa(wanFa);
				totalYouDa = totalYouDa + reduceScoreForYouDa;
			}
			winUser.addScore(totalYouDa);
		}
	}

	/**是否有搭牌
	 * @param cards 手中的牌
	 * @param baiDa 百搭
	 * @return
	 */
	public static boolean isHaveDa(List<Integer> cards,int baiDa){
		boolean result =  true;
		int number = baiDa /4;
		for(int i=0;i<cards.size();i++){
			Integer card = cards.get(i);
			if(card/4==number){
				result = false;
				break;
			}
		}
		
		if(result==false){//如果是有搭
			HuPai huPai = new HuPai();
			boolean hu = huPai.isHu(cards, 199);
			if(hu){
				result =  true;
			}
		}
		
		return result;
	}
	
	
	/**修改用户的分数根据搭牌
	 * @param shuUserList
	 * @param winUser
	 */
	private static void mofidyUserScoreForDaPaiBaoChong(int totalUser,User shuUser, User winUser) {
		//判断是否有首搭
		if(winUser.getShoDaList().size()>0){
			int shouDaTotal = 0;
			int size = winUser.getShoDaList().size();
			shouDaTotal = totalUser*ScoreType.REDUCE_SCORE_FOR_SHOUDA*size;
			shuUser.reduceScoreForBaoChong(Math.abs(shouDaTotal));
			winUser.addScore(Math.abs(shouDaTotal));
		}
		//判断是否有二搭
		if(winUser.getErDaList().size()>0){
			int erDaTotal = 0;
			int size = winUser.getErDaList().size();
			erDaTotal = totalUser*ScoreType.REDUCE_SCORE_FOR_ERDA*size;
			shuUser.reduceScoreForBaoChong(Math.abs(erDaTotal));
			winUser.addScore(Math.abs(erDaTotal));
		}
		//判断是否有三搭
		if(winUser.getSanDaList().size()>0){
			int sanDaTotal = 0;
			int size = winUser.getSanDaList().size();
			sanDaTotal = totalUser*ScoreType.REDUCE_SCORE_FOR_SANDA*size;
			shuUser.reduceScoreForBaoChong(Math.abs(sanDaTotal));
			winUser.addScore(Math.abs(sanDaTotal));
		}
	}
	
	
	/**修改用户的分数根据搭牌
	 * @param shuUserList
	 * @param winUser
	 */
	private static void mofidyUserScoreForDaPai(List<User> shuUserList, User winUser,int wanFa,boolean zimo,boolean peiChong) {
		//判断是否有首搭
		int totalUser = 0;
		if(peiChong){
			totalUser =1;
		}else{
			if(zimo){
				totalUser =1;
			}else{
				totalUser = shuUserList.size();
			}
		}
		if(winUser.getShoDaList().size()>0){
			int shouDaTotal = 0;
			int size = winUser.getShoDaList().size();
			for(int i=0;i<shuUserList.size();i++){
				User shuUser = shuUserList.get(i);
				int reduceScoreForShouDa = shuUser.reduceScoreForShouDa(size,wanFa,totalUser);
				shouDaTotal = shouDaTotal + reduceScoreForShouDa;
			}
			winUser.addScore(shouDaTotal);
		}
		//判断是否有二搭
		if(winUser.getErDaList().size()>0){
			int erDaTotal = 0;
			int size =  winUser.getErDaList().size();
			for(int i=0;i<shuUserList.size();i++){
				User shuUser = shuUserList.get(i);
				int reduceScoreForErDa = shuUser.reduceScoreForErDa(size,wanFa,totalUser);
				erDaTotal = erDaTotal + reduceScoreForErDa;
			}
			winUser.addScore(erDaTotal);
		}
		//判断是否有三搭
		if(winUser.getSanDaList().size()>0){
			int sanDaTotal = 0;
			int size = winUser.getSanDaList().size();
			for(int i=0;i<shuUserList.size();i++){
				User shuUser = shuUserList.get(i);
				int reduceScoreForSanDa = shuUser.reduceScoreForSanDa(size,wanFa,totalUser);
				sanDaTotal = sanDaTotal + reduceScoreForSanDa;
			}
			winUser.addScore(sanDaTotal);
		}
	}

	
	/**通知所有的玩家 暗杠的牌
	 * @param game
	 * @param pengCards
	 */
	public static void notifyAllUserAnGang(Game game, List<Integer> gangCards,User user) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("gangCards", gangCards);
		outJsonObject.put(method, "playGame");
		outJsonObject.put(type, "anGang");
		outJsonObject.put("gangDirection", user.getDirection());
		outJsonObject.put(discription, "玩家杠的牌");
		List<ChannelHandlerContext> userIoSessionList = game.getRoom().getUserIoSessionList();
		NotifyTool.notifyIoSessionList(userIoSessionList, outJsonObject);
	}
	
	
	/**通知所有的玩家杠的牌
	 * @param game
	 * @param pengCards
	 */
	public static void notifyAllUserGang(Game game, List<Integer> gangCards,User user) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("gangCards", gangCards);
		outJsonObject.put(method, "playGame");
		outJsonObject.put(type, "gang");
		outJsonObject.put("gangDirection", user.getDirection());
		outJsonObject.put(discription, "玩家杠的牌");
		List<ChannelHandlerContext> userIoSessionList = game.getRoom().getUserIoSessionList();
		NotifyTool.notifyIoSessionList(userIoSessionList, outJsonObject);
	}
	
	
	/**通知所有的玩家公杠的牌
	 * @param game
	 * @param pengCards
	 */
	public static void notifyAllUserGongGang(Game game, List<Integer> gangCards,User user) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("gangCards", gangCards);
		outJsonObject.put(method, "playGame");
		outJsonObject.put(type, "gongGang");
		outJsonObject.put("gangDirection", user.getDirection());
		outJsonObject.put(discription, "玩家杠的牌");
		List<ChannelHandlerContext> userIoSessionList = game.getRoom().getUserIoSessionList();
		NotifyTool.notifyIoSessionList(userIoSessionList, outJsonObject);
	}
	
	/**碰牌或杠牌 ,出完牌后改变出牌的状态
	 * @param jsonObject
	 * @param session
	 */
	private void peng(JSONObject jsonObject, ChannelHandlerContext session) {
		int cardId = jsonObject.getInt("cardId");
		Game game = getGame(session);
		User sessionUser = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		User gamingUser = getGamingUser(game, sessionUser.getDirection());
		gamingUser.setUserCanPlay(true);
//		boolean canPeng = checkUserIsCanPeng(gamingUser);
//		if(!canPeng){
//			notifyUserError(session, "不可以碰");
//			return;
//		}
		Map<String, User> seatMap = game.getSeatMap();
		User user = seatMap.get(sessionUser.getDirection());
		List<Integer> cards = user.getCards();
		List<Integer> pengList = getPengList(cards, cardId);//得到可以碰的集合
		if(pengList.size()!=2){
			notifyUserError(session, "异常碰牌");
			return;
		}
		int status = game.getGameStatus();
		if(status == GAGME_STATUS_OF_WAIT_HU_NEW){//如果是自摸胡
			User fangPaoUser = game.getFangPaoUser();
			Integer autoHuCardId = game.getAutoHuCardId();
			String endDriection = fangPaoUser.getDirection();
			String canHuDirection = nextUserIsHu(game, gamingUser, autoHuCardId, endDriection);
			if(!StringUtils.isNullOrEmpty(canHuDirection)){//下一个人是否还可以胡 
				nextUserHu(game, autoHuCardId, canHuDirection);
				game.setWaitHuStatus(GAGME_STATUS_OF_WAIT_HU_NEW_WAIT_PENG);
				game.setWaitDirection(gamingUser.getDirection());
				return;
			}
		}
		user.userPengCards(pengList);//玩家碰牌
		pengList.add(cardId);
		user.addUserPengCards(pengList,game.getFangPengUser().getDirection());//用户添加碰出的牌
		//如果碰的是搭牌,放碰的人没有了首搭
		if(pengList.get(0)/4==game.getBaida()/4){
			List<Integer> shouDaList = game.getFangPengUser().getShoDaList();
			shouDaList.remove(new Integer(cardId));
			//碰的人首搭增加
			List<Integer> gameUserShouDaList = gamingUser.getShoDaList();
			for(int i=0;i<pengList.size();i++){
				gameUserShouDaList.add(pengList.get(i));
			}
		}
		user.setUserCanPlay(true);//该玩家可以出牌
		game.setGameStatus(GAGME_STATUS_OF_CHUPAI);//游戏的状态变为出牌
		game.setDirec(user.getDirection());
		user.setLastChuPaiDate(new Date());
		//记录玩家碰的牌
		HuiFangUitl.getPengPai(game.getHuiFang(), gamingUser, pengList);
		notifyAllUserPeng(game, pengList,user);
		gamingUser.setCanSanDa(true);
		gamingUser.setZiMo(false);
	}
	
	
	/**通知用户不可碰
	 * @param session
	 * @param type 1 、不可以碰，2、不可以杠
	 */
	public void notifyUserCanNotPengOrGang(ChannelHandlerContext session,int type){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(code, error);
		switch (type) {
		case 1:
			jsonObject.put(discription, "不可以碰");
			logger.fatal("不可以碰");
			break;
		case 2:
			jsonObject.put(discription, "不可以杠");
			logger.fatal("不可以杠");
			break;
		}
		session.write(jsonObject.toString());
	}
	
	
	
	public boolean checkUserIsCanPeng(User user){
		boolean result =  user.isCanPeng();
		return result;
	}
	
	
	/**检测用户是否可以杠
	 * @param user
	 * @return
	 */
	public boolean checkUserIsCanGang(User user){
		boolean result =  user.isCanGang();
		return result;
	}
	
	
	
	/**得到可以碰的集合
	 * @param cards
	 * @param cardId
	 * @return
	 */
	public static List<Integer> getPengList(List<Integer> cards,int cardId){
		List<Integer> list = new ArrayList<>();
		int total = 0;
		for(Integer card:cards){
			if(cardId/4==card/4&&total<2){
				list.add(card);
				total ++ ;
			}
			
			if(total==2){
				break;
			}
			
		}
		return list;
	}
	
	
	/**得到可以碰的集合
	 * @param cards
	 * @param cardId
	 * @return
	 */
	public static List<Integer> getGangList(List<Integer> cards,int cardId){
		List<Integer> list = new ArrayList<>();
		int total = 0;
		for(Integer card:cards){
			if(card/4==cardId/4&&total<4){//4444 万 到第四个4停止
				list.add(card);
				total ++ ;
			}
		}
		return list;
	}
	
	
	
	/**通知所有的玩家游戏方向改变
	 * @param user
	 */
	public void notifyAllUserDirectionChange(User user){
		Game game = getGame(user);
		String nextDirection = user.getDirection();
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "playDirection");
		outJsonObject.put("direction", nextDirection);
		outJsonObject.put("description", "出牌的的方向");
		game.setDirec(user.getDirection());//出牌的方向改变
		NotifyTool.notifyIoSessionList(GameManager.getSessionListWithRoomNumber(user.getRoomId()+""), outJsonObject);
	}
	

	/**通知所有的玩家碰的牌
	 * @param game
	 * @param pengCards
	 * @param user 
	 */
	public static void notifyAllUserPeng(Game game, List<Integer> pengCards, User user) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "playGame");
		outJsonObject.put(type, "peng");
		outJsonObject.put("pengDirction",user.getDirection());
		outJsonObject.put("pengCards", pengCards);
		outJsonObject.put("cpTotal", user.getChuPaiCiShu());
		outJsonObject.put("chuDir", game.getFangPengUser().getDirection());//放碰的方向
		List<ChannelHandlerContext> userIoSessionList = game.getRoom().getUserIoSessionList();
		NotifyTool.notifyIoSessionList(userIoSessionList, outJsonObject);
		if(pengCards.get(0)/4==game.getFanpai()/4){
			modifyUserScoreForJieGang(game, user);
			notifyAllUserCurrentScore(game);//通知用户现在的成绩
		}
	}
	
	/**得到游戏
	 * @param session
	 * @return
	 */
	public static Game getGame(ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		Game game = getGame(user);
		return game;
	}
	
	/**出牌,清空该用户的没有出牌次数
	 * @param jsonObject
	 * @param session
	 */
	public void chuPai(JSONObject jsonObject, ChannelHandlerContext session){
		int cardId = jsonObject.getInt("cardId");//出牌的牌号
		User sessionUser = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		Game game = getGame(sessionUser);
		User gamingUser = getGamingUser(game, sessionUser.getDirection());
		Map<String, User> seatMap = game.getSeatMap();
		String direction = gamingUser.getDirection();//得到当前的座次
		User user = seatMap.get(direction);
		int removeCardId = user.chuPai(new Integer(cardId));//出牌
		if(removeCardId<0){
			notifyUserError(session, "同时出牌出错");
			return;
		}
		JSONObject outJsonObject = getChuPaiOutJSONObject(cardId, user);
		//记录玩家出的牌
		HuiFangUitl.getChuPai(game.getHuiFang(), user, cardId);
		NotifyTool.notifyIoSessionList(GameManager.getSessionListWithRoomNumber(sessionUser.getRoomId()), outJsonObject);//通知所有用户打出的牌 是什么
		//判断其他人是否可以胡牌
		//判断他是否打的是首搭 
		if(removeCardId/4==game.getBaida()/4){//先判断是否首搭和二搭
			modifyUserForDa(cardId, game, user);
		}else{
			gamingUser.setCanShouDa(false);//出过非搭牌，不可以首搭
		}
		boolean isFangPao = isUserFangPao(cardId, game, direction,gamingUser);
		gamingUser.setZiMo(false);
		gamingUser.setCanSanDa(false);//用户不可以三搭
		gamingUser.setCanNotPengList(new ArrayList<>());//不可以碰的集合置空  
		gamingUser.setGangKai(false);
		if(!isFangPao){
			analysis(cardId, gamingUser, game);//继续分析是下一个人出牌还是能够碰牌和杠牌
		}else{
			if(game.getCanHuUser().isAuto()){
				 user.setFangPao(true);//放炮了
				 List<Integer> cards = game.getCanHuUser().getCards();
				 cards.add(cardId);
				 Collections.sort(cards);
				 //通知玩家赢牌 
				 userWin(game,cardId,game.getCanHuUser(),false);
			 }else{
				User huUser = game.getCanHuUser();
				int canPengOrCanGang = UserService.isCanPengOrCanGang(removeCardId, huUser.getCards());
				boolean isCanGang = false;
				boolean isCanPeng = false;
				if(canPengOrCanGang==2){
					huUser.setCanPeng(true);
					huUser.setCanGang(true);
					isCanGang = true;
					isCanPeng = true;
				}else if(canPengOrCanGang==1){
					game.setFangPengUser(gamingUser);
					huUser.setCanPeng(true);
					isCanPeng = true;
				}
				notifyUserCanHu(game, game.getCanHuUser(), 1,cardId,true,isCanPeng,isCanGang);
			 }
		}
	}

	private static void modifyUserForDa(int cardId, Game game, User user) {
		int chuPaiCiShu = user.getChuPaiCiShu();
		if((chuPaiCiShu==1||user.getMyPlays().size()==0)&&user.isCanShouDa()){//可以打首搭
			notifyUserDaPai(cardId, game, user,1);
			moidfyUserDa(user, 1, cardId);
		}else if(chuPaiCiShu==2){
			int size = user.getShoDaList().size();
			if(size==1){//已经打过一首搭
				notifyUserDaPai(cardId, game, user,1);
				moidfyUserDa(user, 1, cardId);
			}else{
				notifyUserDaPai(cardId, game, user,2);
				moidfyUserDa(user, 2, cardId);
			}
		}else if(chuPaiCiShu==3){
			int size = user.getShoDaList().size();
			if(size==2){//已经打过二首搭
				notifyUserDaPai(cardId, game, user,1);
				moidfyUserDa(user, 1, cardId);
			}else{
				if(user.isCanSanDa()){
					notifyUserDaPai(cardId, game, user,4);
					moidfyUserDa(user, 3, cardId);
				}else{
					notifyUserDaPai(cardId, game, user,2);
					moidfyUserDa(user, 2, cardId);
				}
			}
		}else if(chuPaiCiShu==4){
			int size = user.getShoDaList().size();
			if(size==3){//已经打过二首搭
				notifyUserDaPai(cardId, game, user,1);
				moidfyUserDa(user, 1, cardId);
			}else{
				if(user.isCanSanDa()){
					notifyUserDaPai(cardId, game, user,4);
					moidfyUserDa(user, 3, cardId);
				}else{
					notifyUserDaPai(cardId, game, user,2);
					moidfyUserDa(user, 2, cardId);
				}
			}
		}else{
			if(user.isCanSanDa()){
				notifyUserDaPai(cardId, game, user,4);
				moidfyUserDa(user, 3, cardId);
			}else{
				notifyUserDaPai(cardId, game, user,2);
				moidfyUserDa(user, 2, cardId);
			}
		}
	}

	/**改变用户搭牌
	 * @param user
	 * @param type 搭牌的类型 1 首搭 2 二搭 3三搭
	 */
	private static void moidfyUserDa(User user,int type,int cardId) {
		switch (type) {
		case 1:
			user.getShoDaList().add(cardId);
			break;
		case 2:
			user.getErDaList().add(cardId);
			break;
		case 3:
			user.getSanDaList().add(cardId);
			break;
		}
	}

	/**通知用户首搭
	 * @param cardId
	 * @param game
	 * @param u
	 * @param type
	 */
	private static void notifyUserDaPai(int cardId, Game game, User u,int type) {
		JSONObject showDaJsonObject = new JSONObject();
		showDaJsonObject.put("method", "showdownCard");
		if(type==1){
			showDaJsonObject.put("type", 1);
			showDaJsonObject.put("discription", "打首搭亮");
		}else if(type==2){
			showDaJsonObject.put("type", 3);
			showDaJsonObject.put("discription", "打二搭亮");
		}else if(type==4){//用户可以三搭
			showDaJsonObject.put("type", 4);
			showDaJsonObject.put("discription", "打三搭亮");
		}
		showDaJsonObject.put("cardId", cardId);
		showDaJsonObject.put("direction", u.getDirection());
		showDaJsonObject.put("isShowCard", true);
		NotifyTool.notifyIoSessionList(game.getIoSessionList(), showDaJsonObject);
	}
	

	/**
	 * @param cardId
	 * @param game
	 * @param direction
	 * @return
	 */
	private static boolean isUserFangPao(int cardId, Game game, String direction,User fangPaoUser) {
		String nextDirection = getNextDirection(direction);
		User canHuUser = game.getSeatMap().get(nextDirection);
		List<Integer> cards = canHuUser.getCards();
		List<Integer> newCards = HuPai.getNewListFromOldList(cards);
		newCards.add(cardId);
		Collections.sort(newCards);
		while(!nextDirection.equals(direction)){
			HuPai huPai = new HuPai();
			boolean hu = huPai.isHu(newCards, 200);
			if(hu){//是否胡牌
				hu = huPai.isHu(newCards, game.getBaida());
				boolean diaoJiang = huPai.isDiaoJiang();
				if(!diaoJiang){//没有调将
					game.setCanHuUser(canHuUser);
					game.setFangPaoUser(fangPaoUser);
					game.setAutoHuCardId(cardId);
					return true;
				}else{//调将
					nextDirection = getNextDirection(nextDirection);
					canHuUser = game.getSeatMap().get(nextDirection);
					cards = canHuUser.getCards();
					newCards = HuPai.getNewListFromOldList(cards);
					newCards.add(cardId);
					Collections.sort(newCards);
				}
			}else{
				nextDirection = getNextDirection(nextDirection);
			    canHuUser = game.getSeatMap().get(nextDirection);
			    cards = canHuUser.getCards();
			    newCards = HuPai.getNewListFromOldList(cards);
			    newCards.add(cardId);
			    Collections.sort(newCards);
			}
		}
		return false;
	}
	
	
	/**
	 * @param cardId
	 * @param game
	 * @param direction
	 * @return
	 */
	private static User isOtherUserQiangGang(int cardId, Game game, String direction,User fangPaoUser) {
		String nextDirection = getNextDirection(direction);
		User canHuUser = game.getSeatMap().get(nextDirection);
		List<Integer> cards = canHuUser.getCards();
		List<Integer> newCards = HuPai.getNewListFromOldList(cards);
		newCards.add(cardId);
		Collections.sort(newCards);
		while(!nextDirection.equals(direction)){
			HuPai huPai = new HuPai();
			boolean hu = huPai.isHu(newCards, 200);
			if(hu){//是否胡牌
				hu = huPai.isHu(newCards, game.getBaida());
				boolean diaoJiang = huPai.isDiaoJiang();
				if(!diaoJiang){//没有调将
					game.setCanHuUser(canHuUser);
					game.setFangPaoUser(fangPaoUser);
					game.setAutoHuCardId(cardId);
					return canHuUser;
				}else{//调将
					nextDirection = getNextDirection(nextDirection);
					canHuUser = game.getSeatMap().get(nextDirection);
					cards = canHuUser.getCards();
					newCards = HuPai.getNewListFromOldList(cards);
					newCards.add(cardId);
					Collections.sort(newCards);
				}
			}else{
				nextDirection = getNextDirection(nextDirection);
				canHuUser = game.getSeatMap().get(nextDirection);
				cards = canHuUser.getCards();
				newCards = HuPai.getNewListFromOldList(cards);
				newCards.add(cardId);
				Collections.sort(newCards);
			}
		}
		return null;
	}

	/**判断是否有搭牌
	 * @param cards
	 * @param baiDa
	 * @return
	 */
	public static boolean isHaveDaPai(List<Integer> cards,int baiDa){
		boolean result =  false;
		int number = baiDa /4;
		for(int i=0;i<cards.size();i++){
			Integer card = cards.get(i);
			if(card/4==number){
				result = true;
				break;
			}
		}
		return result;
	}
	
	/**给用户提示错误信息,不可以出牌
	 * @param session
	 * @param result 
	 */
	public void notifyUserError(ChannelHandlerContext session, String discription){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(method, "gameError");
		session.write(jsonObject.toString());
		logger.fatal(discription);
	}
	
	
	/**检测用户的权限
	 * @param cardId 
	 * @return -1 不该出牌 -2牌不存在 -3 不可以出牌
	 */
	public int checkPower(Game game,User user, int cardId){
		int result = 0;
		User gamingUser = game.getGamingUser(user.getDirection());
		String direc = game.getDirec();
		if(!direc.equals(gamingUser.getDirection())){
			return -1;
		}
		List<Integer> cards = game.getSeatMap().get(gamingUser.getDirection()).getCards();
		int index = MathUtil.binarySearch(cardId, cards);
		if(index<0){
			return -2;
		}
		return result;
	}
	
	/**得到出牌的json对象
	 * @param cardId
	 * @param u
	 * @return
	 */
	public static JSONObject getChuPaiOutJSONObject(int cardId, User u) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("cardId", cardId);//打出的牌
		outJsonObject.put("direction", u.getDirection());//出牌的人的座次
		outJsonObject.put("userId", u.getId());
		outJsonObject.put("type", "chupai");
		outJsonObject.put("method", "playGame");
		outJsonObject.put("discription", "出牌");
		return outJsonObject;
	}

	/**分析下一个人是否可以抓牌或杠牌,然后进一步计算
	 * @param cardId 打出的牌
	 * @param user 当前的人
	 * @param game
	 * @param seatMap
	 * @param direction 出牌的方向
	 */
	public static void analysis(int cardId, User user, Game game) {
		Map<String, User> seatMap = game.getSeatMap();
		//计算可以碰牌或杠牌的人
		User canPengOrGangUser = getPengOrGangCardUser(cardId,seatMap,user.getId(),game.getBaida(),user.getChuPaiCiShu());
		if(canPengOrGangUser!=null){
			//可以听牌或杠牌,该用户也没有托管才通知它
			boolean auto = canPengOrGangUser.isAuto();//用户是否托管
			if(auto){//如果托管自动碰
				List<Integer> cards = canPengOrGangUser.getCards();
				boolean isTing = canPengOrGangUser.isUserTingPaiOfPengOrGang(cards,game.getBaida());//用户是否听牌
				if(isTing){//如果该用户已经听牌
					logger.info("碰牌或杠牌的时候直接越过该用户"+canPengOrGangUser);
					nextUserDrawCards(cardId, user, game);
				}else{
					notifyUserCanPengOrGang(cardId, user, game, canPengOrGangUser);
					autoPengOrGang(canPengOrGangUser,game);
				}
			}else{
				notifyUserCanPengOrGang(cardId, user, game, canPengOrGangUser);
			}
		}else{//下一个人抓牌
			nextUserDrawCards(cardId, user, game);
		}
	}

	/**下一个用户抓牌
	 * @param cardId 当前的用户出的牌
	 * @param user 当前的用户
	 * @param game
	 */
	private static void nextUserDrawCards(int cardId, User user, Game game) {
		user.addMyPlays(cardId);
		String nextDirection = getNextDirection(user.getDirection());
		notifyUserDirectionChange(user, nextDirection);//通知用户出牌的方向改变
		userDrawCard(game, nextDirection,false);//用户抓牌
	}
	
	/**自动碰牌和杠牌
	 * @param canPengOrGangUser
	 */
	public static void autoPengOrGang(User canPengOrGangUser,Game game) {
		if(canPengOrGangUser.getPengOrGang()==1){//可以碰牌
			List<Integer> pengList = canPengOrGangUser.getPengOrGangList();
			canPengOrGangUser.userPengCards(pengList);
			canPengOrGangUser.addUserPengCards(pengList,game.getFangPengUser().getDirection());//用户添加碰出的牌
			//记录玩家碰的牌
			HuiFangUitl.getPengPai(game.getHuiFang(), canPengOrGangUser, pengList);
			notifyAllUserPeng(game, pengList,canPengOrGangUser);//通知碰牌
			game.setDirec(canPengOrGangUser.getDirection());//重新改变游戏的方向
			//碰牌后游戏的状态变为出牌
			game.setGameStatus(GAGME_STATUS_OF_CHUPAI);
			canPengOrGangUser.setLastChuPaiDate(new Date());
			if(canPengOrGangUser.isAuto()){
				canPengOrGangUser.setCanSanDa(true);
				autoChuPai(game);//自动出牌
			}
		}else if(canPengOrGangUser.getPengOrGang()==2){//可以杠牌
			List<Integer> gangCards = canPengOrGangUser.getPengOrGangList();
			canPengOrGangUser.userGangCards(gangCards);
			//记录玩家杠的牌
			canPengOrGangUser.recordUserGangCards(0, gangCards);
			//记录玩家杠的牌
			HuiFangUitl.getGangPai(game.getHuiFang(), canPengOrGangUser, gangCards);
//			modifyUserScoreForGang(game, canPengOrGangUser);//修改玩家得分
			modifyUserScoreForJieGang(game, canPengOrGangUser);
			PlayGameService.notifyAllUserGang(game, gangCards,canPengOrGangUser);//通知所有的玩家杠的牌 
			notifyAllUserCurrentScore(game);//通知用户现在的成绩
			//该玩家在抓一张牌 
			PlayGameService.userDrawCard(game, canPengOrGangUser.getDirection(),true);
		}
	}
	
	/**
	 * FIXME 注意这里加锁是为了，同一时间出牌
	 * 自动出牌
	 */
	public static void autoChuPai(Game game){
		String direc = game.getDirec();
		Map<String, User> seatMap = game.getSeatMap();
		User user = seatMap.get(direc);
		user.setUserCanPlay(true);
		int cardId = user.autoChuPai(game.getBaida());//自动出的牌
		if(cardId<0){
			logger.info("可能在同一时间打牌了");
			return;
		}
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//记录玩家出的牌
		HuiFangUitl.getChuPai(game.getHuiFang(), user, cardId);
		JSONObject outJsonObject = PlayGameService.getChuPaiOutJSONObject(cardId, user);
		NotifyTool.notifyIoSessionList(GameManager.getSessionListWithRoomNumber(user.getRoomId()), outJsonObject);//通知所有用户打出的牌 是什么
		//判断其他人是否可以胡牌
		if(cardId/4==game.getBaida()/4){
			modifyUserForDa(cardId, game, user);
		}
		boolean isFangPao = isUserFangPao(cardId, game,user.getDirection(),user);
		user.setCanSanDa(false);
		user.setCanNotPengList(new ArrayList<>());//不可以碰的集合置空  
		if(!isFangPao){
			analysis(cardId, user, game);//继续分析是下一个人出牌还是能够碰牌和杠牌
		}else{//
			 User canHuUser = game.getCanHuUser();
			 if(canHuUser.isAuto()){
				 user.setFangPao(true);//放炮了
				 List<Integer> cards = game.getCanHuUser().getCards();
				 cards.add(cardId);
				 Collections.sort(cards);
				 //通知玩家赢牌 
				 userWin(game,cardId,game.getCanHuUser(),false);
			 }else{
				 User huUser = game.getCanHuUser();
				 int canPengOrCanGang = UserService.isCanPengOrCanGang(cardId, huUser.getCards());
				 boolean isCanGang = false;
				 boolean isCanPeng = false;
				if(canPengOrCanGang==2){
					huUser.setCanPeng(true);
					huUser.setCanGang(true);
					isCanGang = true;
					isCanPeng = true;
				}else if(canPengOrCanGang==1){
					game.setFangPengUser(user);
					huUser.setCanPeng(true);
					isCanPeng = true;
				}
				notifyUserCanHu(game, game.getCanHuUser(), 1,cardId,true,isCanPeng,isCanGang);
			 }
		}
	}

	/**通知用户可以碰或者杠牌,这里设置了游戏的状态,碰牌或者杠牌
	 * @param cardId
	 * @param user 如果可以杠牌或碰牌就是放杠或者是放碰的用户
	 * @param game
	 * @param canPengOrGangUser
	 */
	private static void notifyUserCanPengOrGang(int cardId, User user, Game game, User canPengOrGangUser) {
		canPengOrGangUser.setLastChuPaiDate(new Date());//记录下他可以碰和杠的时间
		//FIXME 明明可以杠牌提示不可以，需要改一下
		User gamingUser = getGamingUser(game, user.getDirection());
		if(canPengOrGangUser.getPengOrGang()==1){//可以碰牌
			canPengOrGangUser.setCanPeng(true);
			game.setAutoPengCardId(cardId);//可以碰的牌号
			game.setCanPengUser(canPengOrGangUser);//可以碰牌的用户
			game.setFangPengUser(user);
			game.setGameStatus(GAGME_STATUS_OF_PENGPAI);//碰牌
			gamingUser.setCanPeng(true);
			//如果是百搭的上一张牌
			if(cardId/4==game.getFanpai()/4){//该张牌可以杠牌
				gamingUser.setCanGang(true);
				game.setFangGangUser(user);
			}
		}else if(canPengOrGangUser.getPengOrGang()==2){//可以杠牌
			game.setAutoGangCardId(cardId);
			canPengOrGangUser.setCanPeng(true);
			canPengOrGangUser.setCanGang(true);
			gamingUser.setCanPeng(true);
			gamingUser.setCanGang(true);
			game.setFangGangUser(user);
			game.setFangPengUser(user);
			game.setCanGangUser(canPengOrGangUser);//可以杠牌的用户
			game.setGameStatus(GAGME_STATUS_OF_GANGPAI);//杠牌
		}
		notifyUserCanPengOrGang(cardId, game, canPengOrGangUser);
	}

	/**通知用户出牌的方向改变
	 * @param user
	 * @param nextDirection
	 */
	private static void notifyUserDirectionChange(User user,
			String nextDirection) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "playDirection");
		outJsonObject.put("direction", nextDirection);
		outJsonObject.put("description", "出牌的的方向");
		NotifyTool.notifyIoSessionList(GameManager.getSessionListWithRoomNumber(user.getRoomId()+""), outJsonObject);
	}

	/**通知用户可以碰或杠
	 * @param cardId
	 * @param game
	 * @param canPengOrGangUser
	 */
	private static void notifyUserCanPengOrGang(int cardId, Game game,
			User canPengOrGangUser) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("cardId", cardId);
		outJsonObject.put("pengOrGangUser", canPengOrGangUser.getDirection());
		outJsonObject.put(discription, "该用户可以碰牌或杠牌");
		outJsonObject.put(method, "testType");//可以杠
		int pengOrGang = canPengOrGangUser.getPengOrGang();
		if(pengOrGang==1){
			outJsonObject.put(type, "canPeng");//可以碰
		}else if(pengOrGang==2){
			outJsonObject.put(type, "canGang");//可以杠
		}
		String nowDirection = game.getDirec();
		game.setBeforeTingOrGangOrHuDirection(nowDirection);//设置原来的方向
		game.setDirec(canPengOrGangUser.getDirection());//出牌的方向改变
		canPengOrGangUser.getIoSession().write(outJsonObject);//通知该用户可以碰牌或杠牌
	}
	
	
	
	/**分析用户是否可以暗杠
	 * @param user
	 * @param baiDa 百搭的那张牌
	 * @param fanDa 翻搭的那张牌
	 * @return
	 */
	public static List<Integer> isUserCanAnGang(User user,int baiDa,int fanDa){
		List<Integer> cards = user.getCards();
		int type = cards.get(0)/4;
		int total = 0;
		int compareCard = cards.get(0);
		List<Integer> anGangCards = new ArrayList<>();
		for(int i=1;i<cards.size();i++){
			Integer card = cards.get(i);
			//百搭不可以暗杠
			if(card/4!=baiDa/4){//不是百搭
				int currentType = card/4;
				if(type == currentType){
					total++;
					anGangCards.add(card);
					if(total==3){
						anGangCards.add(compareCard);
						break;
					}else if(total==2&&type==fanDa/4){
						anGangCards.add(compareCard);
						break;
					}
				}else{
					type = currentType;
					total = 0;//计数清零
					anGangCards = new ArrayList<>();
					compareCard = card;
				}
			}
		}
		if(anGangCards.size()==3&&anGangCards.get(0)/4==fanDa/4){
			Collections.sort(anGangCards);
			return anGangCards;
		}
		if(anGangCards.size()!=4){
			return new ArrayList<Integer>();
		}
		Collections.sort(anGangCards);
		return anGangCards;
	}
	

	/**分析用户是否可以公杠
	 * @param removeCard
	 * @param user
	 */
	private static List<Integer> analysisUserIsCanGongGang(Integer removeCard, User user) {
		List<Integer> pengCards = user.getUserPengCardsId();//碰出的牌
		int total = 0;
		List<Integer> gongGangCards =  new ArrayList<>();
		for(int i=0;i<pengCards.size();i++){
			Integer card = pengCards.get(i);
			if(card/4==removeCard/4){
				gongGangCards.add(card);
				total ++;
			}
			if(total==3){
				break;
			}
		}
		return gongGangCards;
	}
	
	
	/**分析用户是否可以公杠
	 * @param removeCard
	 * @param user
	 */
	private static List<Integer> analysisUserIsCanGongGangEveryTime(User user) {
		List<Integer> pengCards = user.getUserPengCardsId();//碰出的牌
		List<Integer> cards = user.getCards();
		for(int c = 0 ;c<cards.size();c++){
			Integer nowCard = cards.get(c);
			int total = 0;
			List<Integer> gongGangCards =  new ArrayList<>();
			for(int i=0;i<pengCards.size();i++){
				Integer card = pengCards.get(i);
				if(card/4==nowCard/4){
					gongGangCards.add(card);
					total ++;
				}
				if(total==3){
					user.setGangCard(nowCard);
					return gongGangCards;
				}
			}
		}
		return new ArrayList<>();
	}
	

	/**通知用户赢牌
	 * @param direction 胡牌的方向
	 * @param user  赢牌的玩家
	 * @param game 
	 * @param zhongMaCards 
	 */
	private static void notifyUserWin(User user, Game game,int huCardId) {
		OneRoom room = game.getRoom();
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("type", "hupai");
		outJsonObject.put("userId", user.getId());
		outJsonObject.put("description", "胡牌了");
		outJsonObject.put("method", "playGame");
		outJsonObject.put("huCardId", huCardId);
		outJsonObject.put("huCards", user.getCards());
		outJsonObject.put("hupaiDirection",user.getDirection());
//		outJsonObject.put("zhongMaCards", zhongMaCards);//中码的牌
		JSONArray userJsonArray = getUserJSONArray(room);
		List<Integer> remainCards = game.getRemainCards();
		outJsonObject.put("remainCards", remainCards);//剩余的牌
		outJsonObject.put("users", userJsonArray);
		NotifyTool.notifyIoSessionList(GameManager.getSessionListWithRoomNumber(user.getRoomId()+""), outJsonObject);
		//afterUserWin(user);//在用户赢牌以后分析
	}

	/**得到当前房间里用户的信息
	 * @param room
	 * @return
	 */
	private static JSONArray getUserJSONArray(OneRoom room) {
		JSONArray jsonArray = new JSONArray();
		List<User> userList = room.getUserList();
		for(int i=0;i<userList.size();i++){
			User u = userList.get(i);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(direction, u.getDirection());
			jsonObject.put("pengCards", u.getUserPengCardsId());//碰出的牌
			List<GangCard> gangCards = u.getGangCards();
			if(gangCards.size()>0){
				JSONArray gangCardArray = getJGangCardArray(gangCards);
				jsonObject.put("gangCardArray", gangCardArray);
			}
			jsonObject.put("score", u.getCurrentGameSore());//得到本局的得分
			jsonObject.put("cards", u.getCards());//剩余的牌
			jsonObject.put("userId", u.getId());
			int playerScoreByAdd = u.getCurrentScore();//用户当前的分数
			jsonObject.put("playerScoreByAdd", playerScoreByAdd);
			List<Integer> daList = getDaList(u);
			jsonObject.put("daCard", daList);
			jsonObject.put("isZhuang", u.isBanker());
			jsonArray.put(jsonObject);
		}
		return jsonArray;
	}

	
	
	
	
	/**得到用户的搭的数组
	 * @param user
	 * @return
	 */
	private static List<Integer> getDaList(User user){
		List<Integer> daList =  new ArrayList<>();
		List<Integer> shoDaList = user.getShoDaList();
		for(int i=0;i<shoDaList.size();i++){
			daList.add(1);
		}
		List<Integer> erDaList = user.getErDaList();
		for(int i=0;i<erDaList.size();i++){
			daList.add(2);
		}
		List<Integer> sanDaList = user.getSanDaList();
		for(int i=0;i<sanDaList.size();i++){
			daList.add(3);
		}
		return daList;
	}
	

	
	
	/**查看牌中是否含有红中
	 * @param cards 
	 * @return
	 */
	public static boolean isHaveHongZhong(List<Integer> cards){
		//倒序遍历，因为最大的是红中
		for(int i=cards.size()-1;i>=0;i--){
			Integer card = cards.get(i);
			if(CardsMap.getCardType(card).equals("红中")){
				return true;
			}
		}
		return false;
	}
	
	
	
	/**得到杠牌的类型json数组
	 * @param gangCards
	 * @return
	 */
	private static JSONArray getJGangCardArray(List<GangCard> gangCards) {
		JSONArray gangCardArray =  new JSONArray();
		for(int j=0;j<gangCards.size();j++){
			JSONObject gangCardJSONObject = new JSONObject();
			GangCard gangCard = gangCards.get(j);
			int gangType = gangCard.getType();
			String sGangType = "";
			if(gangType == 0){
				sGangType = "jieGang";
			}else if(gangType == 1){
				sGangType = "anGang";
			}else if(gangType == 2){
				sGangType = "gongGang";
			}
			gangCardJSONObject.put("gangType",sGangType);
			gangCardJSONObject.put("gangCard", gangCard.getCards());
			gangCardArray.put(gangCardJSONObject);
		}
		return gangCardArray;
	}

	
	/**在用户赢牌以后,初始化用户的一些数据,设置新的庄家
	 * @param user,胜利的玩家，或者是最后抓牌的玩家
	 */
	private static void initializeUser(User user,Game game) {
		int alreadyTotalGame = game.getAlreadyTotalGame();
//		int minZuoZhuangTotal = getMinZuoZhuangTotal(game.getUserList());
		try{
			
			List<User> userList = game.getUserList();
			for(int i=0;i<userList.size();i++){
				User u = userList.get(i);
				List<Integer> myPlays = new ArrayList<>();//出去的牌
				u.setMyPlays(myPlays);
				u.setPengCards(new ArrayList<>());
				List<GangCard> gangCards = new ArrayList<>();//杠的牌
				u.setGangCards(gangCards);
				u.setReady(false);
				u.setChuPaiCiShu(0);//出牌次数清零
				u.setShoDaList(new ArrayList<>());
				u.setErDaList(new ArrayList<>());
				u.setSanDaList(new ArrayList<>());
				u.setFangPao(false);
				u.setZiMo(false);
				u.setGangKai(false);
				u.setCanShouDa(true);
				u.setCanNotPengList(new ArrayList<>());
			}
		}finally{
//			if(minZuoZhuangTotal>alreadyTotalGame){
//			}
			game.setAlreadyTotalGame(alreadyTotalGame+1);//设置已经万的游戏局数 
			game.setDirec("");
			game.setBeforeTingOrGangOrHuDirection("");
			game.setHuiFang(new StringBuffer());
			game.setFangPaoUser(null);
		}
	}

	
	
	/**
	 * @param user
	 * @return
	 */
	private static int getMinZuoZhuangTotal(List<User> users){
		int min = Integer.MAX_VALUE;
		for(int i=0;i<users.size();i++){
			User user = users.get(i);
			int zuoZhuangTotal = user.getZuoZhuangTotal();
			if(zuoZhuangTotal<min){
				min = zuoZhuangTotal;
			}
		}
		return min;
	}
	
	

	/**设置新的庄家
	 * @param user
	 */
	private static void setNewBank(User user,Game game) {
		OneRoom room = game.getRoom();
		List<User> userList = room.getUserList();
		String beforeBankDirection = getBeforeUserDirection(userList);
		if(user.getDirection().equals(beforeBankDirection)){//庄家不变
			return;
		}
		String nextDirection = getNextDirection(beforeBankDirection);
		for(User u:userList){
			if(!u.getDirection().equals(nextDirection)){
				u.setBanker(false);
			}else{
				u.setBanker(true);
			}
		}
	}

	
	/**
	 * @return 得到原来的
	 */
	private static String  getBeforeUserDirection(List<User> userList){
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			if(user.isBanker()){
				return user.getDirection();
			}
		}
		return "";
	}
	
	
	/**通知抓牌的方向
	 * @param removeCard
	 * @param nextUser 抓牌的玩家
	 * @param cards 暗杠或者公杠的牌
	 * @param type 0、接杠 1、暗杠 2、公杠
	 */
	private static void notifyUserDrawDirection(Integer removeCard, User nextUser, List<Integer> cards,int type) {
		Game game = getGame(nextUser);
		List<User> userList = getUserListWithGame(game);
		//得到抓牌
		HuiFangUitl.getZhuaPai(game.getHuiFang(),nextUser, removeCard);
		boolean zpLiang = isZPLiang(game.getBaida(), removeCard, nextUser);
		for(User  user : userList){
			JSONObject outJsonObject = new JSONObject();
			//通知他抓到的牌
			outJsonObject.put("description", "抓牌的方向");
			outJsonObject.put("type", "zhuapai");
			outJsonObject.put(method, "playGame");
			outJsonObject.put("direction", nextUser.getDirection());
			outJsonObject.put("isZPLiang", zpLiang);
			if(user.getId()==nextUser.getId()){//本人
				outJsonObject.put("getCardId", removeCard);
				outJsonObject.put("cpTotal", user.getChuPaiCiShu());
				if(cards!=null&&cards.size()>0){//该玩家可以明杠
					outJsonObject.put("isCanGangType", type);
					outJsonObject.put("cards", cards);
					outJsonObject.put("gangCard", user.getGangCard());
				}
			}else{
				outJsonObject.put("getCardId", 1000);//返回一个不存在的数
			}
			NotifyTool.notify(user.getIoSession(), outJsonObject);
		}
		//判断是否二搭亮牌
		if(zpLiang){
			JSONObject jsonObject =  new JSONObject();
			jsonObject.put("method", "showdownCard");
			jsonObject.put("discription", "抓二搭亮");
			jsonObject.put("type", 2);
			jsonObject.put("cardId", removeCard);
			jsonObject.put("direction", nextUser.getDirection());
			jsonObject.put("isShowCard", true);
			NotifyTool.notifyIoSessionList(game.getIoSessionList(), jsonObject);
		}
	}

	
	
	/**
	 * @param baiDa 百搭的那张牌
	 * @param removeCard 抓的那张牌
	 * @param user
	 * @return
	 */
	public static boolean isZPLiang(Integer baiDa,Integer removeCard,User nextUser){
		boolean isLiang = false;
		if(nextUser.getShoDaList().size()==0&&removeCard/4==baiDa/4){
			isLiang = true;
		}
		return isLiang;
	}
	
	
	
	
	private static List<User> getUserListWithGame(Game game){
		OneRoom room = game.getRoom();
		List<User> userList = room.getUserList();
		return userList;
	}
	
	

	/**计算出可以碰牌或杠牌的人
	 * @param cardId 上一个人打出的牌
	 * @param seatMap
	 * @param nowUserId
	 * @param baiDa
	 * @param totalChuPaiCiShu 上一个人的出牌次数
	 * @return
	 */
	public static User getPengOrGangCardUser(int cardId,Map<String, User> seatMap,int nowUserId,int baiDa,int totalChuPaiCiShu){
		Iterator<String> it = seatMap.keySet().iterator();
		int baiDaNumber = baiDa/4;
		while(it.hasNext()){
			String next = it.next();
			User u = seatMap.get(next);
			if(u.getId()!=nowUserId){//不是现在这个人
				List<Integer> cards = u.getCards();
				int total = 0;
				List<Integer> pengOrGangList = new ArrayList<>();
				for(Integer cId:cards){
					if(totalChuPaiCiShu==1){//打首搭的时候可以碰和杠
						if(cId/4==cardId/4){
							total ++;
							pengOrGangList.add(cId);
						}
					}else{
						if(cId/4!=baiDaNumber&&cId/4==cardId/4){
							total ++;
							pengOrGangList.add(cId);
						}
					}
				}
				if(total>=2){
					List<Integer> canNotPengList = u.getCanNotPengList();
					if(canNotPengList.size()>0){//已经放弃过就不在碰了
						Integer canPengId = pengOrGangList.get(0)/4;
						for(int i=0;i<canNotPengList.size();i++){
							Integer canNotPengId = canNotPengList.get(i)/4;
							if(canPengId == canNotPengId){
								return null;
							}
						}
					}
					pengOrGangList.add(cardId);
					u.setPengOrGangList(pengOrGangList);
					if(total==2){
						u.setPengOrGang(1);//用户可以碰牌
					}else if(total == 3){
						u.setPengOrGang(2);//用户可以杠牌
					}
					return u;
				}
			}
		}
		return null;
	}
	
	
	/**得到下一个玩家的方向
	 * @param nowDirection 现在的方向
	 * @return
	 */
	public static String getNextDirection(String nowDirection){
		String direction = "";//方向
		switch (nowDirection) {
			case "east":
				direction = "north";
				break;
			case "north":
				direction = "west";
				break;
			case "west":
				direction = "south";
				break;
			case "south":
				direction = "east";
				break;
			default:
				break;
		}
		return direction;
	}
	
	/**游戏是否结束
	 * @param game
	 * @return
	 */
	public static boolean gameIsOver(Game game){
		OneRoom room  = game.getRoom();
		List<User> userList = room.getUserList();
		int end = room.getEnd();
		int totalEnd = 0;
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			int currentScore = user.getCurrentScore();
			if(currentScore<=0){
				totalEnd = totalEnd + 1;
			}
		}
		
		if(totalEnd>=end){
			return true;
		}
		
		return false;
	}
	
	
}
