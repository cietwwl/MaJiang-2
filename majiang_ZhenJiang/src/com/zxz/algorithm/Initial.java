package com.zxz.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Initial {

	public static void main(String[] args) {
		HashMap<String, String> maptest = new HashMap<String, String>();
		maptest.put("1��", "day1");
		maptest.put("5��", "day5");
		maptest.put("4��", "day4");
		maptest.put("2��", "day2");
		maptest.put("3��", "day3");

		Collection<String> keyset = maptest.keySet();
		List<String> list = new ArrayList<String>(keyset);

		// ��key��ֵ���ֵ���������
		Collections.sort(list);

		for (int i = 0; i < list.size(); i++) {
			System.out.println("key��---ֵ: " + list.get(i) + ","
					+ maptest.get(list.get(i)));
		}
	}
}
