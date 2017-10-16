package com.zxz.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.zxz.utils.MathUtil;


public class TestHuPai {

	public static void main(String[] args) {
//		testErWei();
//		int array[] = {0, 4, 9, 21, 22, 23, 40, 41, 42 ,65,66, 67,102,103};
//		int array[] = {0, 1, 2, 4, 8, 24, 25,26,65 ,66, 67, 101,102,109};
//		int array[] = {4, 5};
//		showPai(array);
//		int result = MathUtil.binarySearch(110, array);
//		System.out.println("������еĸ���:"+findTotalHongZhong(array));
		//System.out.println(isHaveDuiZi(array));
//		boolean win = isWin(array);
//		if(win){
//			System.out.println("����");
//		}else{
//			System.out.println("δ����");
//		}
	}
	
	
	public static void showPai(int array[]){
		System.out.println("\t\t");
		for(int i=0;i<array.length;i++){
			System.out.print(InitialPuKe.map.get(array[i])+" ");
		}
		System.out.println();
	}
	
	
	/**�ж��Ƿ����
	 * @param array
	 * @return
	 */
	public static boolean isWin(int array[]){
		showPai(array);
		if(array.length==2){
			boolean winWithDouble = isWinWithDouble(array);
			if(winWithDouble){
				return true;
			}else{
				return false;
			}
		}
		List<Integer> duiziList = isHaveDuiZi(array);
		int size = duiziList.size();
		if(duiziList.size()>0){
			for(int i=0;i<size/2;i++){
				int dui1 = duiziList.get(i*2);
				int dui2 = duiziList.get(i*2+1);
				boolean isWin = checkWinWithDuizi2(array, dui1,dui2);
				System.out.println("������:"+InitialPuKe.map.get(dui1)+" "+InitialPuKe.map.get(dui2));
				if(isWin){
					System.out.println("++����:++");
					return isWin;
				}
				System.out.println("---------------------------------------------");
			}
		}
		return false;
	}

	/**�����Ӻ���
	 * @param array
	 * @return
	 */
	public static boolean isWinWithDouble(int array[]){
		if(array.length==2){
			if(array[0]==array[1]){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	/**
	 * @param array
	 * @param isWin
	 * @param duiziList
	 * @return
	 */
	private static boolean checkWinWithDuizi(int[] array,
			int dui1,int dui2) {
		boolean isWin = true;
		boolean isCheckOver = false;
		int begin = 0;
		while(!isCheckOver){
			if(array[begin]!=dui1&&array[begin]!=dui2){
				String currentPaiHao = InitialPuKe.map.get(array[begin]);
				String nextPaiHao = InitialPuKe.map.get(array[begin+1]);
				String nextTwoHao = InitialPuKe.map.get(array[begin+2]);
				if(currentPaiHao.equals(nextPaiHao)&&currentPaiHao.equals(nextTwoHao)){//�����Ƿ���һ����
					System.out.println("��:"+currentPaiHao);
					if(begin+2<array.length-1){
						begin=begin+2;
					}
				}else{//�Ƿ��Ǿ���
					int a1 = array[begin];
					int a2 = array[begin+1];
					int a3 = array[begin+2];
					boolean isOneSentence = isOneSentence(a1,a2,a3);
					if(isOneSentence){
						System.out.println("����:"+InitialPuKe.map.get(a1)+" "+InitialPuKe.map.get(a2)+" "+InitialPuKe.map.get(a3));
						begin=begin+2;
					}else{
						System.out.println("���Ǿ���:"+InitialPuKe.map.get(a1)+" "+InitialPuKe.map.get(a2)+" "+InitialPuKe.map.get(a3));
						isWin = false;//�Ȳ��ǿ�Ҳ���Ǿ��ӣ�������
						isCheckOver =  true;
					}
				}
				begin+=1;
			}else{
				begin+=2;
			}
			if(begin==array.length-2){
				isCheckOver = true;
			}
		}
		return isWin;
	}
	
	/**
	 * @param array
	 * @param isWin
	 * @param duiziList
	 * @return
	 * [0,  0, 1, 2, 4, 5, 6, 8, 12, 16, 66, 67, 101, 102]
		1�� 1��  1��  1�� 2�� 2�� 2�� 3��    4��   5��   8Ͳ     8Ͳ    8��      8�� 
	 */
	private static boolean checkWinWithDuizi2(int[] array,
			int dui1,int dui2) {
		boolean isWin = true;
		List<Integer> list = new ArrayList<Integer>();
		for(int i=0;i<array.length;i++){
			if(array[i]!=dui1&&array[i]!=dui2){
				list.add(array[i]);
			}
		}
		for(int i=0;i<list.size()-2;i=i+3){
			String type1 = InitialPuKe.map.get(list.get(i));
			String type2 = InitialPuKe.map.get(list.get(i+1));
			String type3 = InitialPuKe.map.get(list.get(i+2));
			if(type1.equals(type2)&&type1.equals(type3)){
				System.out.println("��:"+type1);
			}else if(isOneSentence(list.get(i),list.get(i+1),list.get(i+2))){
				System.out.println("����:"+InitialPuKe.map.get(list.get(i))+" "+InitialPuKe.map.get(list.get(i+1))+" "+InitialPuKe.map.get(list.get(i+2)));
			}else{
				System.out.println("���Ǿ���:"+InitialPuKe.map.get(list.get(i))+" "+InitialPuKe.map.get(list.get(i+1))+" "+InitialPuKe.map.get(list.get(i+2)));
				isWin = false;//�Ȳ��ǿ�Ҳ���Ǿ��ӣ�������
				break;
			}
		}
		return isWin;
	}
	
	/**�����ǲ���һ������
	 * @param a1
	 * @param a2
	 * @param a3
	 * @return
	 */
	public static boolean isOneSentence(int a1,int a2,int a3){
		String typeA1 = getTypeString(a1);
		String typeA2 = getTypeString(a2);
		String typeA3 = getTypeString(a3);
		if(!typeA1.equals(typeA2)||!typeA1.equals(typeA3)||!typeA2.equals(typeA3)){
			return false;
		}
		int typeA1Int = getTypeInt(a1);
		int typeA2Int = getTypeInt(a2);
		int typeA3Int = getTypeInt(a3);
		if(typeA2Int-typeA1Int!=1){
			return false;
		}
		if(typeA3Int-typeA2Int!=1){
			return false;
		}
		
		return true;
	}

	/**�����ƺż�����Ƶ�����
	 * @param paiHao
	 * @return
	 */
	public static int getTypeInt(int paiHao) {
		int type = 0;
		if(paiHao>=0&&paiHao<=35){//��
			type=(paiHao/4)+1;
		}else if(paiHao>=36&&paiHao<=71){
			type=((paiHao/4)-9)+1;
		}else if(paiHao>=72&&paiHao<=107){
			type=((paiHao/4)-18)+1;
		}
		return type;
	}
	
	
	/**�����ƺż�����Ƶ�����
	 * @param paiHao
	 * @return
	 */
	public static String getTypeString(int paiHao) {
		String type = "";
		if(paiHao>=0&&paiHao<=35){//��
			type="��";
		}else if(paiHao>=36&&paiHao<=71){
			type="Ͳ";
		}else if(paiHao>=72&&paiHao<=107){
			type="��";
		}
		return type;
	}

	/**�����ƺż�����Ƶ�����
	 * @param paiHao
	 * @return
	 */
	public static String getTypeByPaiHao(int paiHao) {
		String type = "";
		if(paiHao>=0&&paiHao<=35){//��
			type=(paiHao/4)+1+"��";
		}else if(paiHao>=36&&paiHao<=71){
			type=((paiHao/4)-9)+1+"Ͳ";
		}else if(paiHao>=72&&paiHao<=107){
			type=((paiHao/4)-18)+1+"��";
		}
		return type;
	}
	
	
	/**�Ƿ���һ������
	 * @param array
	 * @return ���ӵ����� ��:1��
	 */
	public static List<Integer> isHaveDuiZi(int array[]){
		List<Integer> result = new ArrayList<Integer>();
		int resultArray[] = {-1,-1};
		for (int i = 0; i < array.length-1; i++) {
			String previousType = "";
			if(i>0){
				previousType = InitialPuKe.map.get(array[i-1]);//�õ�ǰһ�ŵ����� 
			}
			String type = InitialPuKe.map.get(array[i]);//�õ���������
			String nextPukeType = InitialPuKe.map.get(array[i+1]);//��һ���Ƶ�����
			String nextTwoType = "";
			if(i==array.length-2){
				nextTwoType = "";//�¶����Ƶ�����
			}else{
				nextTwoType = InitialPuKe.map.get(array[i+2]);//�¶����Ƶ�����
			}
			if(type.equals(nextPukeType)&&!type.equals(nextTwoType)&&!type.equals(previousType)){//�����ƺ���һ����Ȳ����¶�����Ȳ��Һ�ǰһ�Ų����
				result.add(array[i]);
				result.add(array[i+1]);

			//1�� 1�� 1�� 2�� 3�� 7�� 7�� 7�� 8Ͳ 8Ͳ 8Ͳ 8�� 8�� 8�� 
			}else if(type.equals(nextPukeType)&&type.equals(nextTwoType)){//�����ƺ͵�һ������Ⱥ͵�������Ҳ���
				boolean oneSentence = false;
				if(i+4<=array.length-1){//���һ�ŵĺ��������ܷ����һ�仰
					 oneSentence = isOneSentence(array[i+2],array[i+3],array[i+4]);
				}
				
				if(oneSentence){//�����������ƺͺ�������Ƿ���һ������
					result.add(array[i]);
					result.add(array[i+1]);

				}
			}
		}
		return result;
	}
	
	/**�Ƿ���һ������
	 * @param array
	 * @return ���ӵ����� ��:1��
	 */
	public static int[] isHaveDuiZi2(int array[]){
		boolean isFind  = false;
		int resultArray[] = {-1,-1};
		int begin = 0;
		
		return resultArray;
	}
	
	
	/**�����еĸ���
	 * @param array
	 * @return
	 */
	public static int findTotalHongZhong(int array[]){
		int result = 0;
		for(int i=108;i<=111;i++){
			//System.out.println(i);
			if(MathUtil.binarySearch(i, array)>=0){
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
