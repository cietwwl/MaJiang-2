package com.zxz.algorithm;

import java.util.Arrays;

public class MySort {
	
	public static void main(String[] args) {
		Object [] objs = {23,23,90,-9};
		int[] sort = sort(objs);
		showArray(sort);
	}
	
	public static int[] sort(Object [] objs) {
		int[] aArray = new int[objs.length];
		for(int i=0;i<objs.length;i++){
			aArray[i] = Integer.parseInt(objs[i]+"");
		}
		return aArray;
	}
	
	
	public static void showArray(int[] sort){
		for(int i=0;i<sort.length;i++){
			System.out.println(sort[i]);
		}
	}
	
}
