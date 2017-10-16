package com.gs;

import java.util.LinkedList;
import java.util.List;

public class HuPai2 {

	public static int needHongZhong= 0;
	public static int haveHongZhong= 4;
	
	
	public static void main(String[] args) {
		long currentTimeMillis = System.currentTimeMillis();
		int[] array = {1,1,1,1,2,2,2,2,3,3,3,3,4,5};
		boolean hu = isHu(arrayToList(array));
		System.out.println(hu+" needHongZhong:"+needHongZhong);
		long currentTimeMillis2 = System.currentTimeMillis();
		System.out.println(currentTimeMillis2 - currentTimeMillis);
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

	public static boolean isHu(List<Integer> cards) {
		int step = 1;
		for (int i = 0; i < cards.size(); i = i + step) {
			Integer now = cards.get(i);
			Integer next = null;
			if (i + 1 <= cards.size() - 1) {
				next = cards.get(i + 1);
			}
			List<Integer> newListFromOldList = getNewListFromOldList(cards);
			newListFromOldList.remove(now);
			if (now == next){//�н�
				needHongZhong = 0;
				newListFromOldList.remove(next);
				boolean huWithNoDui = isHuWithNoDui(newListFromOldList);
				if (huWithNoDui) {
					return true;
				}
				step = 2;
			}else{//�޽�
				needHongZhong = 1;
				boolean huWithNoDui = isHuWithNoDui(newListFromOldList);
				if (huWithNoDui) {
					return true;
				}
				step=1;
			}
			if(haveHongZhong-needHongZhong>=0){
				return true;
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	public static boolean isHuWithNoDui(List<Integer> list) {
		if (list.size() == 0) {
			if(needHongZhong==0){
				return true;
			}
			return false;
		}
		
		if(list.size()==1){
			needHongZhong = needHongZhong +2 ;
			return false;
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
			return isHuWithNoDui(list);
		}
		if (next != null && nnext != null) {
			if (next - now == 1 && nnext - next == 1) {
				list.remove(now);
				list.remove(next);
				list.remove(nnext);
				return isHuWithNoDui(list);
			}
		}
		if (now == next || next - 1 == now) {// ������˳��
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
				return isHuWithNoDui(list);
			}
			//û���ҵ�˳��
			list.remove(now);
			list.remove(next);
			needHongZhong = needHongZhong + 1;
			return isHuWithNoDui(list);
		}
		needHongZhong = needHongZhong+4;
		return false;
	}
}
