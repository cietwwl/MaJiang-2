package com.zxz.utils;

public interface ScoreType {

	/**
	 * 杠得分
	 */
	public static final int ADD_SCORE_FOR_GANG = 3;
	
	/**
	 * 放杠减分
	 */
	public static final int REDUCE_SCORE_FOR_GANG = -3;
	
	/**
	 * 明杠加分
	 */
	public static final int ADD_SCORE_FOR_MINGGANG = 3;
	
	/**
	 * 明杠加分基础分
	 */
	public static final int ADD_SCORE_FOR_MINGGANG_BASE = 1;
	
	/**
	 * 明杠减分
	 */
	public static final int REDUCE_SCORE_FOR_MINGGANG = -1;
	
	/**
	 * 胡牌得分
	 */
	public static final int ADD_SCORE_FOR_HUPAI = 6;
	
	/**
	 * 胡牌得分基础分
	 */
	public static final int ADD_SCORE_FOR_HUPAI_BASE = 2;
	
	/**
	 * 暗杠得分
	 */
	public static final int ADD_SCORE_FOR_ANGANG = 6;
	
	/**
	 * 暗杠得分基础分
	 */
	public static final int ADD_SCORE_FOR_ANGANG_BASE = 2;
	
	/**
	 * 胡牌减分
	 */
	public static final int REDUCE_SCORE_FOR_HUPAI = -2;
	
	
	/**
	 * 暗杠减分
	 */
	public static final int REDUCE_SCORE_FOR_ANGANG = -2;
	
	/**
	 * 首搭减分
	 */
	public static final int REDUCE_SCORE_FOR_SHOUDA = -6;
	
	/**
	 * 二搭减分
	 */
	public static final int REDUCE_SCORE_FOR_ERDA = -4;
	
	/**
	 * 三搭减分
	 */
	public static final int REDUCE_SCORE_FOR_SANDA = -2;
	
	
	/**
	 * 杠开
	 */
	public static final int REDUCE_SCORE_FOR_GANGKAI = -1;
	
	
	/**
	 * 无搭
	 */
	public static final int REDUCE_SCORE_FOR_WUDA = -6;
	
	/**
	 * 有搭
	 */
	public static final int REDUCE_SCORE_FOR_YOUDA = -3;
	
	/**
	 * 跑搭
	 */
	public static final int REDUCE_SCORE_FOR_PAODA = -5;
	
	
	/**
	 * 跑搭脱搭
	 */
	public static final int REDUCE_SCORE_FOR_PAODA_TUODA = -8;
	
}
