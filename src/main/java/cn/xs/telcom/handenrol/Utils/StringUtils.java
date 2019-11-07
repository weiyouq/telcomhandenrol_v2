package cn.xs.telcom.handenrol.Utils;

public class StringUtils {

	public static String getMaxLen(String s, int maxLen) {
		if (s.length() > maxLen) {
			return s.substring(0, maxLen);
		} else {
			return s;
		}
	}

	public static boolean isNullOrEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}
}
