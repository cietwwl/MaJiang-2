package com.zxz.utils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zxz.algorithm.InitialPuKe;

public class HuPaiJiFen {

	private boolean isDiaoJiang = false;
	private boolean isHu = false;

	public static void main(String[] args) {
		long currentTimeMillis = System.currentTimeMillis();
		int[] array = {0,4,8,12,13};
		System.out.println("百搭："+InitialPuKe.map.get(8));
		showPai(array);
		HuPaiJiFen huPai = new HuPaiJiFen();
		boolean hu = huPai.isHu(array,8);
		System.out.println(hu);
		if(hu){
			System.out.println(huPai.isDiaoJiang());
		}
		long currentTimeMillis2 = System.currentTimeMillis();
		System.out.println(currentTimeMillis2 - currentTimeMillis);
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


	public  boolean isHu(int[] array,int bdNumber) {
		List<Integer> arrayToList = arrayToList(array);
		return isHu(arrayToList,bdNumber);
	}

	public boolean isHu(List<Integer> arrayToList,int bdNumber) {
		Map<Integer, List<Integer>> cardMap = getCardMap(arrayToList,bdNumber);
		int wanNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(1)),0);
		int bingNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(2)),0);
		int tiaoNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(3)),0);
		int zhongNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(4)), 0);
		int faNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(5)), 0);
		int baiNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(6)),0);
		int dongNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(7)),0);
		int nanNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(8)),0);
		int xiNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(9)), 0);
		int beiNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(10)),0);
		int remainsHongZhong = 0;
		int haveHongZhong = cardMap.get(0).size();
		remainsHongZhong = haveHongZhong - bingNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - dongNeed - nanNeed - xiNeed - beiNeed;
		// 将在万中
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(1),haveHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在饼中
		remainsHongZhong = haveHongZhong - wanNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - dongNeed - nanNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(2),haveHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在条中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - zhongNeed - faNeed - baiNeed - dongNeed - nanNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(3),haveHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在红中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - faNeed - baiNeed - dongNeed - nanNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(4),haveHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在发中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongNeed - baiNeed - dongNeed - nanNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(5),haveHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在白中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongNeed - faNeed - dongNeed - nanNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(6),haveHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在东中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - nanNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(7),haveHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在南中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - dongNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(8),haveHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在西中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - dongNeed - nanNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(9),haveHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在北中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - dongNeed - nanNeed - xiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(10),haveHongZhong);
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
	public static Map<Integer, List<Integer>> getCardMap(List<Integer> cards,int bdNumber) {
		bdNumber =bdNumber / 4;
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
			if(card==bdNumber){
				map.get(0).add(card);
			}else if (card < 9&& card!=bdNumber){
				map.get(1).add(card);
			} else if (card < 18 && card!=bdNumber ) {
				map.get(2).add(card);
			} else if (card < 27 && card!=bdNumber) {
				map.get(3).add(card);
			} else if (card < 28 && card!=bdNumber) {
				map.get(4).add(card);
			} else if (card < 29 && card!=bdNumber) {
				map.get(5).add(card);
			} else if (card < 30&& card!=bdNumber) {
				map.get(6).add(card);
			} else if (card < 31 && card!=bdNumber) {
				map.get(7).add(card);
			} else if (card < 32&& card!=bdNumber) {
				map.get(8).add(card);
			} else if (card < 33 && card!=bdNumber) {
				map.get(9).add(card);
			} else if (card < 34 && card!=bdNumber) {
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

	private  boolean isHuNoDui(List<Integer> cards,int haveHongZhong) {
		int step = 1;
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
					return true;
				}
				step = 2;
			} else {// 无将
				int totalNeed = getTotalNeedNoDui(newListFromOldList, 1);
				if (haveHongZhong - totalNeed >= 0) {
					//单调将
					this.isDiaoJiang = true;
					return true;
				}
				step = 1;
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	public static int getTotalNeedNoDui(List<Integer> list, int totalNeed) {
		if (list.size() == 0) {
			return totalNeed;
		}

		if (list.size() == 1) {
			totalNeed = totalNeed + 2;
			return totalNeed;
		}

		Integer now = list.get(0);
		Integer next = null;
		if (1 <= list.size() - 1) {
			next = list.get(1);
		}
		Integer nnext = null;
		if (2 <= list.size() - 1) {
			nnext = list.get(2);
		}
		if (now == next && next == nnext) {
			list.remove(now);
			list.remove(next);
			list.remove(nnext);
			return getTotalNeedNoDui(list, totalNeed);
		}
		if (next != null && nnext != null) {
			if (next - now == 1 && nnext - next == 1) {
				list.remove(now);
				list.remove(next);
				list.remove(nnext);
				return getTotalNeedNoDui(list, totalNeed);
			}
		}
		if (now == next || next - 1 == now) {// 必须有顺子
			Integer nextNumber = null;
			Integer nnextNumber = null;
			for (int i = 1; i < list.size(); i++) {
				Integer n = list.get(i);
				if (nextNumber == null && n == now + 1) {
					nextNumber = n;
					continue;
				}
				if (nnextNumber == null && n == now + 2) {
					nnextNumber = n;
				}
				if (nextNumber != null && nnextNumber != null) {
					break;
				}
			}
			if (now != null && nextNumber != null && nnextNumber != null) {
				list.remove(now);
				list.remove(nextNumber);
				list.remove(nnextNumber);
				return getTotalNeedNoDui(list, totalNeed);
			}
			// 没有找到顺子
			list.remove(now);
			list.remove(next);
			totalNeed = totalNeed + 1;
			return getTotalNeedNoDui(list, totalNeed);
		}
		if (next != null && next - now <= 2) {
			totalNeed = totalNeed + 1;
			// 没有找到顺子
			list.remove(now);
			list.remove(next);
			return getTotalNeedNoDui(list, totalNeed);
		} else {
			totalNeed = totalNeed + 2;
			list.remove(now);
			return getTotalNeedNoDui(list, totalNeed);
		}
	}
}
