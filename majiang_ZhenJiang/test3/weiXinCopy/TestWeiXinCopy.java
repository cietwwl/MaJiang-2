package weiXinCopy;

public class TestWeiXinCopy {

	public static void main(String[] args) {

		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx520718635911a4b0"
				+ "&redirect_uri="
				+ "http%3a%2f%2fwww.zxztech.com%2fdownload.html"
				+ "&response_type=code"
				+ "&scope=snsapi_login"
				+ "&state=1"
				+ "&from=timeline"
				+ "&isappinstalled=0"
				+ "&connect_redirect=1#wechat_redirect";

		System.out.println(url);
	}

}
