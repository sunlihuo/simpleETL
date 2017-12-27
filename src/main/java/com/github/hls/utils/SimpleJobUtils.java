package com.github.hls.utils;

import com.github.hls.domain.SimpleJobDO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleJobUtils {

    public final static List<Map<String, Object>> sectionList = new ArrayList<>();

    public static String getReplaceSql(String sql, Map<String, Object> map, Object defaultValue) {
        if (defaultValue == null) {
            defaultValue = "0";
        }
        Pattern p = Pattern.compile("\\#(.*?)\\#");//正则表达式，取#和#之间的字符串，不包括#和#
        Matcher m = p.matcher(sql);
        while (m.find()) {
            String key = m.group(0);//m.group(1)不包括这两个字符
            Object value = map.get(m.group(1));
            if (null == value) {
                sql = sql.replace(key, String.valueOf(defaultValue));
            } else {
                sql = sql.replace(key, String.valueOf(value).replace("'", ""));
            }
        }

        //把所有未指定的'#value#'转为NULL
        return check2NULL(sql);
    }

    public static String check2NULL(String sql) {
        Pattern p = Pattern.compile("\\'\\#(.*?)\\#\\'");//正则表达式，取#和#之间的字符串，不包括#和#
        Matcher m = p.matcher(sql);
        while (m.find()) {
            String key = m.group(0);//m.group(1)不包括这两个字符
            sql = sql.replaceAll(key, "NULL");
        }
        return sql;
    }

    public static Map<String, List<SimpleJobDO>> transList2Map(List<SimpleJobDO> list) {
        Map<String, List<SimpleJobDO>> map = new HashMap<>();
        for (SimpleJobDO simpleJobDO : list) {
            if (map.get(simpleJobDO.getJobName()) == null) {
                map.put(simpleJobDO.getJobName(), new ArrayList<>());
            }
            map.get(simpleJobDO.getJobName()).add(simpleJobDO);
        }
        return map;
    }

}
