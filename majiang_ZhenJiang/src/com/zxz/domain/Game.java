package com.zxz.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.zxz.service.HuiFangUitl;
import com.zxz.utils.Constant;
import com.zxz.utils.MathUtil;

import io.netty.channel.ChannelHandlerContext;

public class Game implements Constant{
	
	private static Logger logger = Logger.getLogger(Game.class);  
	
	OneRoom room;
	private List<Integer> cardList;
	int totalGame;//游戏的局数
	private Map<String, User> seatMap;//座次
	String direc;//出牌方向
	List<Integer> remainCards;//剩余的牌
	private String beforeTingOrGangOrHuDirection;//玩家可以碰或杠或胡的时候出牌的方向
	private int alreadyTotalGame = 0;//已经万的游戏局数,默认为0
	boolean isGameOver = false;//游戏是否结束
	/**
	 * 游戏的状态,0游戏进行中,1游戏等待开始
	 */
	private int status;
	List<Integer> anGangCards;//暗杠的牌
	Integer gongGangCardId;//(公杠)明杠的牌
	private Map<Integer, String> gameStatusMap = new HashMap<>();//当局是否结束
	private User fangGangUser;//放杠的用户
	private User fangPengUser;//放碰的用户
	private User fangPaoUser;//放炮的用户
	private Integer autoPengCardId;//自动的碰牌的牌号
	private Integer autoGangCardId;//自动的杠牌的牌号
	private Integer autoHuCardId;//自动的胡牌的牌号
	private User canPengUser;//可以碰牌的用户
	private User canGangUser;//可以杠牌的用户
	private User canGongGangUser;//公杠的用户
	private User canAnGangUser;//暗杠的用户 
	private User canHuUser;//可以胡牌的玩家
	int gameStatus;//碰牌，听牌，杠牌,出牌
	private boolean isDisband = false; //游戏是否解散 
	private StringBuffer huiFang = new StringBuffer();//游戏回放
	
	
	/**
	 * 胡牌的时候可以杠牌的类型 0 是公杠 1是暗杠
	 */
	private int isCanGangType;
	private int fanpai;//百搭的牌号
	private int baida;//百搭的牌号
	private boolean isOver = false;//游戏是否结束
	
	
	/**
	 * 等待胡牌的时候的状态,7 胡牌 可胡可碰的时候选择了碰 8 可胡可杠的时候选择了杠
	 */
	private int waitHuStatus = 0;
	
	/**
	 * 胡牌的时候选择碰牌或杠牌的人的方向
	 */
	private String waitDirection;
	
	
	/**
	 * 抢杠的那张牌
	 */
	private int qiangGangId = 0;//抢杠的那张牌
	
	
	public Game(OneRoom room) {
		super();
		this.room = room;
		this.totalGame = room.getTotal();
	}

	
	public int getQiangGangId() {
		return qiangGangId;
	}

	public void setQiangGangId(int qiangGangId) {
		this.qiangGangId = qiangGangId;
	}

	public String getWaitDirection() {
		return waitDirection;
	}

	public void setWaitDirection(String waitDirection) {
		this.waitDirection = waitDirection;
	}

	public int getWaitHuStatus() {
		return waitHuStatus;
	}

	public void setWaitHuStatus(int waitHuStatus) {
		this.waitHuStatus = waitHuStatus;
	}

	public int getFanpai() {
		return fanpai;
	}

	public void setFanpai(int fanpai) {
		this.fanpai = fanpai;
	}

	public Integer getAutoHuCardId() {
		return autoHuCardId;
	}

	public void setAutoHuCardId(Integer autoHuCardId) {
		this.autoHuCardId = autoHuCardId;
	}

	public User getFangPaoUser() {
		return fangPaoUser;
	}


	public void setFangPaoUser(User fangPaoUser) {
		this.fangPaoUser = fangPaoUser;
	}

	public boolean isOver() {
		return isOver;
	}


	public void setOver(boolean isOver) {
		this.isOver = isOver;
	}


	public int getBaida() {
		return baida;
	}


	public void setBaida(int baida) {
		this.baida = baida;
	}

	public User getFangPengUser() {
		return fangPengUser;
	}

	public void setFangPengUser(User fangPengUser) {
		this.fangPengUser = fangPengUser;
	}

	public int getIsCanGangType() {
		return isCanGangType;
	}

	public void setIsCanGangType(int isCanGangType) {
		this.isCanGangType = isCanGangType;
	}

	public StringBuffer getHuiFang() {
		return huiFang;
	}

	public void setHuiFang(StringBuffer huiFang) {
		this.huiFang = huiFang;
	}


	/**
	 * @return true 解散  false 没有解散
	 */
	public boolean isDisband() {
		return isDisband;
	}


	public void setDisband(boolean isDisband) {
		this.isDisband = isDisband;
	}


	public User getCanHuUser() {
		return canHuUser;
	}


	public void setCanHuUser(User canHuUser) {
		this.canHuUser = canHuUser;
	}

	public synchronized int getGameStatus() {
		return gameStatus;
	}

	/**设置出牌的状态 ，出牌、听牌、杠牌、暗杠
	 * @param gameStatus
	 */
	public synchronized void setGameStatus(int gameStatus) {
		this.gameStatus = gameStatus;
	}

	
	public User getCanGongGangUser() {
		return canGongGangUser;
	}

	public void setCanGongGangUser(User canGongGangUser) {
		this.canGongGangUser = canGongGangUser;
	}

	public User getCanAnGangUser() {
		return canAnGangUser;
	}

	public void setCanAnGangUser(User canAnGangUser) {
		this.canAnGangUser = canAnGangUser;
	}

	public Integer getAutoGangCardId() {
		return autoGangCardId;
	}

	public void setAutoGangCardId(Integer autoGangCardId) {
		this.autoGangCardId = autoGangCardId;
	}

	public User getCanGangUser() {
		return canGangUser;
	}

	public void setCanGangUser(User canGangUser) {
		this.canGangUser = canGangUser;
	}

	public User getCanPengUser() {
		return canPengUser;
	}

	public void setCanPengUser(User canPengUser) {
		this.canPengUser = canPengUser;
	}

	public Integer getAutoPengCardId() {
		return autoPengCardId;
	}

	public void setAutoPengCardId(Integer autoPengCardId) {
		this.autoPengCardId = autoPengCardId;
	}

	
	/**
	 * 得到游戏中的玩家
	 */
	public User getUserInRoomList(int userId){
		List<User> userList = this.getUserList();
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			if(user.getId()==userId){
				return user;
			}
		}
		logger.fatal("加减分的时候没有找到用户:");
		return null;
	}
	
	
	public User getFangGangUser() {
		return fangGangUser;
	}

	public void setFangGangUser(User fangGangUser) {
		this.fangGangUser = fangGangUser;
	}

	public Map<Integer, String> getGameStatusMap() {
		return gameStatusMap;
	}

	public void setGameStatusMap(Map<Integer, String> gameStatusMap) {
		this.gameStatusMap = gameStatusMap;
	}

	

	public Integer getGongGangCardId() {
		return gongGangCardId;
	}

	public void setGongGangCardId(Integer gongGangCardId) {
		this.gongGangCardId = gongGangCardId;
	}

	public List<Integer> getAnGangCards() {
		return anGangCards;
	}

	public void setAnGangCards(List<Integer> anGangCards) {
		this.anGangCards = anGangCards;
	}

	/**游戏的状态，等待游戏开始或游戏进行中
	 * @return
	 */
	public synchronized int getStatus() {
		return status;
	}

	/**游戏等待或者是游戏开始
	 * @param status
	 */
	public synchronized void setStatus(int status) {
		this.status = status;
	}

	public boolean isGameOver() {
		return isGameOver;
	}

	public void setGameOver(boolean isGameOver) {
		this.isGameOver = isGameOver;
	}

	public int getAlreadyTotalGame() {
		return alreadyTotalGame;
	}

	public void setAlreadyTotalGame(int alreadyTotalGame) {
		this.alreadyTotalGame = alreadyTotalGame;
		//设置房间已经玩的局数
		this.room.setAlreadyTotalGame(alreadyTotalGame);
	}

	public String getBeforeTingOrGangOrHuDirection() {
		return beforeTingOrGangOrHuDirection;
	}

	public void setBeforeTingOrGangOrHuDirection(String beforeTingOrGangOrHuDirection) {
		this.beforeTingOrGangOrHuDirection = beforeTingOrGangOrHuDirection;
	}

	public void playGame() {
		room.setUse(true);//房间已经使用 
		cardList = MathUtil.creatRandom(0, 135);//生成牌
		remainCards = dealCard(room,cardList);//发牌
//		this.baida = MathUtil.getBaiDa();//得到百搭的那张牌
		this.fanpai = remainCards.remove(0);
		this.baida =  MathUtil.getNextCard(fanpai);//得到百搭的那张牌
		HuiFangUitl.getBaiDai(getHuiFang(), baida);
//		seatMap = generateSeat(room);//得到座次Map
		direc = getDirPaly(room);//出牌的方向
		//修改用户的当前的局数
		moifyUserCurrentGame(this);
		logger.info("发出牌后，玩家剩余的牌："+remainCards+",百搭:"+baida + " 总张数:"+remainCards.size());
	}
	
	
	
	/**修改用户当前的局数,并且设置当前的分数
	 * @param game
	 */
	public void moifyUserCurrentGame(Game game){
		List<User> userList = game.getUserList();
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			user.setCurrentGame(user.getCurrentGame()+1);
			Score score = new Score();
			Map<Integer, Score> scoreMap = user.getScoreMap();
			scoreMap.put(user.getCurrentGame(), score);
		}
	}


	public List<Integer> getRemainCards() {
		return remainCards;
	}


	public void setRemainCards(List<Integer> remainCards) {
		this.remainCards = remainCards;
	}


	public OneRoom getRoom() {
		return room;
	}

	public void setRoom(OneRoom room) {
		this.room = room;
	}

	public List<Integer> getCardList() {
		return cardList;
	}


	public void setCardList(List<Integer> cardList) {
		this.cardList = cardList;
	}


	public int getTotalGame() {
		return totalGame;
	}


	public void setTotalGame(int totalGame) {
		this.totalGame = totalGame;
	}


	public Map<String, User> getSeatMap() {
		return seatMap;
	}


	public void setSeatMap(Map<String, User> seatMap) {
		this.seatMap = seatMap;
	}


	public synchronized String getDirec() {
		return direc;
	}


	public synchronized void setDirec(String direc) {
		this.direc = direc;
	}


	/**得到出牌的方向
	 * @param room
	 * @return
	 */
	private String getDirPaly(OneRoom room){
		List<User> userList = room.getUserList();
		for(User user:userList){
			if(user.isBanker()){
				return user.getDirection();
			}
		}
		return "";
	}
	
	
	/**生成座次
	 * @param game 
	 * @param room
	 * @return 
	 */
	public Map<String, User> generateSeat() {
		List<User> users = room.getUserList();
		Map<String, User> map = new HashMap<String, User>();
		for(int i=0;i<users.size();i++){
			User user = users.get(i);
			String direction = user.getDirection();
			map.put(direction, user);
		}
		this.seatMap = map;
		return map;
	}
	
	
	/**得到所有的用户
	 * @return
	 */
	public List<User> getUserList(){
		List<User> userList = room.getUserList();
		return userList;
	}
	
	
	
	/**得到房间里所有的IoSession
	 * @return
	 */
	public List<ChannelHandlerContext> getIoSessionList(){
		List<User> userList = getUserList();
		List<ChannelHandlerContext> sessionList =  new ArrayList<>();
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			sessionList.add(user.getIoSession());
		}
		return sessionList;
	}
	
	
	/**
	 * 得到基础的回放信息
	 */
	public void getBaseHuiFang(List<User> userList){
		getHuiFang().append("f:rInfo:"+room.getRoomNumber()+","+(alreadyTotalGame+1)+","+totalGame+","+0+","+room.getWanFa()+","+room.getChong()+","+room.getZhama()+"|f:info:");
		
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			if(i==userList.size()-1){
				HuiFangUitl.getBase(getHuiFang(), user,true);
			}else{
				HuiFangUitl.getBase(getHuiFang(), user,false);
			}
		}
	}
	
	/**发牌
	 * @param room
	 * @param cardList
	 * @return remainList 剩余的牌
	 */
	private List<Integer> dealCard(OneRoom room, List<Integer> cardList) {
		List<User> userList = room.getUserList();
		int begin = 0 ;
		getBaseHuiFang(userList);
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			List<Integer> cards = new LinkedList<Integer>();
			boolean banker = user.isBanker();//如果是庄家
			if(banker){
				int end = begin +14;
				for(int j=begin;j<end;j++){
					cards.add(cardList.get(j));
					begin ++;
				}
			}else{
				int end = begin +13;
				for(int j=begin;j<end;j++){
					cards.add(cardList.get(j));
					begin ++;
				}
			}
			Collections.sort(cards);//给牌排序
			user.setCards(cards);
			//记录发牌
			HuiFangUitl.getFaPai(getHuiFang(), user, cards);
		}
		List<Integer> remainList = new LinkedList<Integer>();
		for (int i=53;i<cardList.size();i++){
			remainList.add(cardList.get(i));
		}
		return remainList;
	}
	

	/**得到游戏中的玩家
	 * @param direction 玩家的方向
	 * @return
	 */
	public  User getGamingUser(String direction){
		User user = seatMap.get(direction);
		return user;
	}
	
	
	public int getWanFa(){
		int wanFa = this.room.getWanFa();
		return wanFa;
	}
	
	
	@Override
	public String toString() {
		return "Game [autoPengCardId=" + autoPengCardId + ", autoGangCardId=" + autoGangCardId + ", canPengUser="
				+ canPengUser + ", canGangUser=" + canGangUser + ", canGongGangUser=" + canGongGangUser
				+ ", canAnGangUser=" + canAnGangUser + "]";
	}
	
	
	
}
