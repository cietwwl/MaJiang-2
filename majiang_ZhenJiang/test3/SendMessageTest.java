import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.json.JSONObject;


public class SendMessageTest {

	public static void main(String[] args) {
//		login();
//		createRoom();
//		enterRoom();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("method", "readyGame");
		System.out.println(jsonObject.toString());
	}

	private static void enterRoom() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("method", "enterRoom");
		jsonObject.put("roomId", 10000);
		System.out.println(jsonObject.toString());
	}

	private static void createRoom() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("method", "createRoom");
		jsonObject.put("total", 8);
		jsonObject.put("zhama", 3);
		System.out.println(jsonObject.toString());
	}

	private static void login() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("method", "login");
		jsonObject.put("userName", "zhao");
		jsonObject.put("password", "123456");
		System.out.println(jsonObject.toString());
	}
	
}
