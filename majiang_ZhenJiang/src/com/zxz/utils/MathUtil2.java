package com.zxz.utils;

public class MathUtil2 {

	public static void main(String[] args) {
//		int array[] = {3, 10, 11, 20, 24, 31, 33, 36, 40, 47, 67, 106, 110};
//		binarySearch(111, array);
		System.out.println(11);
	}
	
	public static int binarySearch(int number,int array[]){
		int middle = array.length/2;
		int begin = 0;
		int end = array.length;
		while(begin<=end&&middle<array.length&&middle>0){
			int temp = array[middle];
			if(temp==number){
				//System.out.println("ÕÒµ½Ë÷ÒıÊÇ£º"+middle);
				return middle;
			}else if(temp>number){
				end = middle-1;
			}else if(temp<number){
				begin = middle + 1;
			}
			middle = (begin+end)/2;
		}
		return -1;
	}
}
