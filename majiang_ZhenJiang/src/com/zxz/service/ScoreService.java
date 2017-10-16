package com.zxz.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zxz.dao.SumScoreDao;
import com.zxz.dao.UserScoreDao;
import com.zxz.domain.SumScore;
import com.zxz.domain.User;
import com.zxz.domain.UserScore;
import com.zxz.utils.Constant;
import com.zxz.utils.DateUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class ScoreService implements Constant{
	SumScoreDao sumScoreDao = SumScoreDao.getInstance();
	UserScoreDao userScoreDao = UserScoreDao.getInstance();//���ֽ���ɼ�
	
	
	/**�õ��û���ս��
	 * @param jsonObject
	 * @param session
	 */
	public void getUserScore(JSONObject jsonObject, ChannelHandlerContext session) {
		JSONObject outJsonObject = getEveryScoreJsonObject(jsonObject, session);
		session.write(outJsonObject.toString());
	}

	
	/**�õ��û��ܵĳɼ�
	 * @param jsonObject
	 * @param session
	 */
	public void getUserSumScore(JSONObject jsonObject, ChannelHandlerContext session) {
		JSONObject sumScoreJsonObject = getSumScoreJsonObject(jsonObject, session);
		System.out.println(sumScoreJsonObject);
		session.write(sumScoreJsonObject);
	}
	
	
	
	/**����ҳ�����õ�ÿһ����ϷjsonObject 
	 * @param jsonObject
	 * @param session
	 * @return
	 */
	private JSONObject getSumScoreJsonObject(JSONObject jsonObject, ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		int index = jsonObject.getInt("index");
		int pageSzie = 3;
		Map<String, Object> map = new HashMap<>();
		map.put("userid", user.getId());
		map.put("pageIndex", (index-1)*pageSzie);
		map.put("pageSize", pageSzie);
		List<Map<String, Object>> userScoreList = userScoreDao.findUserSumScore(map);
		JSONObject outJsonObject = new JSONObject();
		JSONArray scoreArray = new JSONArray();
		for(int i=0;i<userScoreList.size();i++){
			Map<String, Object> oneMap = userScoreList.get(i);
			int userid = Integer.parseInt(oneMap.get("userid")+"");
			int finalScore = Integer.parseInt(oneMap.get("finalScore")+"");
			int roomNumber = Integer.parseInt(oneMap.get("roomNumber")+"");
			String createDate = oneMap.get("createDate")+"";
			String nickName = oneMap.get("nickName")+"";
			int total = Integer.parseInt(oneMap.get("total")+"");
			createDate = createDate.substring(0, createDate.length()-2);
			JSONObject everyScore = new JSONObject();
			everyScore.put("userid", userid);//�û�id
			everyScore.put("finalScore", finalScore);//�ܳɼ�
			everyScore.put("roomNumber", roomNumber);//����
			everyScore.put("createDate", createDate);//����ʱ��
			everyScore.put("nickName", nickName);//�ǳ�
			everyScore.put("total", total);//����
			scoreArray.put(everyScore);
		}
		JSONArray personArray = new JSONArray();
		for(int i=0;i<scoreArray.length();){
			JSONObject object = new JSONObject();
			JSONObject baseScore = scoreArray.getJSONObject(i);
			object.put("total", baseScore.get("total"));
			object.put("roomNumber", baseScore.get("roomNumber"));
			object.put("createDate", baseScore.get("createDate"));
			JSONArray oneGame = new JSONArray();
			if(i>scoreArray.length()){
				break;
			}
			if(i+1<scoreArray.length()){
				JSONObject p1 = scoreArray.getJSONObject(i);
				removeJsonkey(p1);
				oneGame.put(p1);
			}else{
				break;
			}
			if(i+1<scoreArray.length()){
				JSONObject p2 = scoreArray.getJSONObject(i+1);
				removeJsonkey(p2);
				oneGame.put(p2);
			}else{
				break;
			}
			if(i+1<scoreArray.length()){
				JSONObject p3 = scoreArray.getJSONObject(i+2);
				removeJsonkey(p3);
				oneGame.put(p3);
			}else{
				break;
			}
			if(i+1<scoreArray.length()){
				JSONObject p4 = scoreArray.getJSONObject(i+3);
				removeJsonkey(p4);
				oneGame.put(p4);
			}else{
				break;
			}
			object.put("oneGame", oneGame);
			personArray.put(object);
			i=i+4;
		}
		outJsonObject.put("userScores", personArray);
		outJsonObject.put(method, "getUserSumScore");
		outJsonObject.put(discription, "�õ��û��ܽ����ս��");
		outJsonObject.put("type", "sumScore");
		return outJsonObject;
	}


	private void removeJsonkey(JSONObject p1) {
		p1.remove("total");
		p1.remove("roomNumber");
		p1.remove("createDate");
	}
	
	
	

	/**����ҳ�����õ�ÿһ����ϷjsonObject 
	 * @param jsonObject
	 * @param session
	 * @return
	 */
	private JSONObject getEveryScoreJsonObject(JSONObject jsonObject, ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		int index = jsonObject.getInt("index");
		int pageSzie = 20;
		Map<String, Object> map = new HashMap<>();
		map.put("userid", user.getId());
		map.put("pageIndex", (index-1)*pageSzie);
		map.put("pageSize", pageSzie);
		List<UserScore> userScoreList = userScoreDao.findUserScore(map);
		JSONObject outJsonObject = new JSONObject();
		JSONArray scoreArray = new JSONArray();
		for(int i=0;i<userScoreList.size();i++){
			UserScore userScore = userScoreList.get(i);
			int roomid = userScore.getRoomid();
			int currentGame = userScore.getCurrentGame();
			int score = userScore.getScore();
			String createDate = DateUtils.getFormatDate(userScore.getCreateDate(), "yyyy/MM/dd hh:mm:ss");
			JSONObject everyScore = new JSONObject();
			everyScore.put("roomid", roomid);
			everyScore.put("currentGame", currentGame);
			everyScore.put("score", score);
			everyScore.put("createDate", createDate);
			scoreArray.put(everyScore);
		}
		outJsonObject.put("userScores", scoreArray);
		outJsonObject.put(method, "getUserScore");
		outJsonObject.put(discription, "�õ������û���ս��");
		outJsonObject.put("type", "everyScore");
		return outJsonObject;
	}


	/**�õ��û����յĳɼ�(�ܽ���)
	 * @param jsonObject
	 * @param session
	 */
	private void getUserFinalScore(JSONObject jsonObject, ChannelHandlerContext session) {
		//User user = (User) session.getAttribute("user");
		int index = jsonObject.getInt("index");
		int pageSzie = 10;
		Map<String, Object> map = new HashMap<>();
		map.put("userid", 19);
		map.put("pageIndex", (index-1)*pageSzie);
		map.put("pageSize", pageSzie);
		List<SumScore> sumScoreList = sumScoreDao.findSumScore(map);
		JSONObject outJSONObject = new JSONObject();
		JSONArray scoreArray = new JSONArray();
		for(int i=0;i<sumScoreList.size();i++){
			SumScore sumScore = sumScoreList.get(i);
			JSONObject score = new JSONObject();
			score.put("zhongMa", sumScore.getZhongMaTotal());
			score.put("roomNumber", sumScore.getRoomNumber());
			score.put("huPaiTotal", sumScore.getHuPaiTotal());
			score.put("jieGangTotal", sumScore.getJieGangTotal());
			score.put("anGangTotal", sumScore.getAnGangTotal());
			score.put("zhongMaTotal", sumScore.getZhongMaTotal());
			score.put("finalScore", sumScore.getFinalScore());
			score.put("fangGangTotal", sumScore.getFangGangTotal());
			score.put("mingGangtotal", sumScore.getMingGangtotal());
			score.put("createDate", DateUtils.getFormatDate(sumScore.getCreateDate(), "yyyy/MM/dd hh:mm:ss"));
			scoreArray.put(score);
		}
		outJSONObject.put("userScores", scoreArray);
		outJSONObject.put(method, "getUserScore");
		outJSONObject.put(discription, "�õ��û���ս��");
		System.out.println(outJSONObject);
	}
	
	
	public static void main(String[] args) {
		ScoreService scoreService = new ScoreService();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("index", 1);
	}


	
	
}
