package gs.tool.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zxz.utils.CardsMap;

/**
 * @author Administrator ���� --- ����:gangCards
 */
public class HuCardTest {

	public static void main(String[] args) throws IOException {
		Pattern pattern = Pattern.compile("\\{^*}$");
		// File readFile = new File("E:/��־����/��������.txt");
		File readFile = new File("E:/��־����/186459.txt");
		Reader fr = new FileReader(readFile);
		File writeFile = new File("E:/��־����/����.txt");
		if (!writeFile.exists()) {
			writeFile.createNewFile();
		}
		Writer fw = new FileWriter(writeFile);
		BufferedReader br = new BufferedReader(fr);
		BufferedWriter bw = new BufferedWriter(fw);
		String str = br.readLine();
		while (str != null) {
			if (str.contains("114.242.248.140:41777")) {
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
			// System.out.println(jsonObject);
			try {
				JSONArray jsonArray = jsonObject.getJSONArray("pengCards");
				// System.out.println(jsonArray);
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < jsonArray.length(); i++) {
					Object object = jsonArray.get(i);
					int card = Integer.parseInt(object + "");
					String cardType = CardsMap.getCardType(card);
					sb.append(cardType);
				}
				System.out.println(sb);
				// д
				bw.write(sb.toString());
				// ����
				bw.newLine();
			} catch (Exception e) {
				System.out.println("mei��" + jsonObject);
			}

		}
	}

}
