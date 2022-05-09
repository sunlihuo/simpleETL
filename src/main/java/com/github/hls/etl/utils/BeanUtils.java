package com.github.hls.etl.utils;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;

import java.util.List;
import java.util.Map;

/**
 * 对象拷贝工具
 */
public class BeanUtils {

    /** Bean copier cache */
    private static Map<String, BeanCopier> BEAN_COPIER_CACHE = Maps.newConcurrentMap();

    /**
     * 属性拷贝
     *
     * @param source the source
     * @param target the target
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null) {
            return;
        }
        BeanCopier copier = getBeanCopier(source.getClass(), target.getClass());
        copier.copy(source, target, null);
    }

    /**
     * 属性拷贝
     *
     * @param source    the source
     * @param target    the target
     * @param converter the converter
     */
    public static void copyProperties(Object source, Object target, Converter converter) {
        if (source == null) {
            return;
        }
        BeanCopier copier = getBeanCopier(source.getClass(), target.getClass());
        copier.copy(source, target, converter);
    }

    /**
     * 属性拷贝
     *
     * @param <T>         the type parameter
     * @param source      the source
     * @param targetClass the target class
     * @return the t
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        T t;
        try {
            t = targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Create new instance of " + targetClass + " failed: " + e.getMessage());
        }
        copyProperties(source, t);
        return t;
    }

    /**
     * 属性拷贝
     *
     * @param source      数组
     * @param targetClass class
     * @return 列表 the list
     */
    public static List copyList(Object[] source, Class targetClass) {
        if (source == null) {
            return null;
        }
        List target = Lists.newArrayList();
        for (Object obj : source) {
            target.add(copyProperties(obj, targetClass));
        }
        return target;
    }

    /**
     * 属性拷贝
     *
     * @param source      list
     * @param targetClass class
     * @return 列表 the list
     */
    public static List copyList(List<?> source, Class targetClass) {
        if (source == null) {
            return null;
        }
        List target = Lists.newArrayList();
        for (Object obj : source) {
            target.add(copyProperties(obj, targetClass));
        }
        return target;
    }

    /**
     * Gets bean copier.
     *
     * @param sourceClass the source class
     * @param targetClass the target class
     * @return bean copier the bean copier
     */
    private static BeanCopier getBeanCopier(Class sourceClass, Class targetClass) {
        String beanKey = generateKey(sourceClass, targetClass);
        BeanCopier copier;
        if (!BEAN_COPIER_CACHE.containsKey(beanKey)) {
            copier = BeanCopier.create(sourceClass, targetClass, false);
            BEAN_COPIER_CACHE.put(beanKey, copier);
        } else {
            copier = BEAN_COPIER_CACHE.get(beanKey);
        }
        return copier;
    }

    /**
     * Generate key
     * Generate key description.
     *
     * @param class1 the class 1
     * @param class2 the class 2
     * @return string the string
     */
    private static String generateKey(Class<?> class1, Class<?> class2) {
        return class1.toString() + class2.toString();
    }
}