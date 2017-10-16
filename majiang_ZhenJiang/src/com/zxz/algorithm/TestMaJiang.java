package com.zxz.algorithm;

import com.zxz.utils.MathUtil2;

public class TestMaJiang {

	public static void main(String[] args) {
//		testErWei();
		int array[] = {3, 10, 11, 20, 24, 31, 33, 36, 40, 47, 67, 106, 110};
		int result = MathUtil2.binarySearch(110, array);
		System.out.println("手里红中的个数:"+findTotalHongZhong(array));
	}
	
	
	/**判断是否胡牌
	 * @param array
	 * @return
	 */
	public static boolean isWin(int array[]){
		boolean isWin = false;
		
		
		return isWin;
	}
	
	
	/**是否含有一个对子
	 * @param array
	 * @return
	 */
	public static boolean isHaveDuiZi(int array[]){
		boolean isHaveDuizi = false;
		for (int i = 0; i < array.length; i++) {
			String type = InitialPuKe.map.get(array[i]+"");//得到牌型
			String nextPukeType = InitialPuKe.map.get(array[i+1]+"");//下一张牌的类型
			String nextTwoType = InitialPuKe.map.get(array[i+1]+"");//下二张牌的类型
			if(type.equals(nextPukeType)&&!type.equals(nextTwoType)){//和第一张相等不和第二张相等
				return true;
			}
		}
		return isHaveDuizi;
	}
	
	
	/**检测红中的个数
	 * @param array
	 * @return
	 */
	public static int findTotalHongZhong(int array[]){
		int result = 0;
		for(int i=108;i<=111;i++){
			//System.out.println(i);
			if(MathUtil2.binarySearch(i, array)>=0){
				result ++;
			}
		}
		return result;
	}
	
	/**判断是否可以直接胡牌
	 * @param array
	 * @return
	 */
	public static boolean isDirectWin(int array[]){
		int totalHongZhong = findTotalHongZhong(array);
		if(totalHongZhong==4){
			return true;
		}else{
			return false;
		}
	}

	private static void testErWei() {
		Object[][] maJiang = new Object[4][9];
		for(int i=0;i<maJiang.length;i++){
			int j = 9; 
			for(int m=0;m<j;m++){
				if(i==0){//红中
					j=1;
					maJiang[i][m] = 0;
					break;
				}
				maJiang[i][m] = i+""+(m+1);
			}
		}
		
		for(int i=0;i<maJiang.length;i++){
			Object[] o = maJiang[i];
			for(int j=0;j<o.length;j++){
				System.out.print(maJiang[i][j]+"\t");
			}
			System.out.println();
		}
	}
}
