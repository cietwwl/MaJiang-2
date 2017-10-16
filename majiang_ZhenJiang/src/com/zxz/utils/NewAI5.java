package com.zxz.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zxz.algorithm.InitialPuKe;

/**
 * http://blog.csdn.net/skillart/article/details/40422885?utm_source=tuicool&
 * utm_medium=referral 新的胡牌算法
 * 
 * @author 顾双
 */
public class NewAI5 {

	public static void main(String[] args) {
		long currentTimeMillis1 = System.currentTimeMillis();
//		List<Integer> tingList = testTing();
//		System.out.println(tingList);
//		 int[] cards = {4,8,12,69,70,71,90,91,92,96,100,105,106,108};
//		 int[] cards = {1, 4, 10, 45, 52, 58, 65, 93, 102, 107, 108, 109, 110, 111};
//		 int[] cards = {20, 26, 31, 60, 63, 75, 79, 81, 82, 88, 92, 99, 100, 108};
//		 int[] cards = {8,15,18,20,26,30,55,62,67,71,110};
//		 int[] cards = {10,14,16,19,20,22,25,92,93,95,111};
//		 int[] cards = {20, 26, 35, 45, 50, 54, 58, 60, 78, 88, 89, 90, 98, 101};//不应该胡
//		 int[] cards = {5, 7, 11, 34, 47, 51, 76, 81, 82, 104, 105};//不应该胡
//		int[] cards = {13,14,15,17,24,45,46,47,89,90,95,100,108,111};
//		int[] cards = { 41,47,50,61,62,75,80,84,92,108,111 };
//		int[] cards = {15,19,53,56,65,66,92,94,95,96,102,105,110,111 };
//		int[] cards = {15,19,53,56,65,66,92,94,95,96,102,105,110,111 };
//		int[] cards = {0,1,4,5,8,9,12,16,17,18,20};
//		int[] cards = {28, 29, 77, 78, 82, 83, 84, 85, 86, 89, 95};
//		int[] cards = {76, 77, 78,82, 83, 84, 85, 86, 89, 92,93,94,108,109};
//		int[] cards = {28, 29, 77, 78,79, 82,81, 83, 84, 85, 86};
//		int[] cards = {6,14,20,34,39,50,51,56,57,72,80,89,104};//不应该胡
//		int[] cards = {46,50,54,80,88,92,96,100,108,109};//胡任意牌
//		int[] cards = {36,47,48,51,52,57,62,65,66,109,110};//恋姐
//		int[] cards = {0,4,8,12,13};//恋姐
//		int[] cards = {20,21,27,28,32,58,60,65,78,83,101,105,109,110};//天胡
//		int[] cards = {1 ,2 ,3,4,5 ,6 ,7 ,9 ,10 ,11 ,13 ,14 ,15 ,17};//黄娟
//		int[] cards = {78,79,83,90,95,96,101,110};//黄娟
//		int[] cards = {15,18,21,23,25,28,83,87,94,99,102,104,106,108};//黄娟
//		int[] cards = {10,13,33,60,66,70,72,77,89,105,108,109,110,111};//天胡4个红中,其实不胡
//		int[] cards = {1,40,41,42,44,48,52,56,60,64,108};//悟空
//		int[] cards = {0,4,5,12,16,20,24,80,81,92,96,100,108,109};//疯狂 man
//		int[] cards = {33,35,45,51,54,75,78,83,87,92,97,105,109,111};//cuo
//		int[] cards = {8,11,12,14,16,17,19,20,77,79,109};//cuo2
//		int[] cards = {9,12,36,38,39,43,47,51,68,69,71,72,73,109};//cuo3
//		int[] cards = {1,8,16,19,91,92,93,96,98,99,102,104,109,110};//cuo4
//		int[] cards = {7,11,12,34,35,76,82,85,88,92,98,99,104,109};//cuo5
//		int[] cards = {4,7,74,75,77,78,82,87,89,90,94,95,99,111};//cuo6
		int[] cards = {3,12,14,15,37,47,110};//cuo7四个红中不应该胡
		showPai(cards);
		boolean win = isWin(cards);
		if(win){
			System.out.println("hu");
		}else{
			System.out.println("no");
		}
		long currentTimeMillis2 = System.currentTimeMillis();
		System.out.println(currentTimeMillis2 - currentTimeMillis1);
//		testTing(currentTimeMillis1);
//		showPai(testTing());
		//天胡发牌
//		int[] cards = {0,4,8,12,16,20,24,28,32,36,40,44,72};
//		int total = 0;
//		for(int i=0;i<=111;i++){
//			boolean isIn = false;
//			for(int j=0;j<cards.length;j++){
//				int card = cards[j];
//				if(i==card){
//					isIn = true;
//					break;
//				}
//			}
//			if(!isIn){
//				System.out.print(i+" ,");
//				total++;
//				if(total==13){
//					System.out.println();
//					total = 0;
//				}
//			}
//		}
		
	}

	private static void testTing(long currentTimeMillis1) {
		List<Integer> tingList = new ArrayList<>();
		for(int i=0;i<=111;i++){
			int[] cards = {1 ,2 ,3,5 ,6 ,7,9 ,10 ,11 ,13 ,14 ,15 ,17};//黄娟
//			int[] cards = {0,4,12,16,20,24,80,81,92,96,100,108,109};//疯狂man
//			int[] cards = {40,41,42,44,48,52,56,60,64,108};//悟空
//			int[] cards = {0,1,2,4,8,12,16,20,24,28,32,33,34 };// 苏尼尔发的测试
			List<Integer> cardsList =  new ArrayList<>();
			for(int j=0;j<cards.length;j++){
				cardsList.add(cards[j]);
			}
			cardsList.add(i);
			Collections.sort(cardsList);
			showPai(cardsList);
			boolean win = isWin(cardsList);
			if(win){
				tingList.add(i);
				System.out.println("hu");
			}else{
				System.out.println("no");
			}
			long currentTimeMillis2 = System.currentTimeMillis();
			System.out.println(currentTimeMillis2 - currentTimeMillis1);
		}
		System.out.println(tingList);
		showPai(tingList);
	}
	
	
	/**测试听牌
	 * @return
	 */
	private static List<Integer> testTing() {
		List<Integer> tingList = new ArrayList<>();
		for(int i=0;i<=108;i++){
			int[] cards = {46,50,54,80,88,92,96,100,108,109};
			List<Integer> list = getList(cards);
			list.add(i);
			Collections.sort(list);
			showPai(list);
			boolean win = isWin(list);
			if (win) {
				tingList.add(i);
				System.out.println("hu");
			} else {
				System.out.println("no");
			}
		}
		return tingList;
	}

	public static String showPai(List<Integer> cards) {
		int[] array = new int[cards.size()];
		for (int i = 0; i < cards.size(); i++) {
			array[i] = cards.get(i);
		}
		String result = showPai(array);
		return result;
	}

	public static String showPai(int array[]) {
//		System.out.println("\t\t");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			System.out.print(InitialPuKe.map.get(array[i]) + " ");
			sb.append(InitialPuKe.map.get(array[i]) + " ");
		}
		System.out.println();
		return sb.toString();
	}

	public static List<Integer> getList(int[] array) {
		List<Integer> list = new LinkedList<Integer>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}
	
	public static List<Integer> getList(List<Integer> list) {
		List<Integer> returnList = new LinkedList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			returnList.add(list.get(i));
		}
		return returnList;
	}
	
	
	
	

	public static boolean isWin(int array[]) {
		List<Integer> list = new LinkedList<>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return isWin(list);
	}

	public static boolean isWin(List<Integer> list) {
		Map<Integer, List<Integer>> paiXingCards = getPaiXingCards(list);
		Map<Integer, Integer> kindsNeedHongZhong = getKindsNeedHongZhong(paiXingCards);
		//万
		List<Integer> wanList = paiXingCards.get(0);
		List<Integer> bingList = paiXingCards.get(1);
		List<Integer> tiaoList = paiXingCards.get(2);
		Integer hongZhong = paiXingCards.get(3).size();// 红中的数量
		Integer wanNeed = kindsNeedHongZhong.get(0);
		Integer bingNeed = kindsNeedHongZhong.get(1);
		Integer tiaoNeed = kindsNeedHongZhong.get(2);
		System.out.println("wanNeed:" + wanNeed);
		System.out.println("bingNeed:" + bingNeed);
		System.out.println("tiaoNeed:" + tiaoNeed);
		System.out.println("hongZhong:" + hongZhong);
		boolean checkWithOne = checkWithWan(hongZhong, bingNeed, tiaoNeed, wanList);
		if (checkWithOne) {
			return true;
		} else {
			boolean checkWithTong = checkWithTong(hongZhong, wanNeed, tiaoNeed, bingList);
			if (checkWithTong) {
				return true;
			} else {
				boolean checkWithTiao = checkWithTiao(hongZhong, wanNeed, bingNeed, tiaoList);
				if (checkWithTiao) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 将在条中
	 * 
	 * @param hongZhong
	 *            红中的个数
	 * @param wanNeed
	 *            万需要的个数
	 * @param bingNeed
	 *            筒需要的个数
	 * @param tiaoList
	 *            条
	 * @return
	 */
	private static boolean checkWithTiao(Integer hongZhong, Integer wanNeed, Integer bingNeed, List<Integer> tiaoList) {
		// 3.假如将在【条】里面那么【万】【饼】必然是整扑。
		int needHunNum = wanNeed + bingNeed;
		int hadHunNum = hongZhong - needHunNum;
		if (hadHunNum >= 0) {// 需求的比拥有的多 就不做判断
			return removeDui(tiaoList, hadHunNum);
		} else {
			return false;
		}
	}

	/**
	 * 剔除一个对子
	 * 
	 * @param cards
	 */
	public static boolean removeDui(List<Integer> cards, int RemainHongZhong) {
		for (int i = 0; i < cards.size(); i++) {
			String beforType = "";
			Integer cardId = cards.get(i);
			Integer beforeCardId = null;
			if (i >= 1) {
				beforeCardId = cards.get(i - 1);
				beforType = CardsMap.getCardType(beforeCardId);
			}
			String currentType = CardsMap.getCardType(cardId);
			if (currentType.equals(beforType)) {
				Integer[] cardNumbers = { beforeCardId, cardId };
				List<Integer> nowCards = getCardsWithoutCardNumbers(cards, cardNumbers);
//				int nowNeed = getEveryNeedtotal(nowCards);
				int nowNeed = getEveryNeedFromDui(nowCards);
				if (nowNeed <= RemainHongZhong) {
					return true;
				}
			} else {
				Integer[] cardNumbers = { cardId };
				List<Integer> nowCards = getCardsWithoutCardNumbers(cards, cardNumbers);
//				int nowNeed = getEveryNeedtotal(nowCards);
				int nowNeed = getEveryNeedFromDui(nowCards);
				if (nowNeed <= RemainHongZhong) {
					return true;
				}
			}
		}
		return false;
	}

	
	/**
	 * 得到每一种牌需要的红中数
	 * @return
	 */
	public static int getEveryNeedFromDui(List<Integer> cards) {
		if (cards.size() <= 3) {
			return getWithLtThree(cards);
		} else {
			return getWithGtThreeWithDui(cards);
		}
	}
	
	
	/**
	 * @param cards
	 * @return
	 */
	private static int getWithGtThreeWithDui(List<Integer> cards) {
		int index = 0;
		boolean isNotRemoveAll = true;
		while (isNotRemoveAll) {
//			showPai(cards);
			int length = cards.size();
			boolean remove = false;
			if (index >= length) {
				break;
			}
			Integer n1 = cards.get(index);
			Integer n2 = -1;
			if (index + 1 < length) {
				n2 = cards.get(index + 1);
			}
			Integer n3 = -1;
			if(index + 2 <length){
				n3 = cards.get(index + 2);
			}
			String type1 = CardsMap.getCardType(n1);
			String type2 = CardsMap.getCardType(n2);
			String type3 = CardsMap.getCardType(n3);
			if (type1.equals(type2)&&type2.equals(type3)) {// 坎
				cards.remove(n1);
				cards.remove(n2);
				cards.remove(n3);
				remove = true;
			}else{
				int interval1 = getInterval(n1, n2);
				int interval2 = getInterval(n2, n3);
				if(interval1==1&&interval2==1){//如果是顺子
					cards.remove(n1);
					cards.remove(n2);
					cards.remove(n3);
					remove = true;
				}else{
					String typeString = getTypeString(n1);
					int typeA2Int = getTypeInt(n1)+1;
					String wantType2 = typeA2Int+typeString;
					int typeA3Int = getTypeInt(n1)+2;
					String wantType3 = typeA3Int+typeString;
					int totalWangTyp1 = 0;
					int totalWangTyp2 = 0;
					int totalWangTyp3 = 0;
					List<Integer> removeList = new ArrayList<>();
					for(int i=0;i<cards.size();i++){
						Integer c = cards.get(i);
						String cardType = CardsMap.getCardType(c);
						if(cardType.equals(type1)&&totalWangTyp1<2){
							removeList.add(c);
							totalWangTyp1++;
						}else if(cardType.equals(wantType2)&&totalWangTyp2<2){
							removeList.add(c);
							totalWangTyp2++;
						}else if(cardType.equals(wantType3)&&totalWangTyp3<2){
							removeList.add(c);
							totalWangTyp3++;
						}
						if(removeList.size()==6){
							break;
						}
					}
					if(removeList.size()==6){//223344
						for(int i=0;i<removeList.size();i++){
							Integer removeCard = removeList.get(i);
							cards.remove(removeCard);
						}
						remove = true;
					}else{
						removeList = new ArrayList<>();
						totalWangTyp1 = 0;
						totalWangTyp2 = 0;
						totalWangTyp3 = 0;
						for(int i=0;i<cards.size();i++){
							Integer c = cards.get(i);
							String cardType = CardsMap.getCardType(c);
							if(cardType.equals(type1)&&totalWangTyp1<1){
								removeList.add(c);
								totalWangTyp1++;
							}else if(cardType.equals(wantType2)&&totalWangTyp2<1){
								removeList.add(c);
								totalWangTyp2++;
							}else if(cardType.equals(wantType3)&&totalWangTyp3<1){
								removeList.add(c);
								totalWangTyp3++;
							}
							if(removeList.size()==3){
								break;
							}
						}
						if(removeList.size()==3){//122223
							for(int i=0;i<removeList.size();i++){
								Integer removeCard = removeList.get(i);
								cards.remove(removeCard);
							}
							remove = true;
						}
					}
				}
			}
			if(remove==false||cards.size()<3){
				int interval1 = getInterval(n1, n2);
				if(interval1==2||interval1==1||interval1==0){
					index = index +2;
				}else{
					index++;
				}
				if (index < length) {
					isNotRemoveAll = true;
				}else{
					isNotRemoveAll = false;
				}
			}
		}
		//showPai(cards);
		return getNeed(cards);
	}
	
	/**
	 * @param cards
	 * @return
	 */
	private static int getNeed(List<Integer> cards) {
		int index = 0;
		int length = cards.size();
		int needTotal = 0;
		boolean checkOver = false;
		while (!checkOver) {
			if (index >= length) {
				checkOver = true;
				break;
			}
			int n1 = cards.get(index);
			int n2 = -1;
			if (index + 1 < length) {
				n2 = cards.get(index + 1);
			}
			String type1 = CardsMap.getCardType(n1);
			String type2 = CardsMap.getCardType(n2);
			int interval = getInterval(n1, n2);
			if (type1.equals(type2)) {// 消耗一个
				index = index + 2;
				needTotal = needTotal + 1;
				// System.out.println("消耗一个来做坎");
			} else if (interval == 1 || interval == 2) {// 如果间隔相差一个 消耗1个
				index = index + 2;
				needTotal = needTotal + 1;
				// System.out.println("消耗一个来做句子");
			} else {// 这里消耗两个
				index = index + 1;
				needTotal = needTotal + 2;
				// System.out.println("消耗两个:");
			}
		}
		return needTotal;
	}

	
	
	public static List<Integer> getCardsWithoutCardNumbers(List<Integer> cards, Integer[] cardNumbers) {
		List<Integer> list = new LinkedList<>();
		for (int i = 0; i < cards.size(); i++) {
			Integer card = cards.get(i);
			boolean isEqual = false;
			for (int j = 0; j < cardNumbers.length; j++) {
				Integer notInCard = cardNumbers[j];
				if (card == notInCard) {
					isEqual = true;
					break;
				}
			}
			if (!isEqual) {
				list.add(card);
			}
		}

		return list;
	}

	private static boolean checkWithTong(Integer hongZhong, Integer wanNeed, Integer tiaoNeed, List<Integer> bingList) {
		// 2.假如将在【筒】里面那么【万】【条】必然是整扑。
		int needHunNum = wanNeed + tiaoNeed;
		int hadHunNum = hongZhong - needHunNum;
		if (hadHunNum >= 0) {// 需求的比拥有的多 就不做判断
			return removeDui(bingList, hadHunNum);
		} else {
			return false;
		}
	}

	private static boolean checkWithWan(Integer hongZhong, Integer bingNeed, Integer tiaoNeed, List<Integer> wanList) {
		// 1.假如将在【万】里面那么【饼】【条】必然是整扑。
		int needHunNum = bingNeed + tiaoNeed; // 需要红中的个数
		int hadHunNum = hongZhong - needHunNum;// 拥有 - 需要
		if (hadHunNum >= 0) {// 需求的比拥有的多 就不做判断
			return removeDui(wanList, hadHunNum);
		} else {
			return false;
		}
	}

	/**
	 * 判断是否可以直接胡
	 * 
	 * @param list
	 * @return
	 */
	public boolean winZhiJieHu(List<Integer> list) {
		int total = 0;
		for (int i = list.size() - 1; i >= 0; i--) {
			Integer card = list.get(i);
			if (card >= 108) {
				total++;
				if (total == 4) {
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * 得到红中数
	 * 
	 * @param list
	 * @return
	 */
	public int getTotalHongZhong(List<Integer> list) {
		int total = 0;
		for (int i = list.size() - 1; i >= 0; i--) {
			Integer card = list.get(i);
			if (card >= 108) {
				total++;
			} else {
				return total;
			}
		}
		return total;
	}

	/**
	 * 分类得到牌的集合，
	 * 
	 * @param list
	 *            0 万 1 筒 2条 3中
	 * @return
	 */
	public static Map<Integer, List<Integer>> getPaiXingCards(List<Integer> list) {
		Map<Integer, List<Integer>> cardsMap = new HashMap<>();
		cardsMap.put(0, new LinkedList<Integer>());// 万
		cardsMap.put(1, new LinkedList<Integer>());// 筒
		cardsMap.put(2, new LinkedList<Integer>());// 条
		cardsMap.put(3, new LinkedList<Integer>());// 中
		for (int i = 0; i < list.size(); i++) {
			Integer card = list.get(i);
			if (card >= 0 && card <= 35) {
				List<Integer> wanList = cardsMap.get(0);
				wanList.add(card);
				// cardsMap.put(0, wanList);// 万
			} else if (card >= 36 && card <= 71) {
				List<Integer> tongList = cardsMap.get(1);
				tongList.add(card);
				// cardsMap.put(1, tongList);// 筒
			} else if (card >= 72 && card <= 107) {
				List<Integer> tiaoList = cardsMap.get(2);
				tiaoList.add(card);
				// cardsMap.put(2, tiaoList);// 条
			} else if (card >= 108 && card <= 111) {
				List<Integer> zhongList = cardsMap.get(3);
				zhongList.add(card);
				// cardsMap.put(3, zhongList);// 中
			}
		}
		return cardsMap;
	}

	/**
	 * 现在先获取 key【万 0】【饼 1】【条 2】各自成为整扑所需要癞子的个数
	 * 
	 * @param cardsMap
	 * @return
	 */
	public static Map<Integer, Integer> getKindsNeedHongZhong(Map<Integer, List<Integer>> cardsMap) {
		Map<Integer, Integer> cards = new HashMap<>();
		List<Integer> wanList = cardsMap.get(0);
		List<Integer> bingList = cardsMap.get(1);
		List<Integer> tiaoList = cardsMap.get(2);
		// 万、需要的红中数
		int wanNeed = getEveryNeedtotal(wanList);
		int bingNeed = getEveryNeedtotal(bingList);
		int tiaoNeed = getEveryNeedtotal(tiaoList);
		
		cards.put(0, wanNeed);// 万
		cards.put(1, bingNeed);// 筒
		cards.put(2, tiaoNeed);// 条
		return cards;
	}

	/**
	 * 得到每一种牌需要的红中数
	 * 
	 * @return
	 */
	public static int getEveryNeedtotal(List<Integer> cards) {
		if (cards.size() <= 3) {// 3 张牌 123、124、135、147
			return getWithLtThree(cards);
		} else {
			List<Integer> cCards = getList(cards);
			return getWithGtThree(cCards);
		}
	}

	/**
	 * 得到大于3的牌,需要的红中数
	 * 2条 2条 3条 3条 4条 4条 4条 5条 6条 
	 * @param cards
	 * @return
	 */
	private static int getWithGtThree2(List<Integer> cards) {
		 //showPai(cards);
		// 得到所有重复的集合5567899
		List<Integer> canList = getCanList(cards);
		while(canList.size()>0){
			Integer c1 = canList.get(0);
			Integer c2 = canList.get(1);
			Integer c3 = canList.get(2);
			cards.remove(c1);
			cards.remove(c2);
			cards.remove(c3);
			canList = getCanList(cards);
		}
		
		List<Integer> notDistinctList = getNotDistinctWithoutSen(cards);// 得到不重复的牌
		//showPai(notDistinctList);
		// 得到所有重复的集合56789:567
		List<Integer> sentensList = getSentensList(notDistinctList);// 得到不重复的牌
		// 判断该张牌的后一张，能否组成一个句子,并且该张牌是一对,保留对子，不拆散组合
		if (sentensList.size() > 0) {
			Integer first = sentensList.get(0);
			// 是否有一张牌和该牌号相等
			int haveAcommonType = isHaveAcommonType(cards, first);
			if (haveAcommonType>0) {
				Integer second = sentensList.get(1);
				Integer third =  sentensList.get(2);
				boolean haveAcommonTypeSecond = isHaveAcommonTypeHaveTwo(cards, second);
				boolean haveAcommonTypeThird = isHaveAcommonTypeHaveTwo(cards, third);
				if(!haveAcommonTypeSecond&&!haveAcommonTypeThird){
					int nowIndex = notDistinctList.indexOf(first);
					List<Integer> nextOneList = nextOneIsSentens(notDistinctList,nowIndex);
					if (nextOneList.size() > 0) {// 下一个是句子
						Integer lastCard = nextOneList.get(2);
						int lastCardIndex = cards.indexOf(lastCard);
						if(lastCardIndex>(cards.size()-3)){
							sentensList = nextOneList;
						}
					}
				}else if(haveAcommonTypeSecond&&!haveAcommonTypeThird){
					int totalSecondType = getTotalOneType(cards, second);
					int totalThirdType = getTotalOneType(cards, third);
					int totalFirstType = getTotalOneType(cards, first);
					if(totalSecondType==1&&totalThirdType==0&&totalFirstType==0){
						int nowIndex = notDistinctList.indexOf(second);
						List<Integer> nextOneList = nextOneIsSentens(notDistinctList,nowIndex);
						if (nextOneList.size() > 0) {// 下一个是句子
							sentensList = nextOneList;
						}
					}
				}
			}
		}
		while (sentensList.size() > 0) {
			cards = removeListFromList(cards, sentensList);// 345
			notDistinctList = getNotDistinctWithoutSen(cards);// 2345
			sentensList = getSentensList(notDistinctList);
			if (sentensList.size() > 0) {
				Integer first = sentensList.get(0);
				// 是否有一张牌和该牌号相等
//				boolean haveAcommonType = isHaveAcommonType(notDistinctList, first);
				int haveAcommonType = isHaveAcommonType(cards, first);
				if (haveAcommonType>0) {
					List<Integer> gtList = getGtList(notDistinctList, first);
					List<Integer> nextOneList = nextOneIsSentens(gtList);
					if (nextOneList.size() > 0) {// 下一个是句子
						sentensList = nextOneList;
					}
				}
			}

		}
		cards = getCardsWithoutCan(cards);
		return getNeed(cards);
	}
	
	/**
	 * 得到大于3的牌,需要的红中数 2条 2条 3条 3条 4条 4条 4条 5条 6条
	 * 
	 * @param cards
	 * @return
	 */
	private static int getWithGtThree(List<Integer> cards) {
		// showPai(cards);
		// 得到所有重复的集合5567899
		List<Integer> canList = getCanList(cards);
		while (canList.size() > 0) {
			Integer c1 = canList.get(0);
			Integer c2 = canList.get(1);
			Integer c3 = canList.get(2);
			cards.remove(c1);
			cards.remove(c2);
			cards.remove(c3);
			canList = getCanList(cards);
		}
		List<Integer> notDistinctList = getNotDistinctWithoutSen(cards);// 得到不重复的牌
		// showPai(notDistinctList);
		// 得到所有重复的集合56789:567
		List<Integer> sentensList = getSentensList(notDistinctList);// 得到不重复的牌
		// 判断该张牌的后一张，能否组成一个句子,并且该张牌是一对,保留对子，不拆散组合
		if (sentensList.size() > 0) {
			Integer first = sentensList.get(0);
			int nowIndex = notDistinctList.indexOf(first);
			Integer sententsLast = sentensList.get(2);
			Integer sentensIndexOfLast = cards.indexOf(sententsLast);
			System.out.println("indexOf:----" + sentensIndexOfLast + " Last:" + CardsMap.getCardType(sententsLast));

			boolean isHaveFirstSentens = false;

			if (sentensIndexOfLast >= 4) {// 11223455667
				Integer n4 = cards.get(sentensIndexOfLast-4);
				Integer n3 = cards.get(sentensIndexOfLast-3);
				Integer n2 = cards.get(sentensIndexOfLast-2);
				Integer n1 = cards.get(sentensIndexOfLast-1);
				String cardTypeN4 = CardsMap.getCardType(n4);
				String cardTypeN3 = CardsMap.getCardType(n3);
				String cardTypeN2 = CardsMap.getCardType(n2);
				String cardTypeN1 = CardsMap.getCardType(n1);
				if(cardTypeN2.equals(cardTypeN1)&&cardTypeN3.equals(cardTypeN4)){//2233444
					int interval1 = getInterval(n3, n2);
					int interval2 = getInterval(first,n1);
					if(interval1==1&&interval2==1){
						isHaveFirstSentens = true;
					}
				}
			}

			if (!isHaveFirstSentens) {
				List<Integer> nextOneList = nextOneIsSentens(notDistinctList, nowIndex);
				if (nextOneList.size() > 0) {
					while (nextOneList.size() > 0) {

						Integer indexOfLast = nextOneList.get(2);
						int indexOf = cards.indexOf(indexOfLast);
						int size = cards.size();
						int t = size - indexOf;
						// System.out.println(t);
						int haveAcommonType = isHaveAcommonType(cards, first);
						int indexOfFirst = cards.indexOf(first);
						System.out.println("h" + haveAcommonType + "t" + t);
						if (indexOfFirst == 0) {// 如果第一个就是句子终止判断
							break;
						}

						if (haveAcommonType > 0 && t == 1) {
							sentensList = nextOneList;
						} else if (t > 2) {
							// System.out.println("nowIndex:"+nowIndex);
							if (nowIndex != 0) {
								sentensList = nextOneList;
							}
						}
						first = nextOneList.get(0);
						nowIndex = notDistinctList.indexOf(first);
						nextOneList = nextOneIsSentens(notDistinctList, nowIndex);
					}
				}

			}
		}

		while (sentensList.size() > 0) {
			cards = removeListFromList(cards, sentensList);// 345
			notDistinctList = getNotDistinctWithoutSen(cards);// 2345
			sentensList = getSentensList(notDistinctList);
			if (sentensList.size() > 0) {
				Integer first = sentensList.get(0);
				// 是否有一张牌和该牌号相等
				// boolean haveAcommonType = isHaveAcommonType(notDistinctList,
				// first);
				int haveAcommonType = isHaveAcommonType(cards, first);
				if (haveAcommonType > 0) {
					List<Integer> gtList = getGtList(notDistinctList, first);
					List<Integer> nextOneList = nextOneIsSentens(gtList);
					if (nextOneList.size() > 0) {// 下一个是句子
						sentensList = nextOneList;
					}
				}
			}

		}
		cards = getCardsWithoutCan(cards);
		return getNeed(cards);
	}

	/**得到一个类型的个数
	 * @return
	 */
	public static int getTotalOneType(List<Integer> list,Integer compareCard){
		String compareCardType = CardsMap.getCardType(compareCard);
		int total = 0;
		for (int i = 0; i < list.size(); i++) {
			Integer card = list.get(i);
			if (card != compareCard) {
				String cardType = CardsMap.getCardType(card);
				if(compareCardType.equals(cardType)){
					total++;
				}
			}
		}
		
		return total;
	}
	
	
	
	
	/**
	 * 看看是否有一个同样的牌,或者是它们的间隔大于0并且小于2,总之接近的类型
	 * @return
	 */
	public static boolean isHaveAcommonTypeHaveTwo(List<Integer> cards, int compareCard) {
		String compareCardType = CardsMap.getCardType(compareCard);
		int total = 0;
		for (int i = 0; i < cards.size(); i++) {
			Integer card = cards.get(i);
			if (card != compareCard) {
				String cardType = CardsMap.getCardType(card);
				if(compareCardType.equals(cardType)){
					total++;
				}
			}
		}
		if(total>=1){
			return true;
		}
		return false;
	}
	
	/**
	 * @param cards
	 * @param begin
	 * @return
	 */
	public static List<Integer> getGtList(List<Integer> cards, int begin) {
		List<Integer> list = new LinkedList<>();
		for (int i = 0; i < cards.size(); i++) {
			Integer card = cards.get(i);
			if (card >= begin) {
				list.add(card);
			}
		}
		return list;
	}

	
	public static List<Integer> getCardsWithoutCan(List<Integer> list) {
		List<Integer> canList = getCanList(list);
		if (canList.size() > 0) {
			list = removeListFromList(list, canList);
		}
		return list;
	}

	/**
	 * @param cards
	 * @return
	 */
	private static List<Integer> getCanList(List<Integer> cards) {
		List<Integer> result = new LinkedList<>();
		for (int i = 0; i < cards.size();) {
			if (i >= cards.size()) {
				break;
			}
			int n1 = cards.get(i);
			int n2 = -1;
			int n3 = -2;
			if (i + 1 < cards.size()) {
				n2 = cards.get(i + 1);
			}
			if (i + 2 < cards.size()) {
				n3 = cards.get(i + 2);
			}
			boolean oneSentence = isOneCan(n1, n2, n3);
			if (oneSentence) {
				i = i + 3;
				result.add(n1);
				result.add(n2);
				result.add(n3);
			} else {
				i = i + 1;
			}
		}
		
		if(result.size()>0){
			Integer indexOfLast = result.get(2);
			int indexOf = cards.indexOf(indexOfLast);
			List<Integer> isSentens = nextOneIsSentens(cards,indexOf-1);
			Integer first = result.get(0);
			int indexOfFirst = cards.indexOf(first);
			if(isSentens.size()>0){
				System.out.println("---"+cards.size()+" index:"+indexOf);
				if(indexOf+3<cards.size()){
					Integer a1 = cards.get(indexOf+1);
					Integer a2 = cards.get(indexOf+2);
					Integer a3 = cards.get(indexOf+3);
					String cardTypeA1 = CardsMap.getCardType(a1);
					String cardTypeA2 = CardsMap.getCardType(a2);
					String cardTypeA3 = CardsMap.getCardType(a3);
					System.out.println("/////"+cardTypeA1+" "+cardTypeA2+" "+cardTypeA3);
					boolean oneSentence = isOneSentence(a1, a2, a3);
					if(!oneSentence){
						result = new LinkedList<>();
					}
				}
				
			} 
			
			if(indexOfFirst>=4){
					Integer n4 = cards.get(indexOfFirst-4);
					Integer n3 = cards.get(indexOfFirst-3);
					Integer n2 = cards.get(indexOfFirst-2);
					Integer n1 = cards.get(indexOfFirst-1);
					String cardTypeN4 = CardsMap.getCardType(n4);
					String cardTypeN3 = CardsMap.getCardType(n3);
					String cardTypeN2 = CardsMap.getCardType(n2);
					String cardTypeN1 = CardsMap.getCardType(n1);
					if(cardTypeN4.equals(cardTypeN3)&&cardTypeN2.equals(cardTypeN1)){//2233444
						int interval1 = getInterval(n3, n2);
						int interval2 = getInterval(n1, first);
						if(interval1==1&&interval2==1){
							result = new LinkedList<>();
							System.out.println(cardTypeN4+" "+cardTypeN3+" "+cardTypeN2+" "+cardTypeN1);
							System.out.println(interval1+" "+interval2);
						}
					}
				System.out.println("indexOfFirst:"+indexOfFirst);
			}
			
			if(indexOfFirst==3){//566777
				Integer n3 = cards.get(indexOfFirst-3);
				Integer n2 = cards.get(indexOfFirst-2);
				Integer n1 = cards.get(indexOfFirst-1);
				String cardTypeN3 = CardsMap.getCardType(n3);
				String cardTypeN2 = CardsMap.getCardType(n2);
				String cardTypeN1 = CardsMap.getCardType(n1);
				if(cardTypeN2.equals(cardTypeN1)){//2233444
					int interval1 = getInterval(n3, n2);
					int interval2 = getInterval(n1, first);
					if(interval1==1&&interval2==1){
						result = new LinkedList<>();
						System.out.println(cardTypeN3+" "+cardTypeN2+" "+cardTypeN1);
						System.out.println(interval1+" "+interval2);
					}
				}
			}
			
		}
		return result;
	}

	/**
	 * 看看是不是一个坎
	 * @param num1
	 * @param num2
	 * @param num3
	 * @return
	 */
	public static boolean isOneCan(int num1, int num2, int num3) {
		String type1 = CardsMap.getCardType(num1);
		String type2 = CardsMap.getCardType(num2);
		String type3 = CardsMap.getCardType(num3);

		if (type1.equals(type2) && type1.equals(type3)) {
			// System.out.println("坎:"+CardsMap.getCardType(num1));
			return true;
		}

		return false;
	}

	/**
	 * 从牌中移除句子
	 * 
	 * @param cards
	 * @param sentensList
	 */
	private static List<Integer> removeListFromList(List<Integer> cards, List<Integer> sentensList) {
		List<Integer> list = new LinkedList<>();
		for (int i = 0; i < cards.size(); i++) {
			Integer card = cards.get(i);
			boolean canAdd = true;
			for (int j = 0; j < sentensList.size(); j++) {
				Integer remove = sentensList.get(j);
				if (card == remove) {
					canAdd = false;
					break;
				}
			}
			if (canAdd) {
				list.add(card);
			}
		}
		return list;
	}

	
	/**
	 * @param sentensList
	 * @return
	 */
	public static List<Integer> nextOneIsSentens(List<Integer> notDistinctList) {
		List<Integer> list = new LinkedList<>();
		if (notDistinctList.size() <= 3) {
			return list;
		}
		int	n1 = notDistinctList.get(1);
		int	n2 = notDistinctList.get(2);
		int	n3 = notDistinctList.get(3);
		boolean oneSentence = isOneSentence(n1, n2, n3);
		if (oneSentence) {
			list.add(n1);
			list.add(n2);
			list.add(n3);
		}
		return list;
	}
	
	/**
	 * @param sentensList
	 * @return
	 */
	public static List<Integer> nextOneIsSentens(List<Integer> notDistinctList,int nowIndex) {
		List<Integer> list = new LinkedList<>();
		if (notDistinctList.size() <= 3) {
			return list;
		}
		int n1 = -1;
		int n2 = -2;
		int n3 = -3;
		if(nowIndex+1<notDistinctList.size()){
			n1 = notDistinctList.get(nowIndex+1);
		}
		if(nowIndex+2<notDistinctList.size()){
			n2 = notDistinctList.get(nowIndex+2);
		}
		if(nowIndex+3<notDistinctList.size()){
			n3 = notDistinctList.get(nowIndex+3);
		}
		boolean oneSentence = isOneSentence(n1, n2, n3);
		if (oneSentence) {
			list.add(n1);
			list.add(n2);
			list.add(n3);
		}
		return list;
	}

	/**
	 * 看看是否有一个同样的牌,或者是它们的间隔大于0并且小于2,总之接近的类型
	 * 
	 * @return
	 */
	public static int isHaveAcommonType(List<Integer> cards, int compareCard) {
		String compareCardType = CardsMap.getCardType(compareCard);
		for (int i = 0; i < cards.size(); i++) {
			Integer card = cards.get(i);
			if (card != compareCard) {
				String cardType = CardsMap.getCardType(card);
				int interval = getInterval(card, compareCard);
				if (compareCardType.equals(cardType) || (interval <= 2 && interval >= 0)) {
					return card;
				}
			}
		}
		return -1;
	}

	private static List<Integer> getSentensList(List<Integer> list) {
		List<Integer> result = new LinkedList<>();
		for (int i = 0; i < list.size();) {
			if (i >= list.size()) {
				break;
			}
			int n1 = list.get(i);
			int n2 = -1;
			int n3 = -2;
			if (i + 1 < list.size()) {
				n2 = list.get(i + 1);
			}
			if (i + 2 < list.size()) {
				n3 = list.get(i + 2);
			}
			boolean oneSentence = isOneSentence(n1, n2, n3);
			if (oneSentence) {
				i = i + 3;
				// i = i+1;
				result.add(n1);
				result.add(n2);
				result.add(n3);

				if (result.size() == 3) {
					return result;
				}

			} else {
				i = i + 1;
			}
		}
		return result;
	}

	/**
	 * 去除所有牌型重复的牌
	 * 
	 * @param cards
	 */
	private static List<Integer> getNotDistinctWithoutSen(List<Integer> cards) {
		String type = "";// 前一张牌的牌型
		List<Integer> list = new LinkedList<>();
		int total = 0;
		for (int i = 0; i < cards.size(); i++) {
			Integer card = cards.get(i);
			String cardType = CardsMap.getCardType(card);// 当前的牌型
			if (!type.equals(cardType)) {
				list.add(card);
				type = cardType;
				total++;
			} else {// 是一张重复的牌,移除当前牌
				Integer c = list.remove(total - 1);
				list.add(card);
			}
		}
		return list;
	}

	/**
	 * 得到小于3的牌,需要的红中数
	 * 
	 * @param cards
	 * @return
	 */
	private static int getWithLtThree(List<Integer> cards) {
		Integer size = cards.size();
		switch (size) {
		case 0:
			return 0;
		case 1:
			return 2;
		case 2:
			int n1 = cards.get(0);
			int n2 = cards.get(1);
			int interval = getInterval(n1, n2);
			if (interval <= 2) {// 13,12,11
				return 1;
			}
			return 4;
		case 3:
			n1 = cards.get(0);
			n2 = cards.get(1);
			int n3 = cards.get(2);
			boolean oneSentence = isOneSentence(n1, n2, n3);
			boolean oneCan = isOneCan(n1, n2, n3);
			if (oneSentence || oneCan) {
				return 0;
			} else {
				int interval2 = getInterval(n1, n2);
				int interval3 = getInterval(n2, n3);
				if (interval2 <= 2 || interval3 <= 2) {
					return 3;
				} else {
					return 6;
				}
			}
		}
		return 0;
	}

	/**
	 * @param a1
	 * @param a2
	 * @return 如果牌型不相等则返回-1,否则返回它们之间的间隔
	 */
	public static int getInterval(int a1, int a2) {
		String typeA1 = getTypeString(a1);
		String typeA2 = getTypeString(a2);

		if (!typeA1.equals(typeA2)) {
			return -1;
		}

		int typeA1Int = getTypeInt(a1);
		int typeA2Int = getTypeInt(a2);

		return typeA2Int - typeA1Int;

	}

	/**
	 * 看看是不是一个句子
	 * 
	 * @param a1
	 * @param a2
	 * @param a3
	 * @return
	 */
	public static boolean isOneSentence(int a1, int a2, int a3) {
		String typeA1 = getTypeString(a1);
		String typeA2 = getTypeString(a2);
		String typeA3 = getTypeString(a3);
		if (!typeA1.equals(typeA2) || !typeA1.equals(typeA3) || !typeA2.equals(typeA3)) {
			return false;
		}
		int typeA1Int = getTypeInt(a1);
		int typeA2Int = getTypeInt(a2);
		int typeA3Int = getTypeInt(a3);
		if (typeA2Int - typeA1Int != 1 && typeA2Int - typeA1Int != -1) {
			return false;
		}
		if (typeA3Int - typeA2Int != 1 && typeA3Int - typeA2Int != -1) {
			return false;
		}
		return true;
	}

	/**
	 * 根据牌号计算出牌的类型
	 * 
	 * @param paiHao
	 * @return
	 */
	public static int getTypeInt(int paiHao) {
		int type = 0;
		if (paiHao >= 0 && paiHao <= 35) {// 万
			type = (paiHao / 4) + 1;
		} else if (paiHao >= 36 && paiHao <= 71) {
			type = ((paiHao / 4) - 9) + 1;
		} else if (paiHao >= 72 && paiHao <= 107) {
			type = ((paiHao / 4) - 18) + 1;
		}
		return type;
	}

	/**
	 * 根据牌号计算出牌的类型
	 * 
	 * @param paiHao
	 * @return
	 */
	public static String getTypeString(int paiHao) {
		String type = "";
		if (paiHao >= 0 && paiHao <= 35) {// 万
			type = "万";
		} else if (paiHao >= 36 && paiHao <= 71) {
			type = "筒";
		} else if (paiHao >= 72 && paiHao <= 107) {
			type = "条";
		}
		return type;
	}

	/**
	 * 得到没有红中的的集合
	 * 
	 * @param arr
	 */
	public static List<Integer> getListWithoutHongZhong(int[] arr) {
		List<Integer> list = new LinkedList<Integer>();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] < 108) {
				list.add(arr[i]);
			}
		}
		return list;
	}

}
