package com.github.hls.simplejob.utils;

public class StrUtils {

    /**
     * @Title removeLastSymbol
     * @Description 去除以某符号结尾的字符串的结尾字符
     * @return String    返回类型
     */
    public static String removeLastSymbol(String str, String symbol) {
        if (!str.contains(symbol)) {
            return str;
        }
        if (str.endsWith(symbol)) {
            StringBuffer sb = new StringBuffer(str);
            sb.replace(sb.lastIndexOf(symbol), sb.length(), "");
            return sb.toString();
        } else {
            return str;
        }
    }
}
