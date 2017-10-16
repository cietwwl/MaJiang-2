package com.zxz.service;

import java.util.List;

import com.zxz.domain.User;

public class HuiFangUitl {

	
	public static void getBase(StringBuffer sb,User u,boolean isEnd){
		String direction = getDirection(u);
		if(isEnd){
			sb.append(direction+","+u.getHeadimgurl()+","+u.getNickName()+","+u.getSex()+","+u.getId()+"|");
		}else{
			sb.append(direction+","+u.getHeadimgurl()+","+u.getNickName()+","+u.getSex()+","+u.getId()+";");
		}
	}
	
	
	/**�õ�����
	 * @param sb
	 * @param u
	 * @param cardId
	 */
	public static void getFaPai(StringBuffer sb,User u,List<Integer> cards){
		String direction = getDirection(u);
		sb.append(direction+":"+"fIds:"+cards+"|");
	}
	
	/**�õ�����
	 * @param sb
	 * @param u
	 * @param cardId
	 */
	public static void getAnGang(StringBuffer sb,User u,List<Integer> cards){
		String direction = getDirection(u);
		sb.append(direction+":"+"gggIds:"+cards+"|");
	}
	
	/**�õ�����
	 * @param sb
	 * @param u
	 * @param cardId
	 */
	public static void getChuPai(StringBuffer sb,User u,Integer cardId){
		String direction = getDirection(u);
		sb.append(direction+":"+"cId:"+cardId+"|");
	}
	
	
	/**�õ�����
	 * @param sb
	 * @param u
	 * @param cardId
	 */
	public static void getHuPai(StringBuffer sb,User u,List<Integer> cardIds,List<Integer> remainCards){
		String direction = getDirection(u);
		sb.append(direction+":"+"hIds:"+cardIds+"|r:rIds:"+remainCards+"|");
	}

	/**得到百搭
	 * @param sb
	 * @param baiDa
	 */
	public static void getBaiDai(StringBuffer sb,int baiDa){
		sb.append("f:hInfo:"+baiDa+"|");
	}
	
	
	/**�õ�����
	 * @param sb
	 * @param u
	 * @param cardId
	 */
	public static void getPengPai(StringBuffer sb,User u,List<Integer> cards){
		String direction = getDirection(u);
		sb.append(direction+":"+"pIds:"+cards+"|");
	}
	
	
	
	/**�õ�����
	 * @param sb
	 * @param u
	 * @param cardId
	 */
	public static void getGangPai(StringBuffer sb,User u,List<Integer> cards){
		String direction = getDirection(u);
		sb.append(direction+":"+"gIds:"+cards+"|");
	}
	
	/**�õ�����
	 * @param sb
	 * @param u
	 * @param cardId
	 */
	public static void getGongGang(StringBuffer sb,User u,List<Integer> cards){
		String direction = getDirection(u);
		sb.append(direction+":"+"ggIds:"+cards+"|");
	}
	
	
	/**�õ�ץ��
	 * @param sb
	 * @param u
	 * @param cardId
	 */
	public static void getZhuaPai(StringBuffer sb,User u,Integer cardId){
		String direction = getDirection(u);
		sb.append(direction+":"+"zId:"+cardId+"|");
	}
	
	
	/**
	 * �õ�����
	 * 
	 * @param u
	 * @return
	 */
	public static String getDirection(User u) {
		switch (u.getDirection()) {
		case "east":
			return "e";
		case "west":
			return "w";
		case "north":
			return "n";
		case "south":
			return "s";
		}
		return "";
	}

}
