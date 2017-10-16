package gs.tool.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zxz.utils.CardsMap;

/**
 * @author Administrator
 * ���� --- ����:gangCards
 */
public class huTest {

	public static void main(String[] args) throws IOException {
		Pattern pattern = Pattern.compile("\\{^*}$");
//		File readFile = new File("E:/��־����/��������.txt");
		File readFile = new File("E:/��־����/�����.txt");
		Reader fr = new FileReader(readFile);
		File writeFile = new File("E:/��־����/����.txt");
		if(!writeFile.exists()){
			writeFile.createNewFile();
		}
		Writer fw = new FileWriter(writeFile);
		BufferedReader br = new BufferedReader(fr);
		BufferedWriter bw = new BufferedWriter(fw);
		String str = br.readLine();
		//223.145.77.125:55589:��ϰ
		//223.145.77.125:35079 �氮һ��
		//58.44.38.19:51368  �����
		//223.145.77.125:55679  ��ϰ
		//117.136.24.181:29039 �����Ԕ�
		
		while (str != null) {
			if(//str.contains("223.20.200.90:14485")&&
//					str.contains("111807")&&str.contains("enterRoom")
					str.contains("223.145.77.125:55589")
//					str.contains("223.145.77.125:50051")&&str.contains("login")//����IP�õ��û���
//					str.contains("223.145.77.125:50051")&&str.contains("userName")
//					str.contains("58.44.38.19:51368")&&str.contains("111807")
					){
				System.out.println(str);
				bw.write(str);
				bw.newLine();
			}
			// �ٶ�һ��
			str = br.readLine();
		}
		// �ر��ַ����������ַ������
		br.close();
		bw.close();
		fr.close();
		fw.close();
	}

	private static void next(BufferedWriter bw, Matcher matcher) throws IOException {
		while (matcher.find()) {
			String group = matcher.group();
			JSONObject jsonObject = new JSONObject(group);
			//System.out.println(jsonObject);
			try {
				JSONArray jsonArray = jsonObject.getJSONArray("pengCards");
				//System.out.println(jsonArray);
				StringBuffer sb = new StringBuffer();
				for(int i=0;i<jsonArray.length();i++){
					Object object = jsonArray.get(i);
					int card = Integer.parseInt(object+"");
					String cardType = CardsMap.getCardType(card);
					sb.append(cardType);
				}
				System.out.println(sb);
				// д
				bw.write(sb.toString());
				// ����
				bw.newLine();
			} catch (Exception e) {
				System.out.println("mei��"+jsonObject);
			}
		
		}
	}

}