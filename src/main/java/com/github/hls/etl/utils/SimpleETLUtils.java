package com.github.hls.etl.utils;

import com.github.hls.etl.domain.SimpleETLDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SimpleETLUtils {

    private final static Pattern replaceSqlPattern = Pattern.compile("\\#(.*?)\\#");//正则表达式，取#和#之间的字符串，不包括#和#
    private final static Pattern check2NULLPattern = Pattern.compile("\\'\\#(.*?)\\#\\'");//正则表达式，取#和#之间的字符串，不包括#和#
    /**
     * 分段参数
     */
    public final static List<Map<String, Object>> sectionValueList = new ArrayList<>();

    /**
     * 系统参数
     */
    private final static Map<String, String> sysParam = new HashMap<>();

    public static void putSysParam(String k, String v){
        if (StringUtils.isEmpty(k) || StringUtils.isEmpty(v)) {
            return;
        }
        SimpleETLUtils.sysParam.put(k, v);
        log.info("======全局参数:{}", sysParam);
    }
    public static void clearSysParam(){
        SimpleETLUtils.sysParam.clear();
        log.info("======全局参数清空======");
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
     * 替换分段参数 设置默认值
     * @param sql
     * @param map
     * @param defaultValue
     * @return
     */
    public static String getSectionValueReplaceSql(String sql, Map<String, Object> map, Object defaultValue) {
        if (map==null || CollectionUtils.isEmpty(map.keySet())) {
            return sql;
        }

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
     * 替换全局参数
     * @param sql
     * @return
     */
    public static String getSysValueReplaceSql(String sql) {
        Map<String, String> sysValueMap = SimpleETLUtils.sysParam;
        if (CollectionUtils.isEmpty(sysValueMap.keySet())) {
            return sql;
        }

        //Pattern p = Pattern.compile("\\#(.*?)\\#");//正则表达式，取#和#之间的字符串，不包括#和#
        Matcher m = replaceSqlPattern.matcher(sql);
        while (m.find()) {
            String key = m.group(0);//m.group(1)不包括这两个字符
            Object value = sysValueMap.get(m.group(1));
            if (null == value) {
                //sql = sql.replace(key, String.valueOf(defaultValue));
            } else {
                sql = sql.replace(key, String.valueOf(value).replace("'", ""));
            }
        }
        return sql;
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
    public static Map<String, List<SimpleETLDO>> transList2Map(List<SimpleETLDO> list) {
        Map<String, List<SimpleETLDO>> map = new HashMap<>();
        for (SimpleETLDO simpleJobDO : list) {
            if (map.get(simpleJobDO.getJobName()) == null) {
                map.put(simpleJobDO.getJobName(), new ArrayList<>());
            }
            map.get(simpleJobDO.getJobName()).add(simpleJobDO);
        }
        return map;
    }

}
