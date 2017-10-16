
public class TestJson {

	public static void main(String[] args) {
		
//		String str = "\"playGame\",\"cardId\":33}{\"type\":\"peng\",\"method\":\"playGame\",\"cardId\":555555}{\"type\":\"peng\",\"method\":\"playGame\",\"cardId\":33}{\"type\":\"peng\",\"method\":\"playGame\",\"cardId\":33}";
		String str = "ayAudio\",\"type\":\"voice\",\"messageId\":7777}{ayAudio\",\"type\":\"voice\",\"messageId\":6}";
		String jsonString = getJsonString(str);
		System.out.println(jsonString);
		
	}

	public static String  getJsonString(String str){
		int begin = 0;
		int end = 0;
		boolean haveBegin = false;
		boolean haveEnd = false;
		for(int i=0;i<str.length();i++){
			char charAt = str.charAt(i);
			if(charAt=='{'){
				haveBegin = true;
				begin = i;
			}else if(charAt=='}'){
				if(haveBegin){
					haveEnd = true;
					end = i;
				}
			}
			if(haveBegin && haveEnd){
				break;
			}
		}
		if(end==0){
			return null;
		}
		return str.substring(begin, end+1);
	}
}
