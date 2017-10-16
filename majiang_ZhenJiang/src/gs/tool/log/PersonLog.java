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
 * @author 人名追踪
 * 测试 --- 杠牌:gangCards
 */
public class PersonLog {

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
		// ip : 
		//223.20.209.189:59271,
		//223.20.209.189:59283,
		//223.20.209.189:59289,
		//223.20.209.189:59293,
		//223.20.209.189:59341,
		//223.20.209.189:59345,
		//223.20.209.189:59350,
		//223.20.209.189:59357,
		//223.20.209.189:59384,
		//223.20.209.189:59439,
		//223.20.209.189:59439,
		String [] arr = {
				"183.214.50.97:2970",
				"183.214.50.97:2990",
				"183.214.50.97:3005",
				"183.214.50.97:3010",
				"183.214.50.97:3035",
				"183.214.50.97:2539",
				"183.214.50.97:2551",
				"183.214.50.97:2587",
				"183.214.50.97:2644",
				"183.214.50.97:2683",
				"183.214.50.97:2699",
		};
		while (str != null) {
			/*for(int i=0;i<arr.length;i++){
				System.out.println(arr[i]);
			}*/
			/*if(str.contains("\"userName\":\"421\"")&&str.contains("login")){
				System.out.println(str);
			}*/
			for(int i=0;i<arr.length;i++){
				if(str.contains(arr[i])){
					System.out.println(str);
					bw.write(str);
					// 换行
					bw.newLine();
					break;
				}
			}
			/*if(str.contains("223.20.209.189:59271")){
				System.out.println(str);
			}*/
			// 再读一行
			str = br.readLine();
		}
		// 关闭字符输入流和字符输出流
		br.close();
		bw.close();
		fr.close();
		fw.close();

	}

	
}
