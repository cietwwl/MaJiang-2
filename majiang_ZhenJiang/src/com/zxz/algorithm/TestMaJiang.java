package com.zxz.algorithm;

import com.zxz.utils.MathUtil2;

public class TestMaJiang {

	public static void main(String[] args) {
//		testErWei();
		int array[] = {3, 10, 11, 20, 24, 31, 33, 36, 40, 47, 67, 106, 110};
		int result = MathUtil2.binarySearch(110, array);
		System.out.println("������еĸ���:"+findTotalHongZhong(array));
	}
	
	
	/**�ж��Ƿ����
	 * @param array
	 * @return
	 */
	public static boolean isWin(int array[]){
		boolean isWin = false;
		
		
		return isWin;
	}
	
	
	/**�Ƿ���һ������
	 * @param array
	 * @return
	 */
	public static boolean isHaveDuiZi(int array[]){
		boolean isHaveDuizi = false;
		for (int i = 0; i < array.length; i++) {
			String type = InitialPuKe.map.get(array[i]+"");//�õ�����
			String nextPukeType = InitialPuKe.map.get(array[i+1]+"");//��һ���Ƶ�����
			String nextTwoType = InitialPuKe.map.get(array[i+1]+"");//�¶����Ƶ�����
			if(type.equals(nextPukeType)&&!type.equals(nextTwoType)){//�͵�һ����Ȳ��͵ڶ������
				return true;
			}
		}
		return isHaveDuizi;
	}
	
	
	/**�����еĸ���
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
	
	/**�ж��Ƿ����ֱ�Ӻ���
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
				if(i==0){//����
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
