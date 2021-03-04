package com.manager.traffic.dao;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisOperating {
    public static void set(String key,String value){
        Jedis jedis = RedisUtils.getJedis();
        long t1 = System.currentTimeMillis();
        jedis.set(key,value);
        long t2 = System.currentTimeMillis();
        System.out.println(t2- t1);
        RedisUtils.returnResource(jedis);
    }

    public static Object get(String key){

        Jedis jedis = RedisUtils.getJedis();
        Object value = jedis.get(key);
        RedisUtils.returnResource(jedis);
        return value;
    }
    //将key对应的value+1
    public static long incr(String key){
        Jedis jedis = RedisUtils.getJedis();
        long l = jedis.incr(key);
        RedisUtils.returnResource(jedis);
        return l;
    }

    //将key对应的value-1
    public static long decr(String key){
        Jedis jedis = RedisUtils.getJedis();
        long l = jedis.decr(key);
        RedisUtils.returnResource(jedis);
        return l;
    }

    public static void hset(String key, Map<String, String> hash){
        Jedis jedis = RedisUtils.getJedis();
        jedis.hmset(key,hash);
        RedisUtils.returnResource(jedis);
    }

    public static Object hget(String key, String hkey){
        Jedis jedis = RedisUtils.getJedis();
        Object s = jedis.hget(key, hkey);
        RedisUtils.returnResource(jedis);
        return s;
    }
    public static Object hkeys(String key){
        Jedis jedis = RedisUtils.getJedis();
        Object s = jedis.hkeys(key);
        return s;
    }

    public static long expire(String key,int second){
        Jedis jedis = RedisUtils.getJedis();
        long l = jedis.expire(key,second);
        RedisUtils.returnResource(jedis);
        return l;
    }

    public static Map<String, String> hgetAll(String key){
        Jedis jedis = RedisUtils.getJedis();
        Map<String, String> map = new HashMap<>();
        map = jedis.hgetAll(key);
        RedisUtils.returnResource(jedis);
        return map;
    }

    public static void del(String key){
        Jedis jedis = RedisUtils.getJedis();
        jedis.del(key);
        RedisUtils.returnResource(jedis);
    }
//添加一个list
    public static void lpus(String key,String... value){
        Jedis jedis = RedisUtils.getJedis();
        jedis.lpush(key,value);
        RedisUtils.returnResource(jedis);
    }
//获取list对应区间的元素
    public static List<String> lrange(String key,int start,int end){
        Jedis jedis = RedisUtils.getJedis();
        List<String> list = jedis.lrange(key, start, end);
        RedisUtils.returnResource(jedis);
        return list;
    }
//删除指定元素val个数num
    public static void lrem(String key,String value){
        Jedis jedis = RedisUtils.getJedis();
        jedis.lrem(key, 0, value);
        RedisUtils.returnResource(jedis);
    }
//获取对应list长度
    public static long size(String key){
        Jedis jedis = RedisUtils.getJedis();
        long size = jedis.llen(key);
        RedisUtils.returnResource(jedis);
        return size;
    }
    public static boolean exits(String key){
        Jedis jedis = RedisUtils.getJedis();
        boolean exits = jedis.exists(key);
        RedisUtils.returnResource(jedis);
        return exits;
    }

//    public static void main(String[] args) {
//        long t1 = System.currentTimeMillis();
//        String s = (String)get("test");
//        System.out.println(s);
//        long t2 = System.currentTimeMillis();
//        System.out.println(t2- t1);
//    }
}
