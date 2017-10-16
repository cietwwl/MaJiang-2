package com.zxz.protobuf;

import com.googlecode.protobuf.format.JsonFormat;
import com.zxz.protobuf.Response.responseStr;

public class TestResponse {

	public static void main(String[] args) {
		Response.responseStr.Builder builder = Response.responseStr.newBuilder();
		builder.setMethod("connection");
		builder.setDescription("建立连接成功");
		responseStr responseStr = builder.build();
		System.out.println(responseStr.toByteArray());
		String sresponse = JsonFormat.printToString(responseStr);
		System.out.println("文本是:"+sresponse);
	}
	
}
