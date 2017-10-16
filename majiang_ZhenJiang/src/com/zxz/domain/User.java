package com.zxz.domain;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.gs.NewAI;
import com.mysql.jdbc.StringUtils;
import com.zxz.service.UserService;
import com.zxz.utils.CardsMap;
import com.zxz.utils.Constant;
import com.zxz.utils.HuPai;
import com.zxz.utils.ScoreType;

import io.netty.channel.ChannelHandlerContext;

public class User implements Constant{
	
	private static Logger logger = Logger.getLogger(User.class);  
	
	int id;
	/******************微信的所有东西**********************/
	String openid;//普通用户的标识，对当前开发者帐号唯一
	String nickName;//昵称 
	private String sex;//普通用户性别，1为男性，2为女性
	String province	;//普通用户个人资料填写的省份
	String city	;//普通用户个人资料填写的城市
	String country	;//国家，如中国为CN
	String unionid;//用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的。
	String headimgurl;//头像
	String refreshToken;//refresh_token拥有较长的有效期（30天），当refresh_token失效的后，需要用户重新授权。
	/*******************微信的所有东西*********************/
	Date createDate;//注册时间
	private int roomCard;//房卡的数量
	String userName;
	String password;
	private String roomId;//房间号 
	private boolean ready;//是否准备
	private ChannelHandlerContext ioSession;
	private List<Integer> cards;//自己手中的牌
	private String direction;//东 西 南 北
	private boolean banker;//庄
	private boolean isAuto = false;//用户是否托管
	private boolean isDropLine = false;//用户是否掉线
	private int pengOrGang=0;// 0 不可以牌和杠 1 可以碰 2 可以杠 
	private List<Integer> myPlays = new ArrayList<>();//已经出的的牌
//	private List<Integer> pengCards = new ArrayList<>();//碰出的牌
	private List<PengCard> pengCards = new ArrayList<>();//碰出的牌
	private List<GangCard> gangCards = new ArrayList<>();//杠出去的牌
	private Integer myGrabCard;//我抓到的牌
	private boolean isCanPeng = false;//是不是可以碰
	private boolean isCanGang = false;//是不是可以杠
	/**
	 * 得分记录的集合,key:第几局，Score 该局的得分
	 */
	private Map<Integer, Score> scoreMap =  new LinkedHashMap<>();
	/**
	 * 当前所在的局数
	 */
	private int currentGame;
	private int currentScore = 0;//当前玩家的分数
	private Date lastChuPaiDate;//最后一次的出牌时间
	private List<Integer> pengOrGangList;//可以碰或者杠的牌
	private boolean isUserCanPlay = false;//false 说明:此变量是为了，解决当用户托管的时候，用户出牌和系统托管的时候，一起出牌，造成打两次牌的情况
	private int totalNotPlay = 0;//用户没有出牌的次数
	private boolean isWin = false;//用户是否赢牌
	private List<Integer> zhongMaCards;// 中码的牌
	private Integer huCardId = -1;//胡牌的时候抓的最后一张牌
	private Integer winNumber = 0;//设置赢牌的次序 
	private int isAgreeDisbandType = 0;//是否同意解散房间 0 选择中 1同意 2不同意
	private int totalRequetDisbanRoom = 0;//设置解散房间的次数
	private int chuPaiCiShu = 0;//是不是第一次出牌
	private boolean isFangZhu = false;//是否是房主
	private boolean isCanSanDa =  false;//是否可以三搭
	private int zuoZhuangTotal  = 0;//坐庄次数
	
	private List<Integer> shoDaList = new ArrayList<Integer>();//首搭的个数
	private List<Integer> erDaList = new ArrayList<Integer>();//二搭的个数
	private List<Integer> sanDaList = new ArrayList<Integer>();;//三搭 的个数
	private List<Integer> canNotPengList = new ArrayList<Integer>();;//不可以碰的牌
	
	private boolean isFangPao =  false;//是否放炮
	private boolean isZiMo = false; //是否自摸
	private boolean isGangKai = false; //是否杠上开花
	
	
	private int gangCard;//可以公杠的那张牌
	
	private boolean isCanShouDa = true;//是否可以首搭
	
	public boolean isCanShouDa() {
		return isCanShouDa;
	}

	public void setCanShouDa(boolean isCanShouDa) {
		this.isCanShouDa = isCanShouDa;
	}

	public int getIsAgreeDisbandType() {
		return isAgreeDisbandType;
	}

	public void setIsAgreeDisbandType(int isAgreeDisbandType) {
		this.isAgreeDisbandType = isAgreeDisbandType;
	}

	public int getGangCard() {
		return gangCard;
	}

	public void setGangCard(int gangCard) {
		this.gangCard = gangCard;
	}

	public List<Integer> getCanNotPengList() {
		return canNotPengList;
	}

	public void setCanNotPengList(List<Integer> canNotPengList) {
		this.canNotPengList = canNotPengList;
	}

	public boolean isDropLine() {
		return isDropLine;
	}

	public void setDropLine(boolean isDropLine) {
		this.isDropLine = isDropLine;
	}


	public boolean isGangKai() {
		return isGangKai;
	}


	public void setGangKai(boolean isGangKai) {
		this.isGangKai = isGangKai;
	}


	public boolean isZiMo() {
		return isZiMo;
	}


	public void setZiMo(boolean isZiMo) {
		this.isZiMo = isZiMo;
	}


	public boolean isFangPao() {
		return isFangPao;
	}


	public void setFangPao(boolean isFangPao) {
		this.isFangPao = isFangPao;
	}


	


	public List<Integer> getShoDaList() {
		return shoDaList;
	}

	public void setShoDaList(List<Integer> shoDaList) {
		this.shoDaList = shoDaList;
	}

	public List<Integer> getSanDaList() {
		return sanDaList;
	}

	public void setSanDaList(List<Integer> sanDaList) {
		this.sanDaList = sanDaList;
	}

	public List<Integer> getErDaList() {
		return erDaList;
	}

	public void setErDaList(List<Integer> erDaList) {
		this.erDaList = erDaList;
	}

	public int getZuoZhuangTotal() {
		return zuoZhuangTotal;
	}


	public void setZuoZhuangTotal(int zuoZhuangTotal) {
		this.zuoZhuangTotal = zuoZhuangTotal;
	}


	public boolean isCanSanDa() {
		return isCanSanDa;
	}


	public void setCanSanDa(boolean isCanSanDa) {
		this.isCanSanDa = isCanSanDa;
	}


	public boolean isFangZhu() {
		return isFangZhu;
	}


	public void setFangZhu(boolean isFangZhu) {
		this.isFangZhu = isFangZhu;
	}


	public int getCurrentScore() {
		return currentScore;
	}


	public void setCurrentScore(int currentScore) {
		this.currentScore = currentScore;
	}


	public int getChuPaiCiShu() {
		return chuPaiCiShu;
	}


	public void setChuPaiCiShu(int chuPaiCiShu) {
		this.chuPaiCiShu = chuPaiCiShu;
	}


	public int getTotalRequetDisbanRoom() {
		return totalRequetDisbanRoom;
	}


	public void setTotalRequetDisbanRoom(int totalRequetDisbanRoom) {
		this.totalRequetDisbanRoom = totalRequetDisbanRoom;
	}


	

	public Integer getWinNumber() {
		return winNumber;
	}

	public void setWinNumber(Integer winNumber) {
		this.winNumber = winNumber;
	}




	public Integer getHuCardId() {
		return huCardId;
	}


	public void setHuCardId(Integer huCardId) {
		this.huCardId = huCardId;
	}


	public List<Integer> getZhongMaCards() {
		return zhongMaCards;
	}


	public void setZhongMaCards(List<Integer> zhongMaCards) {
		this.zhongMaCards = zhongMaCards;
	}


	public boolean isWin() {
		return isWin;
	}


	public void setWin(boolean isWin) {
		this.isWin = isWin;
	}


	public int getTotalNotPlay() {
		return totalNotPlay;
	}


	public void setTotalNotPlay(int totalNotPlay) {
		this.totalNotPlay = totalNotPlay;
	}


	/**
	 * 清空所有的属性
	 */
	public void clearAll(){
		this.roomId = null;
		this.isAuto = false;
		this.currentGame = 0;
		this.setPengCards(new ArrayList<PengCard>());
		List<GangCard> gangCards = new ArrayList<>();//杠的牌
		this.setGangCards(gangCards);
		this.myGrabCard = null;//我找到的牌
		this.isCanPeng = false;//是不是可以碰
		this.isCanGang = false;//是不是可以杠
		this.banker = false;//是否是房主
		this.currentGame = 0;//当前的局数设置为0
		this.myPlays = new ArrayList<Integer>();//打出的牌清空
		this.setScoreMap(new LinkedHashMap<Integer,Score>());//清空成绩
		this.totalNotPlay = 0;//用户没有出牌的次数清零
		this.ready = false;//取消准备
		this.isWin = false;//没有赢牌
		this.setZhongMaCards(new ArrayList<Integer>());//中码的牌
		this.huCardId = -1;
		this.winNumber = 0;
		this.chuPaiCiShu = 0;
		this.isZiMo = false;
		this.isGangKai = false;
		this.canNotPengList = new ArrayList<>();
		totalRequetDisbanRoom = 0;
	}
	
	public synchronized boolean isUserCanPlay() {
		return isUserCanPlay;
	}

	public synchronized void setUserCanPlay(boolean isUserCanPlay) {
		this.isUserCanPlay = isUserCanPlay;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getSex() {
		if(sex==null){
			return "0";//男
		}else if(sex.equals("2")){
			return "1";//女
		}else if(sex.equals("1")){
			return "0";//男
		}
		return sex==null?"0":sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public List<Integer> getPengOrGangList() {
		return pengOrGangList;
	}

	public void setPengOrGangList(List<Integer> pengOrGangList) {
		this.pengOrGangList = pengOrGangList;
	}

	public Date getLastChuPaiDate() {
		return lastChuPaiDate;
	}

	public void setLastChuPaiDate(Date lastChuPaiDate) {
		this.lastChuPaiDate = lastChuPaiDate;
	}
	

	public List<PengCard> getPengCards() {
		return pengCards;
	}

	
	/**得到用户所有碰的牌
	 * @return
	 */
	public List<Integer> getUserPengCardsId(){
		List<Integer> list = new ArrayList<>();
		for(int i=0;i<pengCards.size();i++){
			PengCard pengCard = pengCards.get(i);
			List<Integer> cards2 = pengCard.getCards();
			for(int j=0;j<cards2.size();j++){
				list.add(cards2.get(j));
			}
		}
		return list;
	}
	
	/**得到用户所有碰的牌方向
	 * @return
	 */
	public List<String> getUserPengDirs(){
		List<String> dirs = new ArrayList<>();
		for(int i=0;i<pengCards.size();i++){
			PengCard pengCard = pengCards.get(i);
			dirs.add(pengCard.getChuDir());
		}
		return dirs;
	}
	
	
	public void setPengCards(List<PengCard> pengCards) {
		this.pengCards = pengCards;
	}

	public List<GangCard> getGangCards() {
		return gangCards;
	}

	public void setGangCards(List<GangCard> gangCards) {
		this.gangCards = gangCards;
	}

	public int getCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(int currentGame) {
		this.currentGame = currentGame;
	}

	public Map<Integer, Score> getScoreMap() {
		return scoreMap;
	}

	public void setScoreMap(Map<Integer, Score> scoreMap) {
		this.scoreMap = scoreMap;
	}

	public int getRoomCard() {
		return roomCard;
	}

	public void setRoomCard(int roomCard) {
		this.roomCard = roomCard;
	}

	public boolean isCanPeng() {
		return isCanPeng;
	}

	public void setCanPeng(boolean isCanPeng) {
		this.isCanPeng = isCanPeng;
	}

	public boolean isCanGang() {
		return isCanGang;
	}

	public void setCanGang(boolean isCanGang) {
		this.isCanGang = isCanGang;
	}

	public Integer getMyGrabCard() {
		return myGrabCard;
	}

	public void setMyGrabCard(Integer myGrabCard) {
		this.myGrabCard = myGrabCard;
	}

	public List<Integer> getMyPlays() {
		return myPlays;
	}

	public void setMyPlays(List<Integer> myPlays) {
		this.myPlays = myPlays;
	}



	public int getPengOrGang() {
		return pengOrGang;
	}

	public void setPengOrGang(int pengOrGang) {
		this.pengOrGang = pengOrGang;
	}

	public String getNickName() {
		if(!StringUtils.isNullOrEmpty(nickName)){
			if(nickName.length()>4){
				return nickName.substring(0, 4);
			}else{
				return nickName;
			}
		}
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public boolean isAuto() {
		return isAuto;
	}

	public void setAuto(boolean isAuto) {
		this.isAuto = isAuto;
	}

	public User() {
	}
	
	public boolean isBanker() {
		return banker;
	}
	public void setBanker(boolean banker) {
		this.banker = banker;
		if(banker==true){
			this.zuoZhuangTotal ++;
		}
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public List<Integer> getCards() {
		return cards;
	}
	public void setCards(List<Integer> cards) {
		this.cards = cards;
	}
	public ChannelHandlerContext getIoSession() {
		return ioSession;
	}
	public void setIoSession(ChannelHandlerContext ioSession) {
		this.ioSession = ioSession;
	}
	public boolean isReady() {
		return ready;
	}
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return getNickName();
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public User(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}
	
	
	/**得到用户的详细信息,包括,是否托管、已经打出的牌、手中的牌、碰的牌、杠的牌、自己方向 
	 * @param showMyCards 是否显示我手中的牌，true显示,false不显示
	 * @return
	 */
	public JSONObject getMyInfo(boolean showMyCards){
		JSONObject infoJSONObject = new JSONObject();
		infoJSONObject.put("isAuto", isAuto);//是否托管
		infoJSONObject.put("myPlays", myPlays);//已经打出的牌
		if(showMyCards){
			infoJSONObject.put("cards", cards);//手中的牌
		}
		infoJSONObject.put("pengCards", getUserPengCardsId());
		infoJSONObject.put("chuDirs", getUserPengDirs());
		infoJSONObject.put("shoDa", getShoDaList());
		infoJSONObject.put("erDa", getErDaList());
		infoJSONObject.put("sanDa", getSanDaList());
		JSONArray gangCardArray = new JSONArray();//杠牌的数组
		for(int i=0;i<gangCards.size();i++){
			GangCard gangCard = gangCards.get(i);
			JSONObject gangJsonObject = new JSONObject();
			int type = gangCard.getType();
			if (type==0) {
				gangJsonObject.put("gangType", "jieGang");
			}else if(type == 1){
				gangJsonObject.put("gangType", "anGang");
			}else if(type == 2){
				gangJsonObject.put("gangType", "gongGang");
			}
			List<Integer> gangCards = gangCard.getCards();
			gangJsonObject.put("gangCards", gangCards);
			gangCardArray.put(gangJsonObject);
		}
		infoJSONObject.put("userid",id);
		infoJSONObject.put("headimgurl", headimgurl);
		infoJSONObject.put("userName", getUserName());
		infoJSONObject.put("gangCardArray", gangCardArray);
		infoJSONObject.put("direction", direction);
		infoJSONObject.put("ready", ready);
		infoJSONObject.put("ip", getIp());//ip地址
		infoJSONObject.put("isD", isDropLine());//ip地址
//		int playerScoreByAdd = UserService.getUserCurrentGameScore(this);//当前的分数
		int playerScoreByAdd = this.getCurrentScore();
		infoJSONObject.put("playerScoreByAdd", playerScoreByAdd);
		infoJSONObject.put("sex", getSex());
		infoJSONObject.put("isFz", isFangZhu());//是否房主
		return infoJSONObject;
	}
	
	
	
	
	@Override
	public String toString() {
		return nickName+": 坐庄次数:"+zuoZhuangTotal+" 当前筹码："+currentScore;
	}

	/**出牌
	 * @param cardId 出牌的牌号
	 */
	public int chuPai(Integer cardId) {
		int indexOfCardID = cards.indexOf(cardId);
		if(indexOfCardID<0){
			return -1;
		}
	    cards.remove(indexOfCardID);//把该张牌移除
		chuPaiCiShu = chuPaiCiShu + 1;
		//如果出的牌正好是我抓到的牌，则把我抓到的牌置空
		if(myGrabCard!=null&&cardId==myGrabCard){
			myGrabCard = null;
		}
		return cardId;
	}
	
	
	/**智能出牌
	 * @return
	 */
	public  int autoChuPai(int baiDa){
		if(!isUserCanPlay){
			return -100;
		}
		isUserCanPlay = false; //现在不可以打牌了
		Integer myGrabCard = this.getMyGrabCard();//抓到的牌
		boolean containMyGrabCard = cards.contains(myGrabCard);
		if(!containMyGrabCard){
			myGrabCard = null;
		}
		if(chuPaiCiShu==0){//如果是第一次出牌
			int bNumber = baiDa /4 ;
			chuPaiCiShu ++;
			for(int i=0;i<cards.size();i++){
				Integer c = cards.get(i);
				if(c/4==bNumber){
					cards.remove(c);
					return c;
				}
			}
		}
		int[] array = getCardsArray();
		if(myGrabCard != null && myGrabCard/4!=baiDa/4){//不为空且不是红中
			List<Integer> cards = this.getCards();
			boolean win = isUserTingPai(myGrabCard,cards,baiDa);
			if(win){//如果听牌
				//logger.info("自动出牌的时候检测到听牌,用户手中牌是："+ NewAI.showPai(array));
				cards.remove(myGrabCard);
				int myCrabCardNumber = myGrabCard;
				this.setMyGrabCard(null);//设置我抓到的牌为空
				return myCrabCardNumber;
			}
		}
		Integer maxWeightCardId = getWitchCardToPlay(array);
		cards.remove(maxWeightCardId);
		chuPaiCiShu ++;
		return maxWeightCardId;
	}


	/**检测用户是否听牌
	 * @param myGrabCard
	 * @return
	 */
	public static boolean isUserTingPai(Integer myGrabCard,List<Integer> cards,int baiDa) {
		logger.info("抓到的牌是:"+myGrabCard+" "+ CardsMap.getCardType(myGrabCard));
		int[] tingPaiCars = getCardsWithRemoveDrawCardsWithReplaceHongZhong(myGrabCard,cards,baiDa);//计算要听的牌
		HuPai huPai = new HuPai();
		boolean win = huPai.isHu(tingPaiCars, baiDa);
		return win;
	}

	
	
	/**在检测到用户碰牌的时候，检测用户是否听牌
	 * @return
	 */
	public static boolean isUserTingPaiOfPengOrGang(List<Integer> cards,int baiDa) {
		int[] tingPaiCars = getCardsWithAddHongZhong(cards,baiDa);//计算要听的牌
		HuPai huPai = new HuPai();
		boolean win = huPai.isHu(tingPaiCars, baiDa);
		return win;
	}

	/**在用户碰牌或者是杠牌的时候检测用户是否听牌,从而返回牌
	 * @return
	 */
	private static int[] getCardsWithAddHongZhong(List<Integer> cards,int baiDa){
		List<Integer> returnCards = new LinkedList<Integer>();
		for(int i=0;i<cards.size();i++){
			Integer card = cards.get(i);
			returnCards.add(card);
		}
		returnCards.add(baiDa);
		Collections.sort(returnCards);
		int[] array = new int[returnCards.size()];
		for(int i=0;i<returnCards.size();i++){
			array[i] = returnCards.get(i);
		}
		return array;
	}
	
	

	/**计算出哪一张牌最合适
	 * @param array
	 * @return
	 */
	public static Integer getWitchCardToPlay(int[] array) {
		return array[array.length-1];
	}
	
	
	/**计算出哪一张牌最合适
	 * @param array
	 * @return
	 */
	public static Integer getWitchCardToPlay1(int[] array) {
		//NewAI.showPai(array);
		List<Integer> mycards = NewAI.getListWithoutHongZhong(array);//没有红中的牌
		Map<Integer,Integer> cardWeightMap = new LinkedHashMap<>();
		for(int i=0;i<mycards.size();i++){
			Integer cardId = mycards.get(i);//当前的牌号
			Integer preCardId;//前一张牌
			Integer nextCardId;//后一张牌
			if(i==0){
				preCardId = -1000;
			}else{
				preCardId = mycards.get(i-1);
			}
			if(i==mycards.size()-1){
				nextCardId = 1000;
			}else{
				nextCardId = mycards.get(i+1);
			}
			int intervalPre= NewAI.getInterval(preCardId,cardId);
			int intervalNext = NewAI.getInterval(cardId, nextCardId);
			int weight = getCardWeight(intervalPre, intervalNext);
			cardWeightMap.put(cardId, weight);
		}
		Iterator<Integer> iterator = cardWeightMap.keySet().iterator();
		int weight = Integer.MIN_VALUE ;//最大权重的那张牌
		Integer maxWeightCardId = 0;
		while(iterator.hasNext()){
			Integer cardId = iterator.next();
			Integer cardWeight = cardWeightMap.get(cardId);
			//System.out.println(CardsMap.getCardType(cardId)+" 权重 : "+cardWeight);
			if(cardWeight>=weight){
				weight = cardWeight;
				maxWeightCardId = cardId;
			}
		}
		//System.out.println("MaxWeight:"+weight);
//		LinkedHashMap<Integer, Integer> sortByValue = MapUtil.sortByValue(cardWeightMap);
//		Iterator<Integer> iterator2 = sortByValue.keySet().iterator();
//		while(iterator2.hasNext()){
//			Integer cId = iterator2.next();//牌号
//			int index = mycards.indexOf(cId);
//			System.out.println("cId:"+cId+" index:"+index);
//			if(index>=2){
//				int preOne = mycards.get(index-1);
//				int preTwo = mycards.get(index-2);
//				
//				
//			}
//		}
		
		return maxWeightCardId;
	}

	
	/**移除掉抓到的牌，并且用一颗红中来替换
	 * @return
	 */
	private static int[] getCardsWithRemoveDrawCardsWithReplaceHongZhong(Integer myDrawCard,List<Integer> cards,int baiDa){
		//Algorithm2.showPai(cards);
		List<Integer> returnCards = new LinkedList<Integer>();
		for(int i=0;i<cards.size();i++){
			Integer card = cards.get(i);
			if(card!=myDrawCard){
				returnCards.add(card);
			}
		}
		returnCards.add(baiDa);
		Collections.sort(returnCards);
		int[] array = new int[returnCards.size()];
		for(int i=0;i<returnCards.size();i++){
			array[i] = returnCards.get(i);
		}
		return array;
	}
	
	
	/**判断用户是否听牌
	 * @param array
	 * @return
	 */
	public boolean isTingPai(int array[]){
		
		
		return false;
	}
	
	
	/**得到该张牌的权重
	 * @param intervalPre
	 * @param intervalNext
	 * @return
	 */
	private static int getCardWeight(int intervalPre, int intervalNext) {
		int weight = 0;
		//如果是孤立的一张牌
		if(intervalPre==-1&&intervalNext==-1){
			weight = 10000;
		}else{
			if(intervalPre==1){//可能是一句话
				weight = weight-5000;
			}else if(intervalPre==0){//可能组成坎
				weight = weight - 10000;
			}else if(intervalPre == 2){
				weight = weight - 3000;
			}else{
				weight = weight+intervalPre;
			}
			if(intervalNext == 1){//可能是一句话
				weight = weight - 5000;
			}else if(intervalNext==0){//可能组成坎
				weight = weight - 10000;
			}else if(intervalNext==2){
				weight = weight - 3000;
			}else{
				weight = weight+intervalNext;
			}
		}
		return weight;
	}
	
	
	/**抓牌
	 * @param cardId
	 */
	public HuPai zhuaPai(int cardId,int baiDa){
		cards.add(cardId);
		Collections.sort(cards);//抓完牌排序
		setLastChuPaiDate(new Date());//设置本次的出牌时间
		this.myGrabCard = cardId;//当前抓到的牌
		if(chuPaiCiShu == 0){//如果是第一次抓牌
			return UserService.userWinTianHu(cards,baiDa);
		}
		HuPai huPai = new HuPai();
		huPai.isHu(cards, baiDa);
		return huPai;
	}
	
	
	/**在出去的牌的集合中添加一张牌
	 * @param card
	 */
	public void addMyPlays(int card){
		myPlays.add(card);
	}
	

	
	/**碰牌
	 * @param list 需要碰掉的牌
	 */
	public void userPengCards(List<Integer> list){
		for(int i=0;i<list.size();i++){
			Integer removeCard = list.get(i);
			cards.remove(removeCard);
		}
		setCanPeng(false);
	}
	
	
	/**添加用户碰出的牌
	 * @param cards
	 */
	public void addUserPengCards(List<Integer> cards,String chuDir){
		PengCard pengCard = new PengCard();
		pengCard.addPengCard(cards, chuDir);
		this.pengCards.add(pengCard);
	}
	
	/**杠牌
	 * @param list 需要杠掉的牌
	 */
	public void userGangCards(List<Integer> list){
		for(int i=0;i<list.size();i++){
			Integer removeCard = list.get(i);
			cards.remove(removeCard);
		}
		setCanGang(false);
	}
	
	/**接杠加分
	 */
	public int addScoreForCommonGang(int addScore){
		Score score = scoreMap.get(currentGame);
		score.setJieGangTotal(score.getJieGangTotal()+1);//接杠的次数加1
		currentScore = currentScore + addScore;
		return addScore;
	}
	
	
	/**修改玩家自摸的次数
	 * @return
	 */
	public int addZiMoTotal(){
		Score score = scoreMap.get(currentGame);
		int zimoTotal = score.getZimoTotal();
		int now = zimoTotal + 1;
		score.setZimoTotal(now);//修改玩家自摸的次数
		return now;
	}
	
	/**修改玩家自摸的次数
	 * @return
	 */
	public int addFangChongTotal(){
		Score score = scoreMap.get(currentGame);
		int fangchongTotal = score.getFangChongTotal();
		int now = fangchongTotal + 1;
		score.setFangChongTotal(now);//修改玩家自摸的次数
		return now;
	}
	
	
	/**公杠减分
	 */
	public int reduceScoreForFangGang(int wanFa){
		int result = 0;
		Score score = scoreMap.get(currentGame);
		score.setFangGangTotal(score.getFangGangTotal()+1);//放杠的次数减1
		if(wanFa==JIN_CHONG){
			result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_GANG);
			currentScore = currentScore -result;
			return result;
		}else{
			if(currentScore>=Math.abs(ScoreType.REDUCE_SCORE_FOR_GANG)){
				result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_GANG);
				currentScore = currentScore -result;
				return result;
			}else{
				result = currentScore;
				currentScore = 0;
				return result;
			}
		}
	}
	
	
	/**
	 * 明杠加分
	 */
	public int addScoreForMingGang(int addScore){
		Score score = scoreMap.get(currentGame);
		score.setMingGangtotal(score.getMingGangtotal()+1);
		currentScore = currentScore + addScore;
		return addScore;
	}
	
	/**
	 * 明杠减分
	 */
	public int reduceScoreForMingGang(int wanFa){
		int result = 0;
		if(wanFa==JIN_CHONG){
			result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_MINGGANG);
			currentScore = currentScore -result;
			return result;
		}else{
			if(currentScore>=Math.abs(ScoreType.REDUCE_SCORE_FOR_MINGGANG)){
				result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_MINGGANG);
				currentScore = currentScore -result;
				return result;
			}else{
				result = currentScore;
				currentScore = 0;
				return result;
			}
		}
	}
	
	
	/**
	 * 明杠减分
	 */
	public int reduceScoreForFengShu(int wanFa,int fenshu){
		int result = 0;
		if(wanFa==JIN_CHONG){
			result =  Math.abs(fenshu);
			currentScore = currentScore -result;
			return result;
		}else{
			if(currentScore>=Math.abs(fenshu)){
				result =  Math.abs(fenshu);
				currentScore = currentScore -result;
				return result;
			}else{
				result = currentScore;
				currentScore = 0;
				return result;
			}
		}
	}
	
	
	
	
	/**
	 * 胡牌减分
	 */
	public int reduceScoreForHuPai(int wanFa){
		Score score = scoreMap.get(currentGame);
		int result = 0;
		int reduceScore = 0;//需要减去的分数
		reduceScore = Math.abs(ScoreType.REDUCE_SCORE_FOR_HUPAI);
		if(this.isFangPao){
			reduceScore = reduceScore + 2;
		}
		if(wanFa == JIN_CHONG){
			score.setFinalScore(score.getFinalScore()- reduceScore);
			result =  reduceScore;
			currentScore = currentScore -result;
			return result;
		}else{
			if(currentScore>=reduceScore){
				score.setFinalScore(score.getFinalScore()- reduceScore);
				result =  reduceScore;
				currentScore = currentScore -result;
				return result;
			}else{
				score.setFinalScore(score.getFinalScore()- currentScore);
				result = currentScore;
				currentScore = 0;
				return result;
			}
		}
	}
	
	/**
	 * 暗杠得分
	 */
	public int addScoreForAnGang(int addScore){
		Score score = scoreMap.get(currentGame);
		score.setAnGangTotal(score.getAnGangTotal()+1);
		currentScore =  currentScore + addScore;
		return addScore;
	}
	

	/**
	 * 暗杠减分
	 */
	public int reduceScoreForAnGang(int wanFa){
		int result = 0;
		if(wanFa==JIN_CHONG){
			result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_HUPAI);
			currentScore = currentScore -result;
			return result;
		}else{
			if(currentScore>=Math.abs(ScoreType.REDUCE_SCORE_FOR_HUPAI)){
				result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_HUPAI);
				currentScore = currentScore -result;
				return result;
			}else{
				result = currentScore;
				currentScore = 0;
				return result;
			}
		}
	}
	
	/**
	 * 首搭得分
	 */
	public int addScore(int addScore){
		Score score = scoreMap.get(currentGame);
		currentScore =  currentScore + addScore;
		score.setFinalScore(score.getFinalScore() + addScore);
		return addScore;
	}
	
	
	
	/**
	 * 修改用户的胡牌次数
	 */
	public void modifyUserHuPaiTotal(){
		Score score = scoreMap.get(currentGame);
		score.setHuPaiTotal(score.getHuPaiTotal()+1);
	}
	
	
	
	/**
	 * 包冲减分
	 */
	public int reduceScoreForBaoChong(int reduceScore){
		int result = 0;
		Score score = scoreMap.get(currentGame);
		score.setFinalScore(score.getFinalScore()- reduceScore);
		currentScore = currentScore -reduceScore;
		return result;
	}
	
	
	
	
	
	/**
	 * 首搭减分
	 */
	public int reduceScoreForShouDa(int totalShowDa,int wanFa,int totalUser){
		int result = 0;
		Score score = scoreMap.get(currentGame);
		if(wanFa==JIN_CHONG){
			result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_SHOUDA)*totalShowDa*totalUser;
			score.setFinalScore(score.getFinalScore()- result);
			currentScore = currentScore -result;
			return result;
		}else{
			if(currentScore>=Math.abs(ScoreType.REDUCE_SCORE_FOR_SHOUDA)*totalShowDa){
				result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_SHOUDA)*totalShowDa;
				score.setFinalScore(score.getFinalScore()- result);
				currentScore = currentScore -result;
				return result;
			}else{
				result = currentScore;
				score.setFinalScore(score.getFinalScore()- result);
				currentScore = 0;
				return result;
			}
		}
	}
	
	
	
	/**
	 * 二搭减分
	 */
	public int reduceScoreForErDa(int totalErDa,int wanFa,int totalUser){
		int result = 0;
		Score score = scoreMap.get(currentGame);
		if(wanFa==JIN_CHONG){
			result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_ERDA)*totalErDa*totalUser;
			score.setFinalScore(score.getFinalScore()- result);
			currentScore = currentScore -result;
			return result;
		}else{
			if(currentScore>=Math.abs(ScoreType.REDUCE_SCORE_FOR_ERDA)*totalErDa){
				result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_ERDA)*totalErDa;
				score.setFinalScore(score.getFinalScore()- result);
				currentScore = currentScore -result;
				return result;
			}else{
				result = currentScore;
				score.setFinalScore(score.getFinalScore()- result);
				currentScore = 0;
				return result;
			}
		}
		
	}
	
	
	
	/**
	 * 三搭减分
	 */
	public int reduceScoreForSanDa(int totalSanDa,int wanFa,int totalUser){
		int result = 0;
		Score score = scoreMap.get(currentGame);
		if(wanFa==JIN_CHONG){
			result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_SANDA)*totalSanDa*totalUser;
			score.setFinalScore(score.getFinalScore()- result);
			currentScore = currentScore -result;
			return result;
		}else{
			if(currentScore>=Math.abs(ScoreType.REDUCE_SCORE_FOR_SANDA)*totalSanDa){
				result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_SANDA)*totalSanDa;
				score.setFinalScore(score.getFinalScore()- result);
				currentScore = currentScore -result;
				return result;
			}else{
				result = currentScore;
				score.setFinalScore(score.getFinalScore()- result);
				currentScore = 0;
				return result;
			}
		}
	}
	
	
	/**
	 * 杠开减分
	 */
	public int reduceScoreForGangKai(int wanFa){
		int result = 0;
		Score score = scoreMap.get(currentGame);
		
		if(wanFa==JIN_CHONG){
			result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_GANGKAI);
			score.setFinalScore(score.getFinalScore()- result);
			currentScore = currentScore -result;
			return result;
		}else{
			if(currentScore>=Math.abs(ScoreType.REDUCE_SCORE_FOR_GANGKAI)){
				result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_GANGKAI);
				score.setFinalScore(score.getFinalScore()- result);
				currentScore = currentScore -result;
				return result;
			}else{
				result = currentScore;
				score.setFinalScore(score.getFinalScore()- result);
				currentScore = 0;
				return result;
			}
		}
	}
	
	
	
	/**
	 * 无搭减分
	 */
	public int reduceScoreForWuDa(int wanFa){
		int result = 0;
		Score score = scoreMap.get(currentGame);
		if(wanFa==JIN_CHONG){
			result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_WUDA);
			score.setFinalScore(score.getFinalScore()- result);
			currentScore = currentScore -result;
			return result;
		}else{
			if(currentScore>=Math.abs(ScoreType.REDUCE_SCORE_FOR_WUDA)){
				result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_WUDA);
				score.setFinalScore(score.getFinalScore()- result);
				currentScore = currentScore -result;
				return result;
			}else{
				result = currentScore;
				score.setFinalScore(score.getFinalScore()- result);
				currentScore = 0;
				return result;
			}
		}
	}
	
	
	
	
	/**
	 * 有搭减分
	 */
	public int reduceScoreForYouDa(int wanFa){
		int result = 0;
		Score score = scoreMap.get(currentGame);
		
		if(wanFa==JIN_CHONG){
			result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_YOUDA);
			score.setFinalScore(score.getFinalScore()- result);
			currentScore = currentScore -result;
			return result;
		}else{
			if(currentScore>=Math.abs(ScoreType.REDUCE_SCORE_FOR_YOUDA)){
				result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_YOUDA);
				score.setFinalScore(score.getFinalScore()- result);
				currentScore = currentScore -result;
				return result;
			}else{
				result = currentScore;
				score.setFinalScore(score.getFinalScore()- result);
				currentScore = 0;
				return result;
			}
		}
	}
	
	
	
	
	
	/**
	 * 跑搭减分
	 */
	public int reduceScoreForPaoDa(int wanFa){
		int result = 0;
		Score score = scoreMap.get(currentGame);
		
		if(wanFa == JIN_CHONG){
			result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_PAODA);
			score.setFinalScore(score.getFinalScore()- result);
			currentScore = currentScore -result;
			return result;
		}else{
			if(currentScore>=Math.abs(ScoreType.REDUCE_SCORE_FOR_PAODA)){
				result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_PAODA);
				score.setFinalScore(score.getFinalScore()- result);
				currentScore = currentScore -result;
				return result;
			}else{
				result = currentScore;
				score.setFinalScore(score.getFinalScore()- result);
				currentScore = 0;
				return result;
			}
		}
		
	}
	
	
	
	
	
	
	
	/**
	 * 跑搭脱搭
	 */
	public int reduceScoreForPaoDaTuoDa(int wanFa){
		int result = 0;
		Score score = scoreMap.get(currentGame);
		
		if(wanFa==JIN_CHONG){
			result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_PAODA_TUODA);
			score.setFinalScore(score.getFinalScore()- result);
			currentScore = currentScore -result;
			return result;
		}else{
			if(currentScore>=Math.abs(ScoreType.REDUCE_SCORE_FOR_PAODA_TUODA)){
				result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_PAODA_TUODA);
				score.setFinalScore(score.getFinalScore()- result);
				currentScore = currentScore -result;
				return result;
			}else{
				result = currentScore;
				score.setFinalScore(score.getFinalScore()- result);
				currentScore = 0;
				return result;
			}
		}
	}
	
	
	
	/**记录用户杠出的牌
	 * @param type 0 放杠  1、暗杠  2、公杠 /明杠
	 * @param cards 杠出的牌
	 */
	public void recordUserGangCards(int type,List<Integer> cards){
		GangCard gangCard = new GangCard(type, cards);
		gangCards.add(gangCard);
	}
	
	/**得到本局的得分
	 * @return
	 */
	public int getCurrentGameSore(){
		Score score = scoreMap.get(currentGame);
		return score.getFinalScore();
	}
	
	/**从自己的牌中移除公杠的那张牌
	 * @param card
	 */
	public void removeCardFromGongGang(Integer cardId){
		cards.remove(cardId);
	}

	
	/**将牌转成数组
	 * @return
	 */
	public int[] getCardsArray(){
		int arr[]= new int[cards.size()];
		for(int i=0;i<cards.size();i++){
			arr[i] = cards.get(i);
		}
		return arr;
	}
	

	/**得到用户的IP
	 * @return
	 */
	public String getIp(){
		SocketAddress remoteAddress =  this.ioSession.channel().localAddress();
		return remoteAddress.toString().replaceAll("/", "");
	}
	
}
