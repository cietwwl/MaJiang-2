package com.zxz.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Initial {

	public static void main(String[] args) {
		HashMap<String, String> maptest = new HashMap<String, String>();
		maptest.put("1天", "day1");
		maptest.put("5天", "day5");
		maptest.put("4天", "day4");
		maptest.put("2天", "day2");
		maptest.put("3天", "day3");

		Collection<String> keyset = maptest.keySet();
		List<String> list = new ArrayList<String>(keyset);

		// 对key键值按字典升序排序
		Collections.sort(list);

		for (int i = 0; i < list.size(); i++) {
			System.out.println("key键---值: " + list.get(i) + ","
					+ maptest.get(list.get(i)));
		}
	}
}
