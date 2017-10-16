package com.zxz.dao;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class SqlSessionFactoryBuilderManager {

	static SqlSessionFactory ssf;
	
	private SqlSessionFactoryBuilderManager() {
	}
	
	public static SqlSessionFactory getSqlSessionFactory(){
		if(ssf==null){
			synchronized (SqlSessionFactory.class) {
				SqlSessionFactoryBuilder ssfb= new SqlSessionFactoryBuilder();
				ssf= ssfb.build(
						SqlSessionFactoryBuilderManager.class.getClassLoader().
						getResourceAsStream("config/SqlMapConfig.xml"));
			}
		}
		return ssf;
	}
	
	public static void main(String[] args) {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSessionFactory sqlSessionFactory2 = getSqlSessionFactory();
		System.out.println(sqlSessionFactory==sqlSessionFactory2);
	}
}
