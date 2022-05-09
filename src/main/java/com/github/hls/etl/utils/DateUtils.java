/**  
 * @Title: DateUtils.java
 * @Package tf56.skynet.utils
 * @Description: TODO(用一句话描述该文件做什么)
 * @author chen.zhang
 * @date 2016年12月15日 上午10:48:38
 * @version V1.0  
 */
package com.github.hls.etl.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName: DateUtils
 * @Description: 日期工具类
 * @author sunlihuo
 * @date 2016年12月15日 上午10:48:38
 */
public class DateUtils {

	public static SimpleDateFormat df;

	public static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	public static final String FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm:00";

	public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";

	public static final String FORMAT_YYYY_MM = "yyyy-MM";

	public static final String FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

	public static final String FORMAT_YYYYMMDDHH = "yyyyMMddHH";
	public static final String FORMAT_YYYYMMDD = "yyyyMMdd";

	public static boolean isNotFormat(String dateStr, String dateFormat) {
		return isFormat(dateStr, dateFormat);
	}
	
	public static boolean isFormat(String dateStr, String dateFormat) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(dateFormat);
			format.parse(dateStr);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}
	/**
	 *
	 * @param date1 <String>
	 * @param date2 <String>
	 * @return int
	 * @throws ParseException
	 */
	public static int getMonthGap(String date1, String date2)
			throws ParseException {

		int result = 0;

		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM);

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		c1.setTime(sdf.parse(date1));
		c2.setTime(sdf.parse(date2));

		result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);

		return Math.abs(result) + 1;

	}
	/**
	 *
	 * @param date1 <String>
	 * @param date2 <String>
	 * @return int
	 * @throws ParseException
	 */
	public static int getDayGap(String date1, String date2)
			throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD);


		return (int) (Math.abs(sdf.parse(date1).getTime() - sdf.parse(date2).getTime()) / 1000/3600/24) + 1;

	}

	
	/**
	 * 返回给定日期的“FORMAT_YYYY_MM_DD_HH_MM_SS”格式的开始时间 
	 * @param date
	 * @return
	 */
	public static String getBeginStrOfSelectedDate(Date date) {
		Calendar beginDateCalendar = Calendar.getInstance();

		beginDateCalendar.setTimeInMillis(date.getTime());
		beginDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
		beginDateCalendar.set(Calendar.MINUTE, 0);
		beginDateCalendar.set(Calendar.SECOND, 0);
		beginDateCalendar.set(Calendar.MILLISECOND, 0);
		df = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM_SS);
		return df.format(new Date(beginDateCalendar.getTimeInMillis()));
	}
	
	/**
	 * 返回给定日期的“FORMAT_YYYY_MM_DD_HH_MM_SS”格式的结束时间 
	 * @param date
	 * @return
	 */
	public static String getEndStrOfSelectedDate(Date date) {
		Calendar endDateCalendar = Calendar.getInstance();
		endDateCalendar.setTimeInMillis(date.getTime());
		endDateCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endDateCalendar.set(Calendar.MINUTE, 59);
		endDateCalendar.set(Calendar.SECOND, 59);
		endDateCalendar.set(Calendar.MILLISECOND, 0);
		df = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM_SS);
		return df.format(new Date(endDateCalendar.getTimeInMillis()));
	}
	
	
	/**
	 * 根据日期字符串判断当月第几周
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static int getWeek(String str) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(str);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// 第几周
		int week = calendar.get(Calendar.WEEK_OF_MONTH);
		// //第几天，从周日开始
		// int day = calendar.get(Calendar.DAY_OF_WEEK);
		return week;
	}

	/**
	 * 返回某日期 之前或之后的 日期
	 */
	public static final Date getDaysByCount(Date requestDate, int dayCount) {
		return getDaysByCount(requestDate, dayCount, FORMAT_YYYY_MM_DD_HH_MM_SS);
	}
	/**
	 * 返回某日期 之前或之后的 日期 
	 */
	public static final Date getDaysByCount(Date requestDate, int dayCount, String dataFormat) {
		df = new SimpleDateFormat(dataFormat);
		df.format(requestDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(requestDate);
		cal.add(Calendar.DATE, dayCount);
		df.format(cal.getTime());
		return cal.getTime();
	}
	
	/**
	 * 返回昨天起始日期 
	 */
	public static final Date getYesterday() {
		Date requestDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(requestDate);
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	/**
	 * 返回昨天起始日期 
	 */
	public static final String getYesterdayStr(String dataFormat) {
		return getDateStr(getYesterday(),dataFormat);
	}
	
	
	/**
	 * 返回某日期 之前或之后的 日期 
	 */
	public static final String getDayStrByCount(Date requestDate, int dayCount, String dataFormat) {
		df = new SimpleDateFormat(dataFormat);
		Calendar cal = Calendar.getInstance();
		cal.setTime(requestDate);
		cal.add(Calendar.DATE, dayCount);
		df.format(cal.getTime());
		return df.format(cal.getTime());
	}
	
	/**
	 * 返回“近三十天”的第一天日期
	 */
	public static String getNear31DayStr(String dataFormat) {
		df = new SimpleDateFormat(dataFormat);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -30);
		df.format(cal.getTime());
		return df.format(cal.getTime());
	}
	
	/**
	 * 返回“近三十天”的第一天日期
	 */
	public static String getNear12MonthStr(String dataFormat) {
		df = new SimpleDateFormat(dataFormat);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, -11);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		df.format(cal.getTime());
		return df.format(cal.getTime());
	}
	
	/**
	 * 返回某日期 之后第N天后的 日期
	 * @param
	 * @param
	 * @return 校验时期格式是否为特定格式
	 */
	public static final Date getDaysAfterTheDate(Date requestDate,int DayCouts)  {
		df = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM_SS);
		df.format(requestDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(requestDate);
		cal.add(Calendar.DATE, DayCouts);
		df.format(cal.getTime());
		return cal.getTime();
	}

	public static String changeStrDateFormat(String str, String afterFormat) throws Exception {
		Date date = fromStrGetDate(str);
		return getStrByDate(date, afterFormat);
	}

	public static String changeStrDateFormat(String str, String beforFormat, String afterFormat) throws Exception {
		Date date = fromStrGetDate(str, beforFormat);
		return getStrByDate(date, afterFormat);
	}

	public static final String getStrByDate(Date date) {
		return getStrByDate(date, FORMAT_YYYY_MM_DD_HH_MM_SS);
	}

	public static final String getStrByDate(Date date, String format) {
		df = new SimpleDateFormat(format);
		return df.format(date);
	}

	public static final String getStrByDate(String date, String beforeFormat, String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(beforeFormat);
		return getStrByDate(sdf.parse(date), format);
	}

	public static final Date fromStrGetDate(String dateStr) throws Exception {
		return fromStrGetDate(dateStr, FORMAT_YYYY_MM_DD_HH_MM_SS);
	}
	public static final Date fromStrGetDate2(String dateStr) throws Exception {
		return fromStrGetDate(dateStr, FORMAT_YYYY_MM_DD);
	}

	public static final Date fromStrGetDate(String dateStr, String dateFormate) throws ParseException {
		df = new SimpleDateFormat(dateFormate);
		return df.parse(dateStr);
	}

	/**
	 * @Description 返回给定日期当月的最后一天
	 * @author chen.zhang
	 * @date 2016年12月15日 上午11:09:10
	 * @return “yyyy-MM-dd” 格式日期字符串
	 */
	public static String getMonthLastDay(String dateStr) {
		return getMonthLastDay(dateStr, FORMAT_YYYY_MM_DD);
	}

	/**
	 * @Description 返回给定日期当月的最后一天
	 * @author c.zhang
	 * @date 2016年12月15日 上午11:09:10
	 * @return dateFormat格式日期字符串
	 */
	public static String getMonthLastDay(String dateStr, String dateFormat) {
		if (StringUtils.isBlank(dateStr))
			return null;
		if (StringUtils.isBlank(dateFormat))
			return null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			Date dateDat = sdf.parse(dateStr);
			Calendar cal_1 = Calendar.getInstance();// 获取当前日期
			cal_1.setTime(dateDat);
			cal_1.add(Calendar.MONTH, 1);
			cal_1.set(Calendar.DAY_OF_MONTH, 0);
			return sdf.format(cal_1.getTime());
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * @Description 返回给定日期当月的最后一天
	 * @author hill
	 * @date 2016年12月15日 上午11:09:10
	 * @return dateFormat格式日期字符串
	 */
	public static String getMonthLastDay(String dateStr, String dateFormat,String format) {
		if (StringUtils.isBlank(dateStr))
			return null;
		if (StringUtils.isBlank(dateFormat))
			return null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			SimpleDateFormat date=new SimpleDateFormat(dateFormat);
			Date dateDat = sdf.parse(dateStr);
			Calendar cal_1 = Calendar.getInstance();// 获取当前日期
			cal_1.setTime(dateDat);
			cal_1.add(Calendar.MONTH, 1);
			cal_1.set(Calendar.DAY_OF_MONTH, 0);
			return date.format(cal_1.getTime());
		} catch (Exception e) {
			return null;
		}
	}
	public static String getMonLastDay(String dateStr,String format) {
		return  getMonthLastDay(dateStr,FORMAT_YYYY_MM_DD,format);
	}
	
	

	/**
	 * @Description 返回给定日期当月的第一天
	 * @author chen.zhang
	 * @date 2016年12月15日 上午11:09:10
	 * @return “yyyy-MM-dd” 格式日期字符串
	 */
	public static String getMonthFirstDay(String dateStr) {
		return getMonthFirstDay(dateStr, FORMAT_YYYY_MM_DD);
	}

	/**
	 * @Description 返回给定日期当月的第一天
	 * @author c.zhang
	 * @date 2016年12月15日 上午11:09:10
	 * @return dateFormat格式日期字符串
	 */
	public static String getMonthFirstDay(String dateStr, String dateFormat) {
		if (StringUtils.isBlank(dateStr))
			return null;
		if (StringUtils.isBlank(dateFormat))
			return null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			Date dateDat = sdf.parse(dateStr);
			Calendar cal_1 = Calendar.getInstance();// 获取当前日期
			cal_1.setTime(dateDat);
			cal_1.add(Calendar.MONTH, 0);
			cal_1.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
			return sdf.format(cal_1.getTime());
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * @Description 返回给定日期当月的第一天
	 * @author hill
	 * @date 2016年12月15日 上午11:09:10
	 * @return dateFormat格式日期字符串
	 * @param  dateStr 时间日期的字符串，dateFormat 输出时间字符串的时间格式 ,format 输入时间字符串得分格式
	 * 
	 */
	public static String getMonthFirstDay(String dateStr, String dateFormat,String format) {
		if (StringUtils.isBlank(dateStr))
			return null;
		if (StringUtils.isBlank(dateFormat))
			return null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			SimpleDateFormat date = new SimpleDateFormat(dateFormat);
			Date dateDat = sdf.parse(dateStr);
			Calendar cal_1 = Calendar.getInstance();// 获取当前日期
			cal_1.setTime(dateDat);
			cal_1.add(Calendar.MONTH, 0);
			cal_1.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
			return date.format(cal_1.getTime());
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getMonFirstDay(String dateStr,String format){
		return getMonthFirstDay(dateStr,FORMAT_YYYY_MM_DD,format);
	}
	
	
	
	
	public static String getThisMonthFirstDay(String dateFormat) {
		Date now = new Date();
		if (StringUtils.isBlank(dateFormat))
			return null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			Calendar cal_1 = Calendar.getInstance();// 获取当前日期
			cal_1.setTime(now);
			cal_1.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
			cal_1.set(Calendar.HOUR, 0);
			cal_1.set(Calendar.MINUTE, 0);
			cal_1.set(Calendar.SECOND, 0);
			return sdf.format(cal_1.getTime());
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * @Description 返回给定日期当年的第一天
	 * @author c.zhang
	 * @date 2016年12月15日 上午11:09:10
	 * @return dateFormat格式日期字符串
	 */
	public static String getYearFirstDay(String dateStr, String dateFormat) {
		if (StringUtils.isBlank(dateStr) || StringUtils.isBlank(dateFormat))
			return null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			Date dateDat = sdf.parse(dateStr);
			Calendar cal_1 = Calendar.getInstance();// 获取当前日期
			cal_1.setTime(dateDat);
			cal_1.set(Calendar.DAY_OF_YEAR, 1);
			cal_1.set(Calendar.HOUR, 0);
			cal_1.set(Calendar.MINUTE, 0);
			cal_1.set(Calendar.SECOND, 0);
			return sdf.format(cal_1.getTime());
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getThisYearFirstDay(String dateFormat) {
		if (StringUtils.isBlank(dateFormat))
			return null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			Date now = new Date();
			Calendar cal_1 = Calendar.getInstance();// 获取当前日期
			cal_1.setTime(now);
			cal_1.set(Calendar.DAY_OF_YEAR, 1);
			cal_1.set(Calendar.HOUR, 0);
			cal_1.set(Calendar.MINUTE, 0);
			cal_1.set(Calendar.SECOND, 0);
			return sdf.format(cal_1.getTime());
		} catch (Exception e) {
			return null;
		}
	}
	

	/**
	 * @Description: 近3个月第一天）
	 * @author chen.zhang
	 * @date 2016年12月15日 下午5:49:56
	 * @param {sign=签名,
	 *            timestamp=时间戳}
	 * @return 返回类型 String CommonJson格式
	 */
	public static String get3MonFirDay() {
		return getSomMonFirDayBySomDay(new Date(), FORMAT_YYYY_MM_DD, -2);
	}

	/**
	 * @Description: 近6个月第一天）
	 * @author chen.zhang
	 * @date 2016年12月15日 下午5:49:56
	 * @param {sign=签名,
	 *            timestamp=时间戳}
	 * @return 返回类型 String CommonJson格式
	 */
	public static String get6MonFirDay() {
		return getSomMonFirDayBySomDay(new Date(), FORMAT_YYYY_MM_DD, -5);
	}
	
	/**
	 * @return String时间转换成“yyyy-MM-dd”格式Date时间
	 */
	public static final Date fromStrGetDateDay(String dateStr) throws Exception {
		return fromStrGetDate(dateStr,FORMAT_YYYY_MM_DD);
	}

	/**
	 * @Description: 返回给定日期之前的N个月的第一天
	 * @author chen.zhang
	 * @date 2016年12月15日 下午5:49:26
	 * @param {sign=签名,
	 *            timestamp=时间戳}
	 * @return 返回类型 String CommonJson格式
	 */
	public static String getSomMonFirDayBySomDay(Date date, String dateFormat, int num) {
		if (date == null)
			return null;
		if (StringUtils.isBlank(dateFormat))
			return null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			Calendar cal_1 = Calendar.getInstance();// 获取当前日期
			cal_1.setTime(date);
			cal_1.add(Calendar.MONTH, num);
			cal_1.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
			return sdf.format(cal_1.getTime());
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 取上个月最后一天
	 * @return
	 */
	public static final String getSomMonthLastDay(){
		Calendar calendar = Calendar.getInstance();  
		int month = calendar.get(Calendar.MONTH);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));  
		Date strDateTo = calendar.getTime();  
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD);
		return sdf.format(strDateTo);
	}
	public static final String getDateStr(Date date) {
		df = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM_SS);
		return df.format(date);
	}

	public static final String getDateStr(Date date, String dataFormat) {
		df = new SimpleDateFormat(dataFormat);
		return df.format(date);
	}
	/**
	 * 判定给定日期不是本月最后一天<br>
	 * 1.如果是当月的昨天	 	》》	false<br>
	 * 2.如果是往月的最后一天	》》	false<br>
	 * 3.如果任意参数为空		》》	true<br>
	 */
	public static boolean isNotMothLastDay(String inputdateES, String format) {
		return !isMothLastDay(inputdateES, format);
	}

	/**
	 * 获取当前时间是本年的第几天
	 * @param date
	 * @return
	 */
	public static final Integer getDateDayFromYear(Date date){
		Calendar ca=Calendar.getInstance();
		ca.setTime(date);
		SimpleDateFormat sdf=new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM);
		//String st=sdf.format(date);
		return  ca.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * 获取当前时间是本月的第几天
	 * @param date
	 * @return
	 */
	public static final Integer getDateDayFromMonth(Date date){
		Calendar ca=Calendar.getInstance();
		ca.setTime(date);
		SimpleDateFormat sdf=new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM);
		//String st=sdf.format(date);
		return  ca.get(Calendar.DAY_OF_MONTH);

	}
	/**
	 * 取得当月天数
	 * */
	public static final int getCurrentMonthLastDay()
	{
		Calendar a = Calendar.getInstance();
		a.set(Calendar.DATE, 1);//把日期设置为当月第一天
		a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}

	/**
	 * 返回今天占本月的百分比
	 * @param date
	 * @return
	 */
	public static final double getRateInMonth(Date date){
		return new BigDecimal((float)getDateDayFromMonth(date)/getCurrentMonthLastDay()).setScale(4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
	}

	/**
	 * 返回今天占今年的百分比
	 * @param date
	 * @return
	 */
	public static final double getRateInYear(Date date){
		return new BigDecimal((float)getDateDayFromYear(date)/365).setScale(4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
	}


	/**
	 * 判定给定日期是否是本月最后一天<br>
	 * 1.如果是当月的昨天	 	》》	true<br>
	 * 2.如果是往月的最后一天	》》	true<br>
	 * 3.如果任意参数为空		》》	false<br>
	 */
	private static boolean isMothLastDay(String inputdateES, String format) {
		if (StringUtils.isBlank(inputdateES) || StringUtils.isBlank(format)) {
			return false;
		}
		String yesterDay = getDayStrByCount(new Date(), -1, format);
		if (inputdateES.equals(yesterDay)) {
			return true;
		}
		String lastDay = getMonthLastDay(inputdateES, format);
		return inputdateES.equals(lastDay);
	}
	
    public static String dateDiff(Long startTime, Long endTime) {     
    	long between = endTime - startTime;
        long day = between / (24 * 60 * 60 * 1000);
        long hour = (between / (60 * 60 * 1000) - day * 24);
        long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long ms = (between - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
                - min * 60 * 1000 - s * 1000);
		return day + "天" + hour + "小时" + min + "分" + s + "秒" + ms
                + "毫秒";   
    }  
    /**
	 * @function 获取时间的前一天
	 * @author junqiang.qiu
     * @throws Exception 
	 * @date 2017年6月8日
	 */
    public static String  getBeforeOneDay(String dateStr) throws Exception{
    	if(StringUtils.isBlank(dateStr)){
    		return null;
    	}
    	
    	int dayCount=-1;
    	Date requestDate=fromStrGetDate2(dateStr);
    	Date BeforeOneDay=getDaysByCount(requestDate, dayCount);
    	
    	return  getDateStr(BeforeOneDay, FORMAT_YYYY_MM_DD);
    }
	/**
	 * @function 获取时间的前一天
	 * @author junqiang.qiu
	 * @throws Exception
	 * @date 2017年6月8日
	 */
	public static String  getBeforeOneDayByDate(Date dateStr) throws Exception{
		//Date requestDate=fromStrGetDate2(dateStr);
		Date BeforeOneDay=getDaysByCount(dateStr,-1);

		return  getDateStr(BeforeOneDay, FORMAT_YYYY_MM_DD);
	}
    
    /**
	 * @function 获取时间的上一个月
	 * @author junqiang.qiu
	 * @date 2017年6月8日
	 */
    public static String getBeforeOneMonth(String dateStr) throws Exception{
    	if(StringUtils.isBlank(dateStr)){
    		return null;
    	}
    	int MothCount=-1;
    	Date requestDate=fromStrGetDate(dateStr, FORMAT_YYYY_MM);
    	Date BeforeOneMonth=getMonthsByCount(requestDate, MothCount);
    	
    	return getDateStr(BeforeOneMonth,FORMAT_YYYY_MM);
    }
    
    /**
	 * @function 返回某日期 之前或之后的 月
	 * @author junqiang.qiu
	 * @date 2017年6月8日
	 */
    public static Date getMonthsByCount(Date requestDate, int dayCount){
    	return getMonthsByCount(requestDate, dayCount,FORMAT_YYYY_MM);
    }
    
    /**
	 * 返回某日期 之前或之后的 月
	 */
    public static final Date getMonthsByCount(Date requestDate, int dayCount, String dataFormat) {
		df = new SimpleDateFormat(dataFormat);
		df.format(requestDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(requestDate);
		cal.add(Calendar.MONTH, dayCount);
		df.format(cal.getTime());
		return cal.getTime();
	}
    
    /**
     * 返回 天 月 季度 年
     * @param date
     * @return
     */
    public static Map<String, List<String>> subDate(String date){
    	String[] dates = date.split("-");
    	String year = dates[0];
    	String month = dates[1];
    	Map<String, List<String>> map = new HashMap<>();
    	List<String> toDayList = new ArrayList<>();
    	toDayList.add(date);
    	toDayList.add(date);
    	map.put("toDay", toDayList);
    	
    	List<String> monthList = new ArrayList<>();
    	monthList.add(year+"-"+month);
    	monthList.add(year+"-"+month);
    	map.put("month", monthList);
    	
    	int monthInt = Integer.valueOf(month);
    	String quarterNum = "一";
    	String beginQuarter = year;
    	String engQuarter = year;
    	if (monthInt >= 1 && monthInt <= 3) {
    		beginQuarter = beginQuarter + "-01-01";
    		engQuarter = engQuarter + "-03-01";
    		engQuarter = getMonthLastDay(engQuarter);
    		quarterNum = "一";
		} else if (monthInt >= 4 && monthInt <= 6) {
    		beginQuarter = beginQuarter + "-04-01";
    		engQuarter = engQuarter + "-06-01";
    		engQuarter = getMonthLastDay(engQuarter);
    		quarterNum = "二";
		} else if (monthInt >= 7 && monthInt <= 9) {
    		beginQuarter = beginQuarter + "-07-01";
    		engQuarter = engQuarter + "-09-01";
    		engQuarter = getMonthLastDay(engQuarter);
    		quarterNum = "三";
		} else if (monthInt >= 10 && monthInt <= 12) {
    		beginQuarter = beginQuarter + "-10-01";
    		engQuarter = engQuarter + "-12-01";
    		engQuarter = getMonthLastDay(engQuarter);
    		quarterNum = "四";
		}
    	List<String> quarterList = new ArrayList<>();
    	quarterList.add(beginQuarter);
    	quarterList.add(engQuarter);
    	map.put("quarter", quarterList);
    	
    	List<String> quarterNumList = new ArrayList<>();
    	quarterNumList.add(quarterNum);
    	map.put("quarterNum", quarterNumList);
    	
    	List<String> yearList = new ArrayList<>();
    	yearList.add(year);
    	yearList.add(year);
    	map.put("year", yearList);
		return map;
    }
    
    
    public static Map<String, Integer> getYearMonDay(String dateStr, String dateForamt){
		SimpleDateFormat sdf = new SimpleDateFormat(dateForamt);  
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);//获取年份
		int month = cal.get(Calendar.MONTH) + 1;//获取月份
		//它返回的是常量值,从0到11	最好对应Calendar.JANUARY,Calendar.FEBUARY,Calendar.MARCH......
		int day = cal.get(Calendar.DATE);//获取日
		
		Map<String, Integer> map = new HashMap<>();
		map.put("year", year);
		map.put("month", month);
		map.put("day", day);
		return map;
    }
    
    public static boolean compare(String beginDate, String endDate, String date){
    	try {
			Date a = fromStrGetDate2(beginDate);
			Date b = fromStrGetDate2(endDate);
			Date c = fromStrGetDate2(date);
					
			return c.before(b) && c.after(a);
		} catch (Exception e) {
		}
		return false;
    	
    }
	public static void main(String[] args) {
    	try {
			System.out.println(getBeforeOneDayByDate(new Date()));
		}catch (Exception e){

		}
		
	}
	
	
}