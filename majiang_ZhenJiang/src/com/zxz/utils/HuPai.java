package com.zxz.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zxz.algorithm.InitialPuKe;
import com.zxz.algorithm.TingPai;

public class HuPai {

	private boolean isDiaoJiang = false;
	private boolean isHu = false;

	public static void main(String[] args) {
//		long currentTimeMillis = System.currentTimeMillis();
//		int[] array = {22,26,28,48,52,56,73,74};
//		List<Integer> myCards =  new ArrayList<>();
//		showPai(array);
//		for(int i=0;i<array.length;i++){
//			myCards.add(array[i]);
//		}
//		HuPai huPai = new HuPai();
//		boolean hu = huPai.isHu(myCards, 77);
//		boolean diaoJiang = huPai.isDiaoJiang();
//		System.out.println(hu+""+diaoJiang);
//		long currentTimeMillis2 = System.currentTimeMillis();
//		System.out.println(currentTimeMillis2 - currentTimeMillis);
		long currentTimeMillis = System.currentTimeMillis();
		int[] array = {52,56,60,88};
		List<Integer> arrayToList = arrayToList(array);
		List<Integer> tingList = getTingList(arrayToList, 88);
		System.out.println(tingList);
		System.out.println(tingList.size());
		long currentTimeMillis2 = System.currentTimeMillis();
		System.out.println(currentTimeMillis2 - currentTimeMillis);
	}

	
	
	/**得到听牌的集合
	 * @param myCards
	 * @param baiDa
	 * @return
	 */
	public static List<Integer> getTingList(List<Integer> myCards,int baiDa) {
		Integer temp = null;
		List<Integer> ting =  new ArrayList<>();
		
		for(int i=0;i<=135;i=i+4){
			if(temp!=null){
				myCards.remove(temp);
			}
			myCards.add(new Integer(i));
			Collections.sort(myCards);
			temp = new Integer(i);
			HuPai huPai = new HuPai();
			boolean hu = huPai.isHu(myCards, baiDa);
			if(hu){
				ting.add(i);
			}
		}
		return ting;
	}

	
	
	public boolean isHu() {
		return isHu;
	}

	public void setHu(boolean isHu) {
		this.isHu = isHu;
	}

	public boolean isDiaoJiang() {
		return isDiaoJiang;
	}

	public void setDiaoJiang(boolean isDiaoJiang) {
		this.isDiaoJiang = isDiaoJiang;
	}

	public static String showPai(int array[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			System.out.print(InitialPuKe.map.get(array[i]) + " ");
			sb.append(InitialPuKe.map.get(array[i]) + " ");
		}
		System.out.println();
		return sb.toString();
	}

	public static String showPai(List<Integer> list) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			System.out.print(InitialPuKe.map.get(list.get(i)) + " ");
			sb.append(InitialPuKe.map.get(list.get(i)) + " ");
		}
		System.out.println();
		return sb.toString();
	}
	
	public boolean isHu(int[] array, int bdNumber) {
		List<Integer> arrayToList = arrayToList(array);
		return isHu(arrayToList, bdNumber);
	}

	public boolean isHu(List<Integer> arrayToList, int bdNumber) {
		Map<Integer, List<Integer>> cardMap = getCardMap(arrayToList, bdNumber);
		int wanNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(1)), 0);
		int bingNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(2)), 0);
		int tiaoNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(3)), 0);
		int zhongNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(4)), 0);
		int faNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(5)), 0);
		int baiNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(6)), 0);
		int dongNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(7)), 0);
		int nanNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(8)), 0);
		int xiNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(9)), 0);
		int beiNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(10)), 0);
		int remainsHongZhong = 0;
		int haveHongZhong = cardMap.get(0).size();
		remainsHongZhong = haveHongZhong - bingNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - dongNeed - nanNeed
				- xiNeed - beiNeed;
		// 将在万中
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(1), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在饼中
		remainsHongZhong = haveHongZhong - wanNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - dongNeed - nanNeed
				- xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(2), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在条中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - zhongNeed - faNeed - baiNeed - dongNeed - nanNeed
				- xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(3), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在红中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - faNeed - baiNeed - dongNeed - nanNeed
				- xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(4), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在发中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongNeed - baiNeed - dongNeed - nanNeed
				- xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(5), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在白中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongNeed - faNeed - dongNeed - nanNeed
				- xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(6), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在东中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - nanNeed
				- xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(7), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在南中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - dongNeed
				- xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(8), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在西中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - dongNeed
				- nanNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(9), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在北中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - dongNeed
				- nanNeed - xiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(10), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		return false;
	}

	/**
	 * @return
	 */
	public static Map<Integer, List<Integer>> getCardMap(List<Integer> cards, int bdNumber) {
		bdNumber = bdNumber / 4;
		Map<Integer, List<Integer>> map = new LinkedHashMap<Integer, List<Integer>>();
		List<Integer> list0 = new LinkedList<Integer>();
		List<Integer> list1 = new LinkedList<Integer>();
		List<Integer> list2 = new LinkedList<Integer>();
		List<Integer> list3 = new LinkedList<Integer>();
		List<Integer> list4 = new LinkedList<Integer>();
		List<Integer> list5 = new LinkedList<Integer>();
		List<Integer> list6 = new LinkedList<Integer>();
		List<Integer> list7 = new LinkedList<Integer>();
		List<Integer> list8 = new LinkedList<Integer>();
		List<Integer> list9 = new LinkedList<Integer>();
		List<Integer> list10 = new LinkedList<Integer>();
		map.put(0, list0);
		map.put(1, list1);
		map.put(2, list2);
		map.put(3, list3);
		map.put(4, list4);
		map.put(5, list5);
		map.put(6, list6);
		map.put(7, list7);
		map.put(8, list8);
		map.put(9, list9);
		map.put(10, list10);
		for (int i = 0; i < cards.size(); i++) {
			int card = cards.get(i) / 4;
			if (card == bdNumber) {
				map.get(0).add(card);
			} else if (card < 9 && card != bdNumber) {
				map.get(1).add(card);
			} else if (card < 18 && card != bdNumber) {
				map.get(2).add(card);
			} else if (card < 27 && card != bdNumber) {
				map.get(3).add(card);
			} else if (card < 28 && card != bdNumber) {
				map.get(4).add(card);
			} else if (card < 29 && card != bdNumber) {
				map.get(5).add(card);
			} else if (card < 30 && card != bdNumber) {
				map.get(6).add(card);
			} else if (card < 31 && card != bdNumber) {
				map.get(7).add(card);
			} else if (card < 32 && card != bdNumber) {
				map.get(8).add(card);
			} else if (card < 33 && card != bdNumber) {
				map.get(9).add(card);
			} else if (card < 34 && card != bdNumber) {
				map.get(10).add(card);
			}
		}
		return map;
	}

	public static List<Integer> arrayToList(int[] array) {
		List<Integer> list = new LinkedList<>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}

	public static List<Integer> getNewListFromOldList(List<Integer> cards) {
		List<Integer> list = new LinkedList<>();
		for (int i = 0; i < cards.size(); i++) {
			list.add(cards.get(i));
		}
		return list;
	}

	private boolean isHuNoDui(List<Integer> cards, int haveHongZhong) {
		int step = 1;
		boolean result = false;
		if(cards.size()==0&&haveHongZhong>0){
			this.isDiaoJiang = true;
			return true;
		}
		for (int i = 0; i < cards.size(); i = i + step) {
			Integer now = cards.get(i);
			Integer next = null;
			if (i + 1 <= cards.size() - 1) {
				next = cards.get(i + 1);
			}
			List<Integer> newListFromOldList = getNewListFromOldList(cards);
			newListFromOldList.remove(now);
			if (now == next) {// 有将
				newListFromOldList.remove(next);
				int totalNeed = getTotalNeedNoDui(newListFromOldList, 0);
				if (haveHongZhong - totalNeed >= 0) {
					if (haveHongZhong - totalNeed > 0) {
						this.isDiaoJiang = true;
					}
					result = true;
				}
				step = 1;
			} else {// 无将
				int totalNeed = getTotalNeedNoDui(newListFromOldList, 1);
				if (haveHongZhong - totalNeed >= 0) {
					if (haveHongZhong - totalNeed > 0) {
						this.isDiaoJiang = true;
					}
					result = true;
				}
				step = 1;
			}
		}
		return result;
	}

	/**
	 * @return
	 */
	public static int getTotalNeedNoDui(List<Integer> cards, int need) {
		int size = cards.size();
		if (size == 0) {
			return need;
		}
		if (size == 1) {
			need = need + 2;
			return need;
		}
		if (size == 2) {
			Integer c0 = cards.get(0);
			Integer c1 = cards.get(1);
			int abs = Math.abs(c1 - c0);
			if (abs <= 2) {
				need = need + 1;
			} else {
				need = need + 4;
			}
			return need;
		}
		//要做坎
		Integer c0 = cards.get(0);
		Integer c1 = cards.get(1);
		Integer c2 = cards.get(2);
		List<Integer> kanList = getNewListFromOldList(cards);
		int minNeed = 1000;
		if(c0==c1&&c1==c2){
			kanList.remove(c0);
			kanList.remove(c1);
			kanList.remove(c2);
			minNeed = getTotalNeedNoDui(kanList, need);
			if(kanList.size()==0){
				return minNeed;
			}
		}
		if(minNeed==0){
			return minNeed;
		}
		//要做顺
		List<Integer> shunList = getNewListFromOldList(cards);
		Integer n1 = null;
		Integer n2 = null;
		for(int i=0;i<shunList.size();i++){
			Integer c = shunList.get(i);
			if(c-c0==1&&n1==null){
				n1 = c;
			}
			if(n1!=null&&c-n1==1){
				n2 = c;
				break;
			}
		}
		if(n1!=null&&n2!=null){
			shunList.remove(c0);
			shunList.remove(n1);
			shunList.remove(n2);
			int minOfShun = getTotalNeedNoDui(shunList, need);
			if(minNeed>=minOfShun){
				minNeed = minOfShun;
			}
			if(minOfShun==0){
				return minOfShun;
			}
			List<Integer> jiangList = getNewListFromOldList(cards);
			int minOfDui = 0;
			if(minOfShun>0){
				Integer n3 = null;
				Integer n4 = null;
				for(int i=0;i<jiangList.size();i++){
					Integer c = jiangList.get(i);
					if(c-c0==1&&n3==null&&c!=n1){
						n3 = c;
					}
					if(n3!=null&&c-n3==1){
						n4 = c;
						break;
					}
				}
				if(n3!=null&&n4!=null){
					jiangList.remove(c0);
					jiangList.remove(n3);
					jiangList.remove(n4);
					minOfShun = getTotalNeedNoDui(jiangList, need);
					if(minNeed>=minOfShun){
						minNeed = minOfShun;
					}
					if(minOfShun==0){
						return minOfShun;
					}
				}
				
				List<Integer> nowList = getNewListFromOldList(cards);
				Integer n5 = null;
				Integer n6 = null;
				for(int i=0;i<nowList.size();i++){
					Integer c = nowList.get(i);
					if(c-c0==1&&n5==null){
						n5 = c;
					}
					if(n5!=null&&c-n5==1&&c!=n2){
						n6 = c;
						break;
					}
				}
				if(n3!=null&&n4!=null){
					nowList.remove(c0);
					nowList.remove(n5);
					nowList.remove(n6);
					minOfShun = getTotalNeedNoDui(nowList, need);
					if(minNeed>=minOfShun){
						minNeed = minOfShun;
					}
					if(minOfShun==0){
						return minOfShun;
					}
				}
				
				List<Integer> jiangList2 = getNewListFromOldList(cards);
				if(c1-c0==0){
					jiangList2.remove(c0);
					jiangList2.remove(c1);
					need = need + 1;
					minOfDui = getTotalNeedNoDui(jiangList2, need);
					if(minNeed>=minOfDui){
						minNeed = minOfDui;
					}
				}
				
			}
		}else{
			List<Integer> noShunZi = getNewListFromOldList(cards);
			if(c1-c0<=2){
				need = need +1;
				noShunZi.remove(c0);
				noShunZi.remove(c1);
				int noDui = getTotalNeedNoDui(noShunZi, need);
				if(minNeed>=noDui){
					minNeed = noDui;
				}
			}else{
				noShunZi.remove(c0);
				need = need +2;
				int noDui = getTotalNeedNoDui(noShunZi, need);
				if(minNeed>=noDui){
					minNeed = noDui;
				}
			}
		}
		return minNeed;
	}
}
