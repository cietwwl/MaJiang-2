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
 * 测试 --- 杠牌:gangCards
 */
public class TestLog {

	public static void main(String[] args) throws IOException {
		// Pattern pattern = Pattern.compile("\\{[^\\{]*\\}");
		// 匹配{}
		Pattern pattern = Pattern.compile("\\{[^\\{]*\\}");
		// 创建一个字符输入流类和字符输出流
		File readFile = new File("E:/日志分析/111.txt");
		Reader fr = new FileReader(readFile);
		File writeFile = new File("E:/日志分析/write.log");
		Writer fw = new FileWriter(writeFile);
		BufferedReader br = new BufferedReader(fr);
		BufferedWriter bw = new BufferedWriter(fw);
		String str = br.readLine();
		while (str != null) {
			if (str.contains("pengCards")&&str.contains("playGame")&&str.contains("peng")&&str.contains("pengDirction")) {
				Matcher matcher = pattern.matcher(str);
				//System.out.println(str);
				next(bw, matcher);
			}
			// 再读一行
			str = br.readLine();
		}
		// 关闭字符输入流和字符输出流
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
				// 写
				bw.write(sb.toString());
				// 换行
				bw.newLine();
			} catch (Exception e) {
				System.out.println("mei有"+jsonObject);
			}
		
		}
	}

}
