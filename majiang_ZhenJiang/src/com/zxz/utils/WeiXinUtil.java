package com.zxz.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class WeiXinUtil {

	private static Logger logger = Logger.getLogger(WeiXinUtil.class);  
	
//	static final String appid = "wx2ffdd6142f15121d";//麻将测试
//	static final String appsecret = "c931b873b47992135fc646e6b95ce215";//麻将测试
	
	
	static final String appid = "wxab1ca7920364b5a2";//镇江
	static final String appsecret = "e65f318a3bda8bddeb8ebf0d22019d81";//镇江
	
	
	static Map<String,Map<String, Object>> openId_access_tokenMap_web = null;
	
	
	public static void main(String[] args) {
//		WeiXinUtil util = new WeiXinUtil();
//		JSONObject weinxinOpenIdJson = WeiXinUtil.getAccessTokenJson("041CEQ500MTHHB1jn5600ySR500CEQ5w");
//		String openid = weinxinOpenIdJson.getString("openid");
//		System.out.println("openid:"+openid);
//		String access_token = weinxinOpenIdJson.getString("access_token");
//		System.out.println("access_token:"+access_token);
//		JSONObject userInfo = getUserInfo(access_token, openid);
//		System.out.println("userInfo:"+userInfo);
		String accessToken = WeiXinUtil.getAccessTokenWithRefreshToken("Ajc0EXxE_28PebMcoqzyRHWmtSNlAmCLTskDHVzpWaPZJttSe6Si2nB5LyHVX2uSzfCTNAMqh7qNhOEPDgBGrz1W4O_zq6zkQ-1i2ZfkXEo");
		System.out.println(accessToken);
		JSONObject userInfo = getUserInfo(accessToken, "obhqFxLZ4us8x2300oPIf5fVFN3M");
		System.out.println(userInfo);
	}
	
	
	/**根据refreshToken获取access_token
	 * @param refreshToken
	 * @return
	 */
	public static String getAccessTokenWithRefreshToken(String refreshToken){
		String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="+appid+"&grant_type=refresh_token&refresh_token="+refreshToken;
		String returnStr = sendPost(url, null);
		JSONObject outJsonObject = new JSONObject(returnStr);
		if(outJsonObject.has("errcode")){
			throw new IllegalArgumentException(outJsonObject.toString());
		}
		return outJsonObject.getString("access_token");
	}
	
	/**
	 * 获取openid
	 * @param code
	 * @return
	 */
	public static JSONObject getAccessTokenJson(String code){
		String returnStr = "";
		if(code!=null&&!code.trim().equals("")){
			String getopenId_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+appsecret+"&code="+code+"&grant_type=authorization_code";
//			returnStr = httpRequest(getopenId_url, "POST", null);
			returnStr = sendPost(getopenId_url, null);
		}
		logger.info("微信授权返回:"+returnStr);
		JSONObject responseJson = new JSONObject(returnStr);
		if(responseJson.has("errcode")){
			logger.fatal("微信授权:"+code);
			throw new IllegalArgumentException(responseJson.getString("errmsg"));
		}
		return responseJson;
	}
	
	
	/**检验授权凭证是否有效
	 * @param openid
	 * @return
	 */
	public boolean isAccessTokenEffective(String openid){
		String url = "https://api.weixin.qq.com/sns/auth?access_token=ACCESS_TOKEN&openid="+openid;
//		String returnStr = httpRequest(url, "POST", null);
		String returnStr = sendPost(url, null);
		logger.info("检验授权凭证（access_token）是否有效:"+returnStr);
		JSONObject jsonObject = new JSONObject(returnStr);
		int errorcode = jsonObject.getInt("errcode");
		if(errorcode==0){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 得到用户的信息
	 */
	public static JSONObject getUserInfo(String accesstoekn,String openid){
		String url = "https://api.weixin.qq.com/sns/userinfo?access_token="+accesstoekn+"&openid="+openid;
		//System.out.println("url:"+url);
		String returnStr = httpRequest(url, "POST", null);
//		String returnStr = sendPost(url,  null);
		//System.out.println("获取到的用户信息"+returnStr);
		JSONObject jsonObject = new JSONObject(returnStr);
		if(jsonObject.has("errcode")){
			logger.fatal("获取用户信息的时候出错:"+jsonObject.getString("errcode"));
			throw new IllegalArgumentException(jsonObject.getString("errcode"));
		}
		return jsonObject;
	}
	
	
	 public static String sendPost(String url, Map<String,String> params) {
	        PrintWriter out = null;
	        BufferedReader in = null;
	        String result = "";
	        try {
//	        	param = "method=北京";
	            URL realUrl = new URL(url);
	            StringBuffer paramBuffer = new StringBuffer();
	            if(params!=null&&params.size()>0){
	            	Iterator<Entry<String, String>> it = params.entrySet().iterator();
	            	int count = 0;
	            	while(it.hasNext()){
	            		Entry<String, String> ent = it.next();
	            		if(ent.getKey()!=null&&ent.getValue()!=null){
	            			if(count!=0){
		            			paramBuffer.append("&"+ent.getKey()+"="+URLEncoder.encode(ent.getValue(), "utf-8"));
		            		}else{
		            			paramBuffer.append(ent.getKey()+"="+URLEncoder.encode(ent.getValue(), "utf-8"));
		            		}
		            		count++;
	            		}
	            	}
	            }
	            // 打开和URL之间的连接
	            URLConnection conn = realUrl.openConnection();
	            // 设置通用的请求属性
	            conn.setRequestProperty("Accept-Charset", "utf-8");
	            conn.setRequestProperty("contentType", "utf-8");
	            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	            conn.setRequestProperty("accept", "*/*");
	            conn.setRequestProperty("connection", "Keep-Alive");
	            conn.setRequestProperty("user-agent",
	                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	            // 发送POST请求必须设置如下两行
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            // 获取URLConnection对象对应的输出流
	            out = new PrintWriter(conn.getOutputStream());
	            // 发送请求参数
	            out.print(paramBuffer.toString());
	            // flush输出流的缓冲
	            out.flush();
	            // 定义BufferedReader输入流来读取URL的响应
	            in = new BufferedReader(
	                    new InputStreamReader(conn.getInputStream()));
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	            }
	        } catch (Exception e) {
	            System.out.println("发送 POST 请求出现异常！"+e);
	            e.printStackTrace();
	        }
	        //使用finally块来关闭输出流、输入流
	        finally{
	            try{
	                if(out!=null){
	                    out.close();
	                }
	                if(in!=null){
	                    in.close();
	                }
	            }
	            catch(IOException ex){
	                ex.printStackTrace();
	            }
	        }
	        return result;
	    }   

	
	
	/** 
	 * 发起https请求并获取结果 
	 * @param requestUrl 请求地址 
	 * @param requestMethod 请求方式（GET、POST） 
	 * @param outputStr 提交的数据 
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值) 
	 */
	private static String httpRequest(String requestUrl, String requestMethod, String xml) {
		String resultStr = "";
		StringBuffer buffer = new StringBuffer();
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化  
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象  
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			URL url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			httpUrlConn.setRequestMethod(requestMethod);
			httpUrlConn.connect();
			if(xml!=null){
				OutputStream os = httpUrlConn.getOutputStream();
				os.write(xml.toString().getBytes("utf-8"));
				os.close();
			}
			// 将返回的输入流转换成字符串  
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源  
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			resultStr = buffer.toString();
		} catch (ConnectException ce) {
		} catch (Exception e) {
		}
		return resultStr;
	}
	
}
