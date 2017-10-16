package com.zxz.utils;

public interface Constant {

	public static final String method="method";//������
	public static final String code ="code";
	public static final String success ="success";
	public static final String error = "error";//����
	public static final String discription = "discription";//����
	public static final String direction = "dirction";//����
	public static final String type = "type";//����
	public static final int GONG_GANG = 0;//���Թ���
	public static final int AN_GANG = 1;//���԰��� 
	
	/**
	 * ���Ƶ�״̬
	 */
	public static final int GAGME_STATUS_OF_CHUPAI = 0;//���Ƶ�״̬
	
	/**
	 * ���Ƶ�״̬
	 */
	public static final int GAGME_STATUS_OF_PENGPAI = 1;//���Ƶ�״̬
	
	/**
	 * ���Ƶ�״̬ (�Ӹ�)
	 */
	public static final int GAGME_STATUS_OF_GANGPAI = 2;//��ͨ����
	
	/**
	 * ���ܵ�״̬ 
	 */
	public static final int GAGME_STATUS_OF_ANGANG = 3;//����
	
	/**
	 * ���ܵ�״̬ 
	 */
	public static final int GAGME_STATUS_OF_GONG_GANG = 4;//����
	
	
	/**
	 * �ȴ����Ƶ�״̬ 
	 */
	public static final int GAGME_STATUS_OF_WAIT_HU = 5;//�ȴ�����
	
	/**
	 * �ȴ����Ƶ�״̬ 
	 */
	public static final int GAGME_STATUS_OF_WAIT_HU_NEW = 6;//�ȴ�����
	
	/**
	 * �û����ɺ��ֿ�������ʱ��ѡ��������
	 */
	public static final int GAGME_STATUS_OF_WAIT_HU_NEW_WAIT_PENG = 7;//�ȴ������ֿ�����
	
	
	/**
	 * �û����ɺ��ֿ��Ըܵ�ʱ��ѡ���˸���
	 */
	public static final int GAGME_STATUS_OF_WAIT_HU_NEW_WAIT_GANG = 8;//�ȴ������ֿ��Ը�
	
	/**
	 * �û����ɺ��ֿ��Ըܵ�ʱ��ѡ���˸���
	 */
	public static final int GAGME_STATUS_OF_WAIT_HU_NEW_WAIT_QIANG_GANG = 9;//���ܱ��˿��Ժ���
	
	/**
	 * ��Ϸ�ȴ���ʼ
	 */
	public static final int GAGME_STATUS_OF_WAIT_START = 0;
	
	/**
	 * ��Ϸ������
	 */
	public static final int GAGME_STATUS_OF_IS_GAMING = 1;
	
	/**
	 * ��Ϸ�Ƿ���� 
	 */
	public static final String GAME_END = "GAME_END";//��Ϸ�Ƿ����
	
	
	/**
	 * �û�ע���ʱ��Ĭ�ϵķ�������
	 */
	public static final int DEFAULT_USER_REGIST_ROOMCARD = 50;
	
	/**
	 * ��Ϸ��������ʱ
	 */
	public static final int TIME_TO_START_GAME = 30000;//2����
	
	
	/**
	 * �û��Զ���ʼ���������ʱ��
	 */
	public static final int AUTO_USER_TIME_TO_START_GAME = 10000;
	
	
	/**
	 * ���Ե���Ϸ����  1000
	 */
	public static final int TEST_TOTAL_GAME = 1000;
	
	/**
	 * �û�Ӯ�Ƶ�ʱ��ȴ�1000����
	 */
	public static final int TIME_WAIT_WIN = 100;
	
	/**
	 * ֪ͨ�û����Ƶ�ʱ��ȴ�500����
	 */
	public static final int TIME_WAIT_CHUPAI = 100;
	
	
	/**
	 * ���䴴��10���ӻ�û�п�ʼ�����Զ���ɢ����
	 */
	public static final int TIME_TO_DISBAND_ROOM = 60000*10*3;//30����
	
	
	
	/**
	 * �����淨
	 */
	public static final int playTypeOfHongZhong = 0;
	
	/**
	 * Ѫս�淨
	 */
	public static final int playTypeOfXueZhan = 1;
	
	
	/**
	 * �ȴ�����
	 */
	public static final int TIME_TO_WAIT_ZHAMA = 1000*7;//�ȴ�����
	
	
	
	/**
	 * redis�м�¼�û������ʱ��
	 */
	public static final int TIME_TO_USER_ROOM = 60*60*5;
	
	
	
	public static final int REDIS_DB = 1;
	
	
	/**
	 * ���� (���� �� ���)
	 */
	public static final int JIN_CHONG = 2;
	
	
	
	public static final int PEI_CHONG = 1;
	
	
    /**
     * ����ʱ
     */
    public static final int READ_IDEL_TIME_OUT = 10; 
	
	 /**
     * д��ʱ
     */
    public static final int WRITE_IDEL_TIME_OUT = 10;
    
    /**
     * ���г�ʱ
     */
    public static final int ALL_IDEL_TIME_OUT = 10*30; 
	
}
