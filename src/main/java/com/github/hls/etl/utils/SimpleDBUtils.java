package com.github.hls.etl.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * QueryRunner DB工具类
 *
 * @author sunlihuo
 */
@Slf4j
public class SimpleDBUtils {

	/**
	 * 插入
	 * @param dataSource
	 * @param tableName
	 * @param map
	 */
	public static void insert(DataSource dataSource, String tableName, Map<String, Object> map) {
		String[] insertFileds = map.keySet().toArray(new String[]{});
		String sql = buildInsertSQL(tableName, insertFileds);
		QueryRunner sqlRunner = new QueryRunner(dataSource);
		List<Object> params = getParams(insertFileds, map);
		try {
			sqlRunner.insert(sql, new ScalarHandler<Long>(), params.toArray());
		} catch (SQLException e) {
			log.error("sqlRunner.insert error ", e);
		}
	}

	/**
	 * 更新
	 * @param dataSource
	 * @param tableName
	 * @param map
	 * @param idName
	 * @param id
	 */
	private static void update(DataSource dataSource, String tableName, Map<String, Object> map, String idName, long id) {
		String[] insertFileds = map.keySet().toArray(new String[]{});
		String sql = buildUpdateSQL(tableName, insertFileds, idName);

		List<Object> params = getParams(insertFileds, map);
		params.add(id);

		try {
			QueryRunner sqlRunner = new QueryRunner(dataSource);
			sqlRunner.update(sql, params.toArray());
		} catch (SQLException e) {
			log.error("sqlRunner.update error ", e);
		}
	}

	/**
	 * 生成插入sql
	 * @param tableName
	 * @param insertFileds
	 * @return
	 */
	public static String buildInsertSQL(String tableName, String[] insertFileds) {
		StringBuffer insertSB = new StringBuffer("INSERT INTO ");
		// 表字段
		StringBuffer columsSB = new StringBuffer();
		// value占位符
		StringBuffer placeHolderSB = new StringBuffer();
		for (int i = 0; i < insertFileds.length; i++) {
			columsSB.append(insertFileds[i]).append(",");
			placeHolderSB.append("?").append(",");
		}
		insertSB.append(tableName);
		insertSB.append("(");
		insertSB.append(StrUtils.removeLastSymbol(columsSB.toString(), ","));
		insertSB.append(" ) VALUES ( ");
		insertSB.append(StrUtils.removeLastSymbol(placeHolderSB.toString(), ","));
		insertSB.append(")");
		return insertSB.toString();
	}

	/**
	 * 生成更新sql
	 * @param tableName
	 * @param insertFileds
	 * @param idName
	 * @return
	 */
	private static String buildUpdateSQL(String tableName, String[] insertFileds, String idName) {
		StringBuffer updateDB = new StringBuffer("update " + tableName + " set ");
		getValueOrWhere(updateDB, insertFileds, ", ");
		updateDB.append("where " + idName + " = ?");
		return updateDB.toString();
	}

	/**
	 * 生成查询sql
	 * @param tableName
	 * @param idName
	 * @param insertFileds
	 * @return
	 */
	private static String buildSelectSQL(String tableName, String idName, String[] insertFileds) {
		StringBuffer sBuilder = new StringBuffer("select ");
		sBuilder.append(idName).append(" from ").append(tableName).append(" where ");
		getValueOrWhere(sBuilder, insertFileds, "and ");
		return sBuilder.toString();
	}

	/**
	 * 校验是否存在
	 * @param table
	 * @param idName
	 * @param dataSource
	 * @param whereMap
	 * @return
	 */
	private static Object[] queryIsExist(String table, String idName, DataSource dataSource, Map<String, Object> whereMap) {
		String[] insertFileds = whereMap.keySet().toArray(new String[]{});
		List<Object> params = getParams(insertFileds, whereMap);

		QueryRunner sqlRunner = new QueryRunner(dataSource);
		String sql = buildSelectSQL(table, idName, insertFileds);
		Object[] result = null;
		try {
			result = sqlRunner.query(sql, new ArrayHandler(), params.toArray());
		} catch (SQLException e) {
			log.error("sqlRunner.query error ", e);
		}
		return result;
	}

	private static void getValueOrWhere(StringBuffer sBuffers, String[] insertFileds, String connector) {
		for (int i = 0; i < insertFileds.length; i++) {
			if (i == insertFileds.length - 1) {
				//最后一次
				sBuffers.append(insertFileds[i]).append(" = ? ");
			} else {
				sBuffers.append(insertFileds[i]).append(" = ? ").append(connector);
			}
		}
	}

	public static List<Object> getParams(String[] insertFileds, Map<String, Object> map) {
		List<Object> paramsList = new ArrayList<>();
		for (String key : insertFileds) {
			paramsList.add(map.get(key));
		}
		return paramsList;

	}

	/**
	 * 查询
	 * @param sql
	 * @param dataSource
	 * @return
	 */
	public static List<Map<String, Object>> queryListMap(String sql, DataSource dataSource) {
		QueryRunner sqlRunner = new QueryRunner(dataSource);
		List<Map<String, Object>> result = null;
		try {
			result = sqlRunner.query(sql, new MapListHandler());
		} catch (SQLException e) {
			log.error("sqlRunner.queryListMap error ", e);
		}
		return result;
	}

	/**
	 * 分页查询
	 * @param sql
	 * @param dataSource
	 * @param offset
	 * @param limit
	 * @return
	 */
	public static List<Map<String, Object>> queryListMapPage(String sql, DataSource dataSource, int offset, int limit) {
		StringBuilder sb = new StringBuilder();
		sb.append(sql).append(" LIMIT ").append(offset).append(",").append(limit);
		String limitSql = sb.toString();
		QueryRunner sqlRunner = new QueryRunner(dataSource);
		List<Map<String, Object>> result = null;
		try {
			result = sqlRunner.query(limitSql, new MapListHandler());
		} catch (SQLException e) {
			log.error("sqlRunner.queryListMap error ", e);
		}
		return result;
	}

	/**
	 * 查询单行
	 * @param sql
	 * @param dataSource
	 * @return
	 */
	public static Map<String, Object> queryMap(String sql, DataSource dataSource) {
		QueryRunner sqlRunner = new QueryRunner(dataSource);
		Map<String, Object> result = null;
		try {
			result = sqlRunner.query(sql, new MapHandler());
		} catch (SQLException e) {
			log.error("sqlRunner.queryMap error ", e);
		}
		return result;
	}

	/**
	 * 查询count
	 * @param sql
	 * @param dataSource
	 * @return
	 */
	public static Integer queryCount(String sql, DataSource dataSource) {
		QueryRunner sqlRunner = new QueryRunner(dataSource);
		Map<String, Object> result = null;
		try {
			result = sqlRunner.query(sql, new MapHandler());
			Object object = result.values().iterator().next();
			return Integer.valueOf(String.valueOf(object));
		} catch (SQLException e) {
			log.error("sqlRunner.queryCount error ", e);
		}
		return 0;
	}

	/**
	 * 校验是否存在
	 * @param sql
	 * @param dataSource
	 * @return
	 */
	public static boolean checkIsExist(String sql, DataSource dataSource) {
		if("select 1".equalsIgnoreCase(sql.trim())) {
			return true;
		}

		QueryRunner sqlRunner = new QueryRunner(dataSource);
		Object[] result = null;
		try {
			result = sqlRunner.query(sql, new ArrayHandler());
		} catch (SQLException e) {
			log.error("sqlRunner.query error ", e);
		}
		if (null == result || result.length == 0) {
			return false;
		}
		Object obj = result[0];
		if (obj instanceof Long) {
			if ((Long) obj >= 1) {
				return true;
			}
		}
		if (obj instanceof Integer) {
			if ((int) obj >= 1) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 插入
	 * @param sql
	 * @param dataSource
	 */
	public static void insert(String sql, DataSource dataSource) {
		if (StringUtils.isBlank(sql)) {
			log.debug("sqlRunner.insert sql is null");
			return;
		}
		QueryRunner sqlRunner = new QueryRunner(dataSource);
		try {
			sqlRunner.insert(sql, new ScalarHandler<Long>());
		} catch (SQLException e) {
			log.error("sqlRunner.insert error ", e);
		}
	}

	/**
	 * 更新
	 * @param sql
	 * @param dataSource
	 */
	public static void update(String sql, DataSource dataSource) {
		if (StringUtils.isBlank(sql)) {
			log.debug("sqlRunner.update sql is null");
			return;
		}
		QueryRunner sqlRunner = new QueryRunner(dataSource);
		try {
			sqlRunner.update(sql);
		} catch (SQLException e) {
			log.error("sqlRunner.update error ", e);
		}
	}

}
