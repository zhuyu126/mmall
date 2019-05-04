package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TokenCache {
    //首先声明一个日志
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    public static final String TOKEN_PREFIX = "token";

    //CacheBuilder：设置LocalCache的相关参数，并创建LocalCache实例
    //initialCapacity(1000),设置缓存的初始化容量
    //maximumSize(),设置缓存的最大容量，如果超过这个容量，就会通过LRU算法来移除缓存项
    //expireAfterAccess(12, TimeUnit.HOURS),缓存的有效期是12个小时
    //CacheLoader,默认的数据加载实现，当调用get()取值的时候，如果key没有对应的值时，就调用这个load()方法进行加载
    //guava里的
    private static LoadingCache<String, String> localchche = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000)
            .expireAfterAccess(12, TimeUnit.HOURS).build( new CacheLoader<String, String>() {
                //默认的数据加载实现，当调用get取值的时候，如果key没有对应的值，就调用这个方法进行加载
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    public static void setKey(String key, String value) {
        localchche.put(key, value);
    }

    //传入一个key，返回value
    public static String getKey(String key){
        String value = null;
        try{
            value = localchche.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        }catch(Exception e){
            logger.error("localCache get error", e);
        }
        return null;
    }
}
