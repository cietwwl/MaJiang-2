package com.zxz.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	/**�õ�ָ����ʽ��ʱ��
	 * @param date
	 * @param pattern  yyyy/MM/dd hh:mm:ss
	 * @return
	 */
	public static String getFormatDate(Date date,String pattern) {
		DateFormat sdf2 = new SimpleDateFormat(pattern);		
		String sdate = sdf2.format(date);
		return sdate;
	}
	
	
	/**�õ���ǰ��ʱ��
	 * @return
	 */
	public static String getCurrentDate(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss:SSS");
		String formatStr =formatter.format(new Date());
		return formatStr;
	}
}
