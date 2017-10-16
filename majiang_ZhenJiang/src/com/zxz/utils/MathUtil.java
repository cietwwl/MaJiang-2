package com.zxz.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.zxz.algorithm.InitialPuKe;

public class MathUtil {


	/**
	 * 向一个数组中，添加一个数，并且使它仍然有序
	 * 
	 * @param array
	 * @param number
	 * @return
	 */
	public static int[] insertSortArray(int array[], int number) {
		int begin = 0;
		int end = array.length;
		boolean isEnd = false;
		int index = -1;
		int[] copyArray = new int[array.length + 1];
		if (number >= array[array.length - 1]) {
			index = array.length;
		} else if (number <= array[0]) {
			index = 0;
		} else {
			while (!isEnd) {
				int middle = (begin + end) / 2;
				int temp = array[middle];
				if (temp == number) {
					index = middle;
					isEnd = true;
				} else if (number > array[middle] && number <= array[middle + 1]) {
					index = middle + 1;
					isEnd = true;
				} else if (number < array[middle] && number >= array[middle - 1]) {
					index = middle;
					isEnd = true;
				} else if (number > array[middle]) {
					begin = middle;
				} else if (number < array[middle]) {
					end = middle;
				}
				if (begin >= end) {
					isEnd = true;
				}
			}
		}

		if (index == array.length) {
			for (int i = 0; i < array.length; i++) {
				copyArray[i] = array[i];
			}
			copyArray[array.length] = number;
		} else if (index == 0) {
			copyArray[0] = number;
			for (int i = 0; i < array.length; i++) {
				copyArray[i + 1] = array[i];
			}
		} else {
			for (int i = 0; i <= index - 1; i++) {
				copyArray[i] = array[i];
			}

			copyArray[index] = number;
			copyArray[index + 1] = array[index];
			for (int i = index + 1; i < array.length; i++) {
				copyArray[i + 1] = array[i];
			}
		}

		return copyArray;
	}

	/**
	 * 向一个数组中，添加一个数，并且使它仍然有序
	 * 
	 * @param array
	 * @param number
	 * @return
	 */
	public static int[] removeSortArray(int array[], int number) {
		int begin = 0;
		int end = array.length;
		boolean isEnd = false;
		int index = -1;
		int[] copyArray = new int[array.length + 1];
		if (number >= array[array.length - 1]) {
			index = array.length;
		} else if (number <= array[0]) {
			index = 0;
		} else {
			while (!isEnd) {
				int middle = (begin + end) / 2;
				int temp = array[middle];
				if (temp == number) {
					index = middle;
					isEnd = true;
				} else if (number > array[middle] && number <= array[middle + 1]) {
					index = middle + 1;
					isEnd = true;
				} else if (number < array[middle] && number >= array[middle - 1]) {
					index = middle;
					isEnd = true;
				} else if (number > array[middle]) {
					begin = middle;
				} else if (number < array[middle]) {
					end = middle;
				}
				if (begin >= end) {
					isEnd = true;
				}
			}
		}

		if (index == array.length) {
			for (int i = 0; i < array.length; i++) {
				copyArray[i] = array[i];
			}
			copyArray[array.length] = number;
		} else if (index == 0) {
			copyArray[0] = number;
			for (int i = 0; i < array.length; i++) {
				copyArray[i + 1] = array[i];
			}
		} else {
			for (int i = 0; i <= index - 1; i++) {
				copyArray[i] = array[i];
			}

			copyArray[index] = number;
			copyArray[index + 1] = array[index];
			for (int i = index + 1; i < array.length; i++) {
				copyArray[i + 1] = array[i];
			}
		}

		return copyArray;
	}

	public static int binarySearch(int des, int[] srcArray) {
		// 第一个位置.
		int low = 0;
		// 最高位置.数组长度-1,因为下标是从0开始的.
		int high = srcArray.length - 1;
		// 当low"指针"和high不重复的时候.
		while (low <= high) {
			// 中间位置计算,low+ 最高位置减去最低位置,右移一位,相当于除2.也可以用(high+low)/2
			int middle = low + ((high - low) >> 1);
			// 与最中间的数字进行判断,是否相等,相等的话就返回对应的数组下标.
			if (des == srcArray[middle]) {
				return middle;
				// 如果小于的话则移动最高层的"指针"
			} else if (des < srcArray[middle]) {
				high = middle - 1;
				// 移动最低的"指针"
			} else {
				low = middle + 1;
			}
		}
		return -1;
	}

	public static int binarySearch(int des, List<Integer> list) {
		Integer[] srcArray = list.toArray(new Integer[list.size()]);
		// 第一个位置.
		int low = 0;
		// 最高位置.数组长度-1,因为下标是从0开始的.
		int high = srcArray.length - 1;
		// 当low"指针"和high不重复的时候.
		while (low <= high) {
			// 中间位置计算,low+ 最高位置减去最低位置,右移一位,相当于除2.也可以用(high+low)/2
			int middle = low + ((high - low) >> 1);
			// 与最中间的数字进行判断,是否相等,相等的话就返回对应的数组下标.
			if (des == srcArray[middle]) {
				return middle;
				// 如果小于的话则移动最高层的"指针"
			} else if (des < srcArray[middle]) {
				high = middle - 1;
				// 移动最低的"指针"
			} else {
				low = middle + 1;
			}
		}
		return -1;
	}

	/**
	 * 生成指定范围的数
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public static List<Integer> creatRandom(int begin, int end) {
		List<Integer> list = new LinkedList<Integer>();
		for (int i = begin; i <= end; i++) {
			list.add(i);
		}
		Collections.shuffle(list);
		return list;
	}

	/**
	 * 定东风 局：一将4（东南四北）圈，牌局开始掷骰子定东风，5、9自已为东，2、6、10下家为东，3、7、11对家为东，4、8、12上家为东。
	 * 
	 * @return
	 */
	public static int getDongFeng() {
		int result = (int) (Math.random() * 11 + 2);
		return result;
	}

	/**
	 * 得到百搭的那张牌
	 * 
	 * @return
	 */
	public static int getBaiDa() {
		int result = (int) (Math.random() * 136);
		return result;
	}

	
	public static void main(String[] args) {
		for(int i=0;i<=135;i++){
			String cardType = InitialPuKe.getCardType(i);
			int nextCard = getNextCard(i);
			String nextCardType = InitialPuKe.getCardType(nextCard);
			System.out.println(cardType+" "+nextCardType);
		}
	}
	
	
	/**
	 * 得到该张牌的下一张牌
	 * 
	 * @param card
	 * @return
	 */
	public static int getNextCard(int card) {
		int cardNumber = card / 4;
		int baiDaNumber = 0;
		if (cardNumber < 9) {// 万
			if (cardNumber == 8) {
				baiDaNumber = 0 * 4;
			} else {
				baiDaNumber = (cardNumber + 1) * 4;
			}
		} else if (cardNumber < 18) {// 筒
			if (cardNumber == 17) {
				baiDaNumber = 9*4;
			} else {
				baiDaNumber = (cardNumber + 1) * 4;
			}
		} else if (cardNumber < 27) {// 条
			if (cardNumber == 26) {
				baiDaNumber = 18*4;
			} else {
				baiDaNumber = (cardNumber + 1) * 4;
			}
		} else if (cardNumber < 28) {// 中
			baiDaNumber = (cardNumber + 1) * 4;
		} else if (cardNumber < 29) {// 发
			baiDaNumber = (cardNumber + 1) * 4;
		} else if (cardNumber < 30) {// 白
			baiDaNumber = 27 * 4;
		} else if (cardNumber < 31) {// 东
			baiDaNumber = (cardNumber + 1) * 4;
		} else if (cardNumber < 32) {// 南
			baiDaNumber = (cardNumber + 1) * 4;
		} else if (cardNumber < 33) {// 西
			baiDaNumber = (cardNumber + 1) * 4;
		} else if (cardNumber < 34) {// 北
			baiDaNumber = 30 * 4;
		}
		return baiDaNumber;
	}

}
