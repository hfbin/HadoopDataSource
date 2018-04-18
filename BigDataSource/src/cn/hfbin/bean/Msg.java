package cn.hfbin.bean;

public class Msg {
	private static String getContent(String line, int start, int end) {
		return line.substring(start, end);
	}

	public static String getYear(String string) {
		return getContent(string, 14, 18);
	}
	
	public static boolean getCharAt(String str , Integer id) {
		
		if(str.charAt(id) == ' '){
			return true;
		}else{
			return false;
		}
	}
	public static String getMax1(String string) {
		return getContent(string, 104, 108);
	}
	public static String getMax2(String string) {
		return getContent(string, 103, 108);
	}
	public static String getMin1(String string) {
		return getContent(string, 113, 116);
	}
	public static String getMin2(String string) {
		return getContent(string, 112, 116);
	}
	public static String getAvg(String string) {
		return getContent(string, 26, 30);
	}

	public static String getParfait(String string) {
		return getContent(string, 120, 123);
	}
	
	public static String getZhandian(String string) {
		return getContent(string, 0, 6);
	}

	public static String getDate(String string) {
		return getContent(string, 14, 22);
	}
	
	
	

}
