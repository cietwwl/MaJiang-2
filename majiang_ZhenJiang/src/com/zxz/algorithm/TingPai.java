package com.zxz.algorithm;

import java.util.ArrayList;



public class TingPai {
	
	
	public static void main(String[] args) {
//		int array[] = {0,1,4,9};
		int array[] = {0, 2, 4, 5, 6, 8,12,65,66,67, 101,102,103};
		System.out.println(toString(array));
		for(int i=0;i<=108;i=i+4){
			System.out.println("-----------------------------------------测试牌是:-------------------------------------------"+InitialPuKe.map.get(i)+" "+i);
			int[] insertSortArray = insertSortArray(array, i);
			System.out.println(toString(insertSortArray));
			boolean win = TestHuPai.isWin(insertSortArray);
			if(win){
				System.out.println(win==true?"-------------------------胡牌-----------------------------------":"");
			}
		}
	}
	
	public ArrayList<Integer> getTingPaiArray(int array[],int a){
		
		
		return null;
	}
	
	/**向一个数组中，添加一个数，并且使它仍然有序
	 * @param array
	 * @param number
	 * @return 
	 */
	public static int[] insertSortArray(int array[],int number){
		int begin = 0;
		int end = array.length;
		boolean isEnd = false;
		int index = -1;
		int []copyArray = new int[array.length+1];
		if(number>=array[array.length-1]){
			index = array.length;
		}else if(number<=array[0]){
			index = 0;
		}else{
			while(!isEnd){
				int middle = (begin+end)/2;
				int temp = array[middle];
				if(temp == number){
					index = middle;
					isEnd = true;
				}else if(number>array[middle]&&number<=array[middle+1]){
					index  = middle +1;
					isEnd = true;
				}else if(number<array[middle]&&number>=array[middle-1]){
					index  = middle;
					isEnd = true;
				}else if(number>array[middle]){
					begin = middle;
				}else if(number<array[middle]){
					end = middle;
				}
				if(begin>=end){
					isEnd = true;
				}
			}
		}
		
		if(index==array.length){
			for(int i=0;i<array.length;i++){
				copyArray[i] = array[i];
			}
			copyArray[array.length]=number;
		}else if(index==0){
			copyArray[0]=number;
			for(int i=0;i<array.length;i++){
				copyArray[i+1] = array[i];
			}
		}else{
			for(int i=0;i<=index-1;i++){
				copyArray[i] = array[i];
			}
			
			copyArray[index] = number;
			copyArray[index+1] = array[index];
			for(int i=index+1;i<array.length;i++){
				copyArray[i+1] = array[i];
			}
		}
		
		
		return copyArray;
	}
	
	 public static String toString(int[] a) {
	        if (a == null)
	            return "null";
	        int iMax = a.length - 1;
	        if (iMax == -1)
	            return "[]";

	        StringBuilder b = new StringBuilder();
	        b.append('[');
	        for (int i = 0; ; i++) {
	            b.append(a[i]);
	            if (i == iMax)
	                return b.append(']').toString();
	            b.append(", ");
	        }
	    }
}
