import com.zxz.redis.RedisUtil;

public class DeleteRedis {

	public static void main(String[] args) {
		int array[] = {10020,10021,10023,10027,91438};
		System.out.println(RedisUtil.delKey("zjRoomId"+202199,1));;
		for(int i=0;i<array.length;i++){
			Long delKey = RedisUtil.delKey("usRoomId"+array[i], 1);
			System.out.println(delKey);
		}
		
		
//		RedisUtil.setKey("usRoomId"+10021, 202200+"", 1);
		
	}
	
	
}
