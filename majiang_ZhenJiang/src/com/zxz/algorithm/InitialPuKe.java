package com.zxz.algorithm;

import java.util.HashMap;
import java.util.Map;

public class InitialPuKe {
	
	public static Map<Integer, String> map;
	
	static{
		map = new HashMap<Integer, String>();
		putOne(map);
		putTong(map);
		putTiao(map);
		putHongZhong(map);
		putFa(map);
		putBai(map);
		putDong(map);
		putNan(map);
		putXi(map);
		putBei(map);
	}

	public Map<Integer, String> getPaiDictionary(){
		return map;
	}
	
	public static String getCardType(int cardId){
		return map.get(cardId);
	}
	
	
	private static void putHongZhong(Map<Integer, String> map) {
		map.put(108,"红中");
		map.put(109,"红中");
		map.put(110,"红中");
		map.put(111,"红中");
	}
	
	private static void putFa(Map<Integer, String> map) {
		map.put(112,"发");
		map.put(113,"发");
		map.put(114,"发");
		map.put(115,"发");
	}
	
	private static void putBai(Map<Integer, String> map) {
		map.put(116,"白");
		map.put(117,"白");
		map.put(118,"白");
		map.put(119,"白");
	}
	private static void putDong(Map<Integer, String> map) {
		map.put(120,"东");
		map.put(121,"东");
		map.put(122,"东");
		map.put(123,"东");
	}
	private static void putNan(Map<Integer, String> map) {
		map.put(124,"南");
		map.put(125,"南");
		map.put(126,"南");
		map.put(127,"南");
	}
	private static void putXi(Map<Integer, String> map) {
		map.put(128,"西");
		map.put(129,"西");
		map.put(130,"西");
		map.put(131,"西");
	}
	private static void putBei(Map<Integer, String> map) {
		map.put(132,"北");
		map.put(133,"北");
		map.put(134,"北");
		map.put(135,"北");
	}

	private static void putTong(Map<Integer, String> map) {
		map.put(36,"1筒");
		map.put(37,"1筒");
		map.put(38,"1筒");
		map.put(39,"1筒");
		map.put(40,"2筒");
		map.put(41,"2筒");
		map.put(42,"2筒");
		map.put(43,"2筒");
		map.put(44,"3筒");
		map.put(45,"3筒");
		map.put(46,"3筒");
		map.put(47,"3筒");
		map.put(48,"4筒");
		map.put(49,"4筒");
		map.put(50,"4筒");
		map.put(51,"4筒");
		map.put(52,"5筒");
		map.put(53,"5筒");
		map.put(54,"5筒");
		map.put(55,"5筒");
		map.put(56,"6筒");
		map.put(57,"6筒");
		map.put(58,"6筒");
		map.put(59,"6筒");
		map.put(60,"7筒");
		map.put(61,"7筒");
		map.put(62,"7筒");
		map.put(63,"7筒");
		map.put(64,"8筒");
		map.put(65,"8筒");
		map.put(66,"8筒");
		map.put(67,"8筒");
		map.put(68,"9筒");
		map.put(69,"9筒");
		map.put(70,"9筒");
		map.put(71,"9筒");
	}

	private static void putTiao(Map<Integer, String> map) {
		map.put(72,"1条");
		map.put(73,"1条");
		map.put(74,"1条");
		map.put(75,"1条");
		map.put(76,"2条");
		map.put(77,"2条");
		map.put(78,"2条");
		map.put(79,"2条");
		map.put(80,"3条");
		map.put(81,"3条");
		map.put(82,"3条");
		map.put(83,"3条");
		map.put(84,"4条");
		map.put(85,"4条");
		map.put(86,"4条");
		map.put(87,"4条");
		map.put(88,"5条");
		map.put(89,"5条");
		map.put(90,"5条");
		map.put(91,"5条");
		map.put(92,"6条");
		map.put(93,"6条");
		map.put(94,"6条");
		map.put(95,"6条");
		map.put(96,"7条");
		map.put(97,"7条");
		map.put(98,"7条");
		map.put(99,"7条");
		map.put(100,"8条");
		map.put(101,"8条");
		map.put(102,"8条");
		map.put(103,"8条");
		map.put(104,"9条");
		map.put(105,"9条");
		map.put(106,"9条");
		map.put(107,"9条");
	}

	private static void putOne(Map<Integer, String> map) {
		map.put(0,"1万");
		map.put(1,"1万");
		map.put(2,"1万");
		map.put(3,"1万");
		map.put(4,"2万");
		map.put(5,"2万");
		map.put(6,"2万");
		map.put(7,"2万");
		map.put(8,"3万");
		map.put(9,"3万");
		map.put(10,"3万");
		map.put(11,"3万");
		map.put(12,"4万");
		map.put(13,"4万");
		map.put(14,"4万");
		map.put(15,"4万");
		map.put(16,"5万");
		map.put(17,"5万");
		map.put(18,"5万");
		map.put(19,"5万");
		map.put(20,"6万");
		map.put(21,"6万");
		map.put(22,"6万");
		map.put(23,"6万");
		map.put(24,"7万");
		map.put(25,"7万");
		map.put(26,"7万");
		map.put(27,"7万");
		map.put(28,"8万");
		map.put(29,"8万");
		map.put(30,"8万");
		map.put(31,"8万");
		map.put(32,"9万");
		map.put(33,"9万");
		map.put(34,"9万");
		map.put(35,"9万");
	}
	
	public static void main(String[] args) {
//		int j=1;
//		for(int i=72;i<108;i++){
//			if(j==10){
//				j=1;
//			}
//			System.out.println("map.put("+i+","+"\""+(j)+"条\");");
//			j++;
//		}
		
//		int cards[] = {105,48,88,35};
//		int cards[] = {74,16,79,87,22};
		int cards[] = {39,106,60};
		for(int i=0;i<cards.length;i++){
			int c = cards[i];
			String cardType = getCardType(c);
			System.out.println(c+":"+cardType);
		}
	}
}
