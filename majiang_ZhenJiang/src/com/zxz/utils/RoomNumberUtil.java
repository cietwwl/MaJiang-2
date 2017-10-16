package com.zxz.utils;

import com.zxz.config.utils.Config;

public class RoomNumberUtil {

	private static Config config = Config.getConfig();
	
	/**�õ�һ�������
	 * @return
	 */
	public synchronized static int getOneRoomNumber(){
		int interval = config.getInterval();
		config.setInterval(interval+1);
		return interval;
	}
	
}
