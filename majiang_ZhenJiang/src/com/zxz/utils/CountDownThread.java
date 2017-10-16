package com.zxz.utils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.zxz.controller.GameManager;
import com.zxz.controller.RoomManager;
import com.zxz.domain.Game;
import com.zxz.domain.OneRoom;
import com.zxz.domain.PengCard;
import com.zxz.domain.User;
import com.zxz.service.HuiFangUitl;
import com.zxz.service.PlayGameService;
import com.zxz.service.UserService;

/**出牌倒计时线程，用来监听玩家是否出牌,如果没有出牌，系统会替玩家出牌
 * @author Administrator
 */
public class CountDownThread implements Runnable,Constant{

	String roomId;//房间号 
	/**
	 * 当前的局数
	 */
	int currentGame;
	
	private static Logger logger = Logger.getLogger(CountDownThread.class);  
	
	public CountDownThread(String roomId,int currentGame) {
		super();
		this.roomId = roomId;
		this.currentGame = currentGame;
	}

	@Override
	public void run() {
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@SuppressWarnings("deprecation")
			@Override
			public synchronized void run() {
				try {
					//logger.info(".......自动出牌......");
					Game game = getGame();
					if (game == null) {
						logger.fatal("监视进程结束--game==null:" + "RoomId:" + roomId + " currentGame:" + currentGame);
						timer.cancel();//进程结束 
						return;
					}
					Map<Integer, String> gameStatusMap = game.getGameStatusMap();
					String gameStatus = gameStatusMap.get(currentGame);
					if (gameStatus.equals(GAME_END)) {
						logger.fatal("监视进程结束:" + "RoomId:" + roomId + " currentGame:" + currentGame);
						timer.cancel();//进程结束 
						Thread.currentThread().stop();
						return;
					}
					User user = getUser(game);
					if(user==null){
						logger.error("监视线程，用户为空..............");
					}
					Date lastChuPaiDate = user.getLastChuPaiDate();
					long interval = getInterval(lastChuPaiDate, new Date());//得到时间间隔
					int status = game.getGameStatus();//游戏的状态
					logStatus(status, interval, game);
					autoPlayWithGameStatus(game, status, interval);
				} catch (Exception e) {
					e.printStackTrace();
					logger.fatal("监视线程发生错误"+e);
					timer.cancel();//进程结束 
					Thread.currentThread().stop();
				}
			}
		},0,5000);
	}
	
	/**
	 * @param status
	 */
	public void logStatus(int status,long interval,Game game){
		if(status==GAGME_STATUS_OF_CHUPAI){
			User user = game.getSeatMap().get(game.getDirec());
			List<Integer> cards = user.getCards();
			logger.info("当前的状态是值是:"+status+" 状态是【出牌】"+"时间间隔是:"+interval+"毫秒"+"	出牌的方向是:"+game.getDirec()+" 手中牌是:"+cards+ " RID:"+roomId);
		}else if(status==GAGME_STATUS_OF_PENGPAI){
			logger.info("当前的状态是值是:"+status+" 状态是【碰牌】"+"时间间隔是:"+interval+"毫秒"+"	可以碰牌的用户:"+ game.getCanPengUser()+ " RID:"+roomId);
		}else if(status==GAGME_STATUS_OF_GANGPAI){
			logger.info("当前的状态是值是:"+status+" 状态是【接杠】"+"时间间隔是:"+interval+"毫秒"+"	可以接杠的用户"+game.getCanGangUser()+ " RID:"+roomId);
		}else if(status==GAGME_STATUS_OF_ANGANG){
			logger.info("当前的状态是值是:"+status+" 状态是【暗杠】"+"时间间隔是:"+interval+"毫秒"+"	可以暗杠的用户"+game.getCanAnGangUser()+ " RID:"+roomId);
		}else if(status==GAGME_STATUS_OF_GONG_GANG){
			logger.info("当前的状态是值是:"+status+" 状态是【公杠】"+"时间间隔是:"+interval+"毫秒"+"	可以公杠的用户"+game.getCanGongGangUser()+ " RID:"+roomId);
		}else if(status == GAGME_STATUS_OF_WAIT_HU_NEW){
			logger.info("当前的状态是值是:"+status+" 状态是【等待胡牌】"+"时间间隔是:"+interval+"毫秒"+"	可以胡牌的用户"+game.getCanHuUser()+ " RID:"+roomId);
		}else{
			logger.info("未知状态:"+status);
		}
	}
	
	/**暗杠
	 * @param game
	 */
	private void anGang(Game game) {
		User canAnGangUser = game.getCanAnGangUser();
		List<Integer> anGangCards = game.getAnGangCards();
		canAnGangUser.userGangCards(anGangCards);
		//logger.info("自动出牌...暗杠.................:"+NewAI.showPai(anGangCards));
		//记录玩家暗杠的牌
		HuiFangUitl.getAnGang(game.getHuiFang(), canAnGangUser, anGangCards);
		canAnGangUser.recordUserGangCards(1, anGangCards);
		PlayGameService.notifyAllUserAnGang(game, anGangCards,canAnGangUser);//通知所有的玩家杠的牌 
		PlayGameService.modifyUserScoreForAnGang(game, canAnGangUser);//修改玩家暗杠得分
		//该玩家在抓一张牌 
		PlayGameService.userDrawCard(game, canAnGangUser.getDirection(),true);
	}
	
	/**出牌,当用户两次没有出牌后，该用户变成托管状态
	 * @param game
	 */
	public void chuPai(Game game){
		String direc = game.getDirec();
		Map<String, User> seatMap = game.getSeatMap();
		User user = seatMap.get(direc);
		int cardId = user.autoChuPai(game.getBaida());//自动出的牌
		if(cardId<0){
			return;
		}
		//设置用户没有出牌的次数
		int totalNotPlay = user.getTotalNotPlay();
		if(totalNotPlay<2){
			totalNotPlay = totalNotPlay +1;
			user.setTotalNotPlay(totalNotPlay);
			if(totalNotPlay==2){//两次没有出牌启动托管
				logger.info("该用户两次没有出牌，自动托管...:"+user.getUserName());
				user.setAuto(true);//该用户启动托管
				JSONObject autoJsonObject = UserService.getAutoJsonObject(user);
				OneRoom oneRoom = RoomManager.getRoomMap().get(user.getRoomId());
				oneRoom.noticeUsersWithJsonObject(autoJsonObject);
			}
		}
		logger.info("自动出牌....................:"+CardsMap.getCardType(cardId));
		//记录玩家出的牌
		HuiFangUitl.getChuPai(game.getHuiFang(), user, cardId);
		JSONObject outJsonObject = PlayGameService.getChuPaiOutJSONObject(cardId, user);
		NotifyTool.notifyIoSessionList(GameManager.getSessionListWithRoomNumber(user.getRoomId()), outJsonObject);//通知所有用户打出的牌 是什么
		try {
			Thread.currentThread().sleep(TIME_WAIT_CHUPAI);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		PlayGameService.analysis(cardId, user, game);
	}
	
	
	/**碰牌
	 * @param game
	 */
	public void pengPai(Game game){
		game.setBeforeTingOrGangOrHuDirection("");//由于自动碰牌，设置原来可以碰和杠的方向为空,由系统处理
		User canPengUser = game.getCanPengUser();
		Integer autoPengCardId = game.getAutoPengCardId();//自动碰牌的集合 
		List<Integer> cards = canPengUser.getCards();
		List<Integer> pengList = PlayGameService.getPengList(cards, autoPengCardId);//得到可以碰牌的集合
		if(pengList.size()<2){//至少有两颗牌，才对
			//FIXME  为什么会进入到这里
			logger.fatal("碰牌人手中的牌......:"+canPengUser);
			logger.fatal("为什么会进到这里，这里只有一张牌 碰牌的集合是:"+pengList);
			game.setGameStatus(GAGME_STATUS_OF_CHUPAI);
			return;
		}
		canPengUser.userPengCards(pengList);
		pengList.add(autoPengCardId);
		//logger.info("自动碰牌...................."+NewAI.showPai(pengList));
		canPengUser.addUserPengCards(pengList,game.getFangPengUser().getDirection());//用户添加碰出的牌
		//记录玩家碰的牌
		HuiFangUitl.getPengPai(game.getHuiFang(), canPengUser, pengList);
		PlayGameService.notifyAllUserPeng(game, pengList,canPengUser);
		game.setDirec(canPengUser.getDirection());//重新改变游戏的方向
		//碰牌后游戏的状态变为出牌
		game.setGameStatus(GAGME_STATUS_OF_CHUPAI);
		canPengUser.setLastChuPaiDate(new Date());
		canPengUser.setUserCanPlay(true);//用户可以打牌
	}
	
	/**杠牌
	 * @param game
	 */
	public void gangPai(Game game){
		User canGangUser = game.getCanGangUser();
		canGangUser.setCanGang(false);//托管中由系统处理
		Integer autoGangCardId = game.getAutoGangCardId();
		List<Integer> cards = canGangUser.getCards();
		List<Integer> gangCards = PlayGameService.getGangList(cards, autoGangCardId);
		canGangUser.userGangCards(gangCards);
		gangCards.add(autoGangCardId);
		//logger.info("自动杠牌....................:"+NewAI.showPai(gangCards));
		//记录玩家杠的牌
		canGangUser.recordUserGangCards(0, gangCards);
		//记录玩家杠的牌
		HuiFangUitl.getGangPai(game.getHuiFang(), canGangUser, gangCards);
		PlayGameService.modifyUserScoreForJieGang(game, canGangUser);//修改玩家得分
		PlayGameService.notifyAllUserGang(game, gangCards,canGangUser);//通知所有的玩家杠的牌 
		//该玩家在抓一张牌 
		PlayGameService.userDrawCard(game, canGangUser.getDirection(),true);
	}
	
	
	private Game getGame() {
		Map<String, Game> gameMap = GameManager.getGameMap();
		Game game = gameMap.get(roomId);
		return game;
	}
	
	
	public static long getInterval(Date before,Date nowDate){
		
		if(before==null){
			return 0;
		}
		
		long lastTime = before.getTime();
		long nowTime = nowDate.getTime();
		return nowTime-lastTime;
	}
	
	/**得到用户
	 * @param game
	 * @return
	 */
	private User getUser(Game game) {
		String direc = game.getDirec();
		Map<String, User> seatMap = game.getSeatMap();
		User user = seatMap.get(direc);
		return user;
	}
	
	private void autoPlayWithGameStatus(Game game, int status, long interval) {
		switch (status) {
		case GAGME_STATUS_OF_CHUPAI:
			if(interval>30000){
				chuPai(game);//出牌
			}
			break;
		case GAGME_STATUS_OF_PENGPAI:
			if(interval>10000){
				pengPai(game);//碰牌
			}
			break;
		case GAGME_STATUS_OF_GANGPAI:
			if(interval>10000){
				gangPai(game);//接杠
			}
			break;	
		case GAGME_STATUS_OF_ANGANG:	
			if(interval>10000){
				anGang(game);//暗杠
			}
			break;
		case GAGME_STATUS_OF_GONG_GANG:	
			if(interval>10000){
				gongGang(game);//公杠
			}
			break;
		case GAGME_STATUS_OF_WAIT_HU_NEW:	
			if(interval>10000){
				autoHuPai(game);//等待胡牌
			}
			break;
		}
	}

	/**等待胡牌
	 * @param game
	 */
	private void autoHuPai(Game game) {
		User huUser = game.getCanHuUser();
		if(huUser.isZiMo()){
			Integer myGrabCard = huUser.getMyGrabCard();//最后抓的那一张牌
			PlayGameService.userWin(game,myGrabCard,huUser,huUser.isZiMo());
		}else{
			game.getFangPaoUser().setFangPao(true);
			Integer autoHuCardId = game.getAutoHuCardId();
			List<Integer> cards = huUser.getCards();
			cards.add(autoHuCardId);
			Collections.sort(cards);
			PlayGameService.userWin(game,autoHuCardId,huUser,huUser.isZiMo());
		}
	}

	
	/**自动公杠
	 * @param game
	 */
	private void gongGang(Game game) {
		User user = game.getCanGongGangUser();
		Integer cardId = game.getGongGangCardId();
		List<Integer> pengCards = user.getUserPengCardsId();//用户碰的牌
		List<Integer> removeList = PlayGameService.getRemoveList(cardId, pengCards);
		List<PengCard> pengs = user.getPengCards();
		for(int i=0;i<pengs.size();i++){
			PengCard pengCard = pengs.get(i);
			List<Integer> cards = pengCard.getCards();
			if(cards.get(0)/4==removeList.get(0)/4){
				pengs.remove(pengCard);
				break;
			}
		}
		removeList.add(cardId);
		//logger.info("自动公杠....................:"+NewAI.showPai(removeList));
		//从自己的牌中移除公杠的那张牌
		user.removeCardFromGongGang(cardId);
		//记录玩家杠的牌
		user.recordUserGangCards(2, removeList);
		PlayGameService.notifyAllUserGongGang(game, removeList,user);//通知所有的玩家杠的牌 
		PlayGameService.modifyUserScoreForGongGangAndFangGang(game, user);//修改玩家公杠得分
		HuiFangUitl.getGongGang(game.getHuiFang(), user, removeList);
		//该玩家在抓一张牌 
		PlayGameService.userDrawCard(game, user.getDirection(),true);
	}
	
	
}
