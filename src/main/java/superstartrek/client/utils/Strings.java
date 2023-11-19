package superstartrek.client.utils;

public class Strings {

	public static boolean isEmpty(String s) {
		return s== null || s.isEmpty();
	}
	
	public static String denull(String s) {
		return s==null?"":s;
	}
}
