package com.gs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zxz.algorithm.InitialPuKe;

public class NewAI {

	private static final int TABLE_COUNT_PAGE = 4;
	int needNum_total = 0;
	int needNum_duizi = 0;
	int needNum_min = 0;
	
	public static void main(String[] args) {
		int array [] = { 57,
		        62,
		        63,
		        64,
		        66,
		        69,
		        85,
		        86,
		        95,
		        96,
		        97,
		        102,
		        103,
		        104};
		showPai(array);
		NewAI newAI = new NewAI();
		boolean tryHuPai = newAI.tryHuPai(array);
		System.out.println(tryHuPai);
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
	
	public static String showPai(List<Integer> cards) {
		int[] array = new int[cards.size()];
		for (int i = 0; i < cards.size(); i++) {
			array[i] = cards.get(i);
		}
		String result = showPai(array);
		return result;
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
			if (card < 9) {
				List<Integer> wanList = cardsMap.get(0);
				wanList.add(card);
				// cardsMap.put(0, wanList);// 万
			} else if (card <9*2 ) {
				List<Integer> tongList = cardsMap.get(1);
				tongList.add(card);
				// cardsMap.put(1, tongList);// 筒
			} else if (card <9*3) {
				List<Integer> tiaoList = cardsMap.get(2);
				tiaoList.add(card);
				// cardsMap.put(2, tiaoList);// 条
			} else{
				List<Integer> zhongList = cardsMap.get(3);
				zhongList.add(card);
				// cardsMap.put(3, zhongList);// 中
			}
		}
		return cardsMap;
	}
	
	
	public static List<Integer> getList(int[] list) {
		List<Integer> returnList = new LinkedList<Integer>();
		for (int i = 0; i < list.length; i++) {
			returnList.add(list[i]);
		}
		return returnList;
	}
	
	/*
	 胡牌检测
	 */
	public  boolean tryHuPai(List<Integer> list){
		int[] temp= new int[list.size()]    ;
		for(int i=0;i<list.size();i++){
			temp[i] = list.get(i)/4;
		}
		List<Integer> list2 = getList(temp);
		return isWin(list2);
	}

	private boolean isWin(List<Integer> list) {
		int needNum_total = 0;
		Map<Integer, List<Integer>> paiXingCards = getPaiXingCards(list);
		int hzNumCur = paiXingCards.get(3).size();
		//万字满足3n模式，需要的数量
		needNum_min = TABLE_COUNT_PAGE;
	    //
		getNeedHunNumToBePu(paiXingCards.get(0), 0);
		int needNum_wan = needNum_min;

		//饼字满足3n模式，需要的数量
		needNum_min = TABLE_COUNT_PAGE;
	   
		getNeedHunNumToBePu(paiXingCards.get(1), 0);
		int needNum_bing = needNum_min;

		//条字满足3n模式，需要的数量
		needNum_min = TABLE_COUNT_PAGE;
		
	    //条TODO
		getNeedHunNumToBePu(paiXingCards.get(2), 0);
		int needNum_tiao = needNum_min;

		//对子在万字牌中的情况
		needNum_total = needNum_bing + needNum_tiao;
		
	
		
		
		if (needNum_total <= hzNumCur)
		{
	     
			int hzNumElse = hzNumCur - needNum_total;
		
			if (arrayCanHu(paiXingCards.get(0), hzNumElse))
			{
				return true;
			}
		}
		//对子在饼字牌中的情况
		needNum_total = needNum_wan + needNum_tiao;
		if (needNum_total <= hzNumCur)
		{
			int hzNumElse = hzNumCur - needNum_total;
			
			if (arrayCanHu(paiXingCards.get(1), hzNumElse))
			{
	           
				return true;
			}
		}
		//对子在条字牌中的情况
		needNum_total = needNum_wan + needNum_bing;
		if (needNum_total <= hzNumCur)
		{
			int hzNumElse = hzNumCur - needNum_total;
			
			if (arrayCanHu(paiXingCards.get(2), hzNumElse))
			{
				return true;
			}
		}
		return false;
	}


	/**
	 * @param typeArray
	 * @param needNum 需要的红中数量
	 */
	public  void getNeedHunNumToBePu(List<Integer> typeArray, int needNum)
	{
	    
	    //整扑需要的红中数量
	    if (needNum_min == 0) {
	       
	        return;
	    }
	    
	    //整扑需要的红中数量小于
	    if (needNum >= needNum_min){
	        
	        return;
	    }
	    
	    
	    int vSize = typeArray.size();
	    
	    
	    
	    if (vSize == 0)//剩余0张牌时
	    {
	        needNum_min = MIN(needNum, needNum_min);
	        return;
	    }
	    else if (vSize == 1)//剩余1张牌时
	    {
	        needNum_min = MIN(needNum + 2, needNum_min);
	        
	        return;
	    }
	    else if (vSize == 2)
	    {
	       Integer p1 = typeArray.get(0);
	       Integer p2 = typeArray.get(1);
	        
	        if (p2 - p1 < 3)
	        {
	            needNum_min = MIN(needNum_min, needNum + 1);
	        }
	        return;
	    }
	    //大于等于3张牌
	    Integer p1 = typeArray.get(0);
	    Integer p2 = typeArray.get(1);
	    Integer p3 = typeArray.get(2);
	    
	    //第一个自己一扑
	    if (needNum + 2 < needNum_min)
	    {
	        
	        typeArray.remove(p1);
	        getNeedHunNumToBePu(typeArray, needNum + 2);
	        typeArray.add(0, p1);;
	    }
	    
	    //第一个跟其它的一个一扑
	    if (needNum + 1 < needNum_min)
	    {
	        for (int i = 1; i < typeArray.size(); i++)
	        {
	            if (needNum + 1 >= needNum_min){
	                break;
	            }
	            
	            p2 = typeArray.get(i);
	            //455567这里可结合的可能为 45 46 否则是45 45 45 46
	            //如果当前的value不等于下一个value则和下一个结合避免重复
	            if (i + 1 != typeArray.size())
	            {
	                p3 = typeArray.get(i+1);
	                if (p3== p2){
	                    continue;
	                }
	            }
	            
	            if (p2 - p1 < 3)
	            {
	                typeArray.remove(p1);
	                typeArray.remove(p2);
	                getNeedHunNumToBePu(typeArray, needNum + 1);
	                typeArray.add(0, p1);
	                typeArray.add(i, p2);
	            }else{
	                break;
	            }
	        }
	    }
	    
	    
	    //第一个和其它两个一扑
	    //后面间隔两张张不跟前面一张相同222234
	    //可能性为222 234
	    for (int i = 1; i < typeArray.size(); i++)
	    {
	        if (needNum >= needNum_min) {
	            break;
	        }
	        
	        p2 = typeArray.get(i);
	        if (i + 2 < typeArray.size())
	        {
	            if ((typeArray.get(i + 2))== p2){
	                continue;
	            }
	        }
	        
	        for ( int j = i + 1; j < typeArray.size(); j++)
	        {
	            if (needNum >= needNum_min) break;
	            p3 = typeArray.get(j);
	            if (j + 1 < typeArray.size())
	            {
	                if (p3 == typeArray.get(j + 1)){
	                    continue;
	                }
	            }
	            
	            if (test3Combine(p1, p2, p3))
	            {
	                
	                typeArray.remove(p1);
	                typeArray.remove(p2);
	                typeArray.remove(p3);
	                getNeedHunNumToBePu(typeArray, needNum);
	                typeArray.add(0, p1);
	                typeArray.add(i, p2);
	                typeArray.add(j, p3);
	            }
	        }
	    }
	    
	}

	private int MIN(int needNum, int needNum_min2) {
		if(needNum<=needNum_min2){
			return needNum;
		}
		return needNum_min2;
	}


	public boolean test3Combine(Integer int1, Integer int2, Integer int3){
		boolean isTrue;
	    if (int1 == int3) {
	        isTrue = true;
	    }else if(int1 + 1 == int2 && int2 + 1 == int3) {
	        isTrue = true;
	    }else{
	        isTrue = false;
	    }
	    
	    return isTrue;
	}


	public boolean arrayCanHu(List<Integer> typeArray, int hunNum)
	{
	    int huSize = typeArray.size();
	    
	    if (huSize <= 0)
	    {
	        if (hunNum >= 2){
	            return true;
	        }
	        return false;
	    }
	    
	    Integer pJiang1 = null;
	    Integer pJiang2 = null;
	    for (int i = 0; i < typeArray.size(); i++) {
	        
	        pJiang1 =typeArray.get(i);
	        
	        if (i == typeArray.size() - 1) {//第一种情况
	            if (hunNum > 0) {
	                
	                hunNum -= 1;
	                
	                typeArray.remove(pJiang1);
	                needNum_min = TABLE_COUNT_PAGE;
	                getNeedHunNumToBePu(typeArray, 0);
	                if (needNum_min <= hunNum)
	                {
	                    return true;
	                }
	                hunNum += 1;
	                typeArray.add(pJiang1);
	            }
	        }else if(i == typeArray.size() - 2){//第二种情况
	            pJiang2 = typeArray.get(i + 1);
	            if (pJiang1 == pJiang2) {
	                typeArray.remove(pJiang1);
	                typeArray.remove(pJiang2);
	                needNum_min = TABLE_COUNT_PAGE;
	                getNeedHunNumToBePu(typeArray, 0);
	                if (needNum_min <= hunNum)
	                {
	                    return true;
	                }
	                typeArray.add(pJiang1);
	                typeArray.add(pJiang2);
	            }else{
	                if (hunNum > 0) {
	                    
	                    hunNum -= 1;
	                    typeArray.remove(pJiang1);
	                    
	                    needNum_min = TABLE_COUNT_PAGE;
	                    getNeedHunNumToBePu(typeArray, 0);
	                    if (needNum_min <= hunNum)
	                    {
	                        return true;
	                    }
	                    
	                    hunNum += 1;
	                    typeArray.add(i, pJiang1);;
	                    
	                }
	            }
	        }else{//第三种情况
//	            pJiang2 = (__Integer*)typeArray->getObjectAtIndex(i + 1);
	            pJiang2 = typeArray.get(i + 1);
	            if (pJiang1 == pJiang2) {
	                
	                if (pJiang2 == typeArray.get(i + 2)) {
	                    continue;
	                }
	                
	                typeArray.remove(pJiang1);
	                typeArray.remove(pJiang2);
	                needNum_min = TABLE_COUNT_PAGE;
	                getNeedHunNumToBePu(typeArray, 0);
	                if (needNum_min <= hunNum)
	                {
	                    return true;
	                }
	                typeArray.add(i, pJiang1);
	                typeArray.add(i + 1, pJiang2);
	            }else{
	                if (hunNum > 0) {
	                    
	                    hunNum -= 1;
	                    typeArray.remove(pJiang1);
	                    needNum_min = TABLE_COUNT_PAGE;
	                    getNeedHunNumToBePu(typeArray, 0);
	                    if (needNum_min <= hunNum)
	                    {
	                        return true;
	                    }
	                    
	                    hunNum += 1;
	                    typeArray.add(i, pJiang1);
	                }
	            }
	        }
	        
	    }
	    return false;
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

	
	public  boolean tryHuPai(int array[]) {
		List<Integer> list = new LinkedList<>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return tryHuPai(list);
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
