package com.zxz.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordScoreThreadPool {

	private static final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
	RecordScoreThreadPool pool = new RecordScoreThreadPool();
	
	
    private RecordScoreThreadPool() {  
    }  


    public static ExecutorService getExecutorService() {  
        return fixedThreadPool;  
    }  
	
}
