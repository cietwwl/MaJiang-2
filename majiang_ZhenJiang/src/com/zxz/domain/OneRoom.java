package com.zxz.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.zxz.utils.Constant;
import com.zxz.utils.NotifyTool;

import io.netty.channel.ChannelHandlerContext;


public class OneRoom  implements Constant{
	private int id;	//房间id
	private int roomNumber;//房间号
	private List<User> userList = new LinkedList<>();//房间里面的人
	private int total;//游戏局数(圈数)
	private int zhama;//扎码数
	private int end;//几家光
	private User createUser;//创建人
	private int createUserId;//创建人的id 
	private boolean isUse = false;//房间是否已经占用
	private Set<String> directionSet = new HashSet<>();//房间的方向
	private boolean isPay = false;//是否扣除房卡
	private Date createDate;//房间创建时间
	private int invertal;//查询创建房间时候的间隔值  房间号
	private boolean isDisband = false;//房间是否解散
	private int alreadyTotalGame = 0;//已经玩的局数
	private int auto = 1;// 是否托管
	private int wanFa = 1; //1,默认50分进园子 (陪冲) 2.紧冲(可以包冲和陪冲) 
	private int chong;//包冲和陪冲  1包冲 ,2陪冲
	private int disbandTotal = 0 ;//解散的次数
	
	public int getDisbandTotal() {
		return disbandTotal;
	}
	public int setDisbandTotal(int disbandTotal) {
		this.disbandTotal = disbandTotal;
		return this.disbandTotal;
	}
	public int getChong() {
		return chong;
	}
	public void setChong(int chong) {
		this.chong = chong;
	}
	public int getWanFa() {
		return wanFa;
	}
	public void setWanFa(int wanFa) {
		this.wanFa = wanFa;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getInvertal() {
		return invertal;
	}
	public void setInvertal(int invertal) {
		this.invertal = invertal;
	}
	public int getAlreadyTotalGame() {
		return alreadyTotalGame;
	}
	public void setAlreadyTotalGame(int alreadyTotalGame) {
		this.alreadyTotalGame = alreadyTotalGame;
	}
	public int getAuto() {
		return auto;
	}
	public void setAuto(int auto) {
		this.auto = auto;
	}
	
	public boolean isDisband() {
		return isDisband;
	}
	public void setDisband(boolean isDisband) {
		this.isDisband = isDisband;
	}
	public User getCreateUser() {
		return createUser;
	}
	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public boolean isPay() {
		return isPay;
	}
	public void setPay(boolean isPay) {
		this.isPay = isPay;
	}
	public Set<String> getDirectionSet() {
		return directionSet;
	}
	public void setDirectionSet(Set<String> directionSet) {
		this.directionSet = directionSet;
	}
	public int getRoomNumber() {
		return roomNumber;
	}
	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}
	public boolean isUse() {
		return isUse;
	}
	public void setUse(boolean isUse) {
		this.isUse = isUse;
	}
	public int getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}
	public int getZhama() {
		return zhama;
	}
	public void setZhama(int zhama) {
		this.zhama = zhama;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<User> getUserList() {
		return userList;
	}
	public void setUserList(List<User> userList) {
		this.userList = userList;
	} 
	
	public void addUser(User user){
		userList.add(user);
	}
	
	public List<ChannelHandlerContext> getUserIoSessionList(){
		List<ChannelHandlerContext> list =  new ArrayList<ChannelHandlerContext>();
		for(User user : userList){
			list.add(user.getIoSession());
		}
		return list;
	}
	
	/**用户离开房间
	 * @param user
	 * @return
	 */
	public boolean userLeaveRoom(User user){
		boolean result = false;
		for(User u:userList){
			if(u.getId()==user.getId()){
				result = userList.remove(u);
				break;
			}
		}
		return result;
	}
	
	

	
	/**通知房间里用户
	 * @param jsonObject
	 */
	public void noticeUsersWithJsonObject(JSONObject jsonObject){
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			ChannelHandlerContext channelHandlerContext = user.getIoSession();
			NotifyTool.notify(channelHandlerContext, jsonObject);
		}
	}
	
	
	
	
}
