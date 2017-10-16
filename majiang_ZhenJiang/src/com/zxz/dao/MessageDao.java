package com.zxz.dao;

import java.util.List;
import java.util.Map;

import com.zxz.dao.BaseDao;
import com.zxz.domain.Message;

public class MessageDao extends BaseDao<Message> {

	public List<Message> selectListByMap(Map<String, Object> map) {
		return super.queryForList("Message.selectListByMap", map);
	}

	public int selectListByMapTotal(Map<String, Object> map) {
		return (int) super.queryForObject("Message.selectListByMapTotal", map);
	}

}
