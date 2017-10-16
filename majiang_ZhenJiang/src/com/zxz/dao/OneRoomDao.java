package com.zxz.dao;

import java.util.HashMap;

import org.junit.Test;

import com.zxz.algorithm.Room;
import com.zxz.domain.OneRoom;


public class OneRoomDao extends BaseDao{
	
	
	public static int roomNumber = 10000;
	static OneRoomDao oneRoomDao;
	
	private OneRoomDao() {
	}
	
	public static OneRoomDao getInstance(){
		if(oneRoomDao!=null){
			return oneRoomDao;
		}else{
			synchronized (OneRoomDao.class) {
				oneRoomDao = new OneRoomDao();
				return oneRoomDao;
			}
		}
	}
	
	/**���һ������
	 * @param room
	 * @return
	 */
	public int saveRoom(OneRoom room){
//		insert("OneRoom.save", room);
//		return room.getId();
		return (int)super.queryForObject("OneRoom.createOneRoom2",room);
	}
	
	
	public static void main(String[] args) {
//		OneRoom oneRoom = new OneRoom();
//		oneRoom.setTotal(8);
//		oneRoom.setZhama(3);
//		OneRoomDao oneRoomDao = new OneRoomDao();
//		oneRoomDao.saveRoom(oneRoom);
		OneRoomDao oneRoomDao = new OneRoomDao();
		HashMap<String, Object> map = new HashMap<>();
		map.put("roomId", 100589);//�����
		map.put("userId", 19);//�û�id
		map.put("totalGame", 16);//�ܾ���
		map.put("type", 1);//���� 1����ֵ0,��Ա��ֵ 3
		map.put("total",0);//��ֵ�ķ�������
		int userCard = oneRoomDao.userConsumeCard(map);
		System.out.println("��ʣ:"+userCard);
	}
	
	
	/**�û����ѷ��� 
	 * @param map
	 * @return
	 */
	public int userConsumeCard(HashMap<String, Object> map){
		return (int)super.queryForObject("OneRoom.userConsumeRoomCard", map);
	}
	
	@Test
	public void testConsumeCard(){
		HashMap<String, Object> map = new HashMap<>();
		map.put("roomId", 100589);//�����
		map.put("userId", 19);//�û�id
		map.put("totalGame", 16);//�ܾ���
		map.put("type", 1);//���� 1����ֵ0,��Ա��ֵ 3
		map.put("total",0);//��ֵ�ķ�������
		int userConsumeCard = userConsumeCard(map);
		System.out.println("ʣ�෿����:"+userConsumeCard);
	}
	@Test
	public void testSave(){
		OneRoom oneRoom = new OneRoom();
		oneRoom.setTotal(8);
		oneRoom.setZhama(3);
		OneRoomDao oneRoomDao = new OneRoomDao();
		oneRoomDao.saveRoom(oneRoom);
	}
	
}
