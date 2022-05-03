package com.github.hls.simplejob.utils;

import com.github.hls.simplejob.domain.SimpleJobEntity;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleJobUtils {

    private final static Pattern replaceSqlPattern = Pattern.compile("\\#(.*?)\\#");//正则表达式，取#和#之间的字符串，不包括#和#
    private final static Pattern check2NULLPattern = Pattern.compile("\\'\\#(.*?)\\#\\'");//正则表达式，取#和#之间的字符串，不包括#和#
    /**
     * 分段参数
     */
    public final static List<Map<String, Object>> sectionList = new ArrayList<>();

    /**
     * 系统参数
     */
    private final static Map<String, String> sysParam = new HashMap<>();

    public static void putSysParam(String k, String v){
        if (StringUtils.isEmpty(k) || StringUtils.isEmpty(v)) {
            return;
        }
        SimpleJobUtils.sysParam.put(k, v);
    }
    public static void clearSysParam(){
        SimpleJobUtils.sysParam.clear();
    }

    /**
     * 替换系统参数
     * @param sql
     * @return
     */
    public static String replaceSysParam(String sql){
        boolean contains = sql.contains("INTERVAL 7 DAY");
        if (contains && !sysParam.isEmpty()) {
            String interval = sysParam.get("INTERVAL");
            if (StringUtils.isNotBlank(interval)) {
                sql = sql.replace("INTERVAL 7 DAY", interval);
            }
        }
        return sql;
    }

    /**
     * 分页总数查询sql
     * @param sql
     * @return
     */
    public static String getCountSql(String sql){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT count(1) FROM(").append(sql).append(")a");
        return sb.toString();
    }


    /**
     * 替换分段参数
     * @param sql
     * @param map
     * @param defaultValue
     * @return
     */
    public static String getReplaceSql(String sql, Map<String, Object> map, Object defaultValue) {
        if (defaultValue == null) {
            defaultValue = "0";
        }
        //Pattern p = Pattern.compile("\\#(.*?)\\#");//正则表达式，取#和#之间的字符串，不包括#和#
        Matcher m = replaceSqlPattern.matcher(sql);
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

    /**
     * 填充NULL
     * @param sql
     * @return
     */
    public static String check2NULL(String sql) {
        //Pattern p = Pattern.compile("\\'\\#(.*?)\\#\\'");//正则表达式，取#和#之间的字符串，不包括#和#
        Matcher m = check2NULLPattern.matcher(sql);
        while (m.find()) {
            String key = m.group(0);//m.group(1)不包括这两个字符
            sql = sql.replaceAll(key, "NULL");
        }
        return sql;
    }

    /**
     * list转map
     * @param list
     * @return
     */
    public static Map<String, List<SimpleJobEntity>> transList2Map(List<SimpleJobEntity> list) {
        Map<String, List<SimpleJobEntity>> map = new HashMap<>();
        for (SimpleJobEntity simpleJobDO : list) {
            if (map.get(simpleJobDO.getJobName()) == null) {
                map.put(simpleJobDO.getJobName(), new ArrayList<>());
            }
            map.get(simpleJobDO.getJobName()).add(simpleJobDO);
        }
        return map;
    }

}
