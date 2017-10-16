package com.zxz.algorithm;


public class PengPai {

	public static void main(String[] args) {
		Integer[] array = {0, 1, 2, 4, 5, 6, 8,12,16,66, 67, 101,105};
		System.out.println(toString(array));
		showPai(array);
		pengPaiOrGangPai(array, 7);
	}
	
	private static String toString(Integer[] a) {
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

	public static int pengPaiOrGangPai(Integer[] array,int number){
		String type = InitialPuKe.map.get(number);
		boolean isEnd = false;
		int step = 0;
		while(!isEnd){
			String currentType = InitialPuKe.map.get(array[step]);
			String nextType = InitialPuKe.map.get(array[step+1]);
			String nextTwoType = InitialPuKe.map.get(array[step+2]);
			if(!type.equals(currentType)){
				step ++;
			}else if(type.equals(currentType)&&type.equals(nextType)&&type.equals(nextTwoType)){//可以杠
				System.out.println("可以杠:"+type);
				return 2;
			}else if(type.equals(currentType)&&type.equals(nextType)){//可以碰
				System.out.println("可以碰:"+type);
				return 1;
			}else{
				step ++;
			}
			if(step==array.length-2){
				//System.out.println("不可以杠和碰");
				isEnd = true;
			}
		}
		return 0;
	}
	
	public static void showPai(Integer[] array){
		for(int i=0;i<array.length;i++){
			System.out.print(InitialPuKe.map.get(array[i])+" ");
		}
		System.out.println();
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
