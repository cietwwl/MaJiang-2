package jiekoubianxie;

import org.json.JSONObject;

public class JsonTest {

	public static void main(String[] args) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("huUser", "east");
		outJsonObject.put("discription", "该用户可以胡牌");
		outJsonObject.put("method", "canHu");//可以杠
		outJsonObject.put("type", 1);//
		System.out.println(outJsonObject);
	}
	
}
