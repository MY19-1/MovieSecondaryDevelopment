package com.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EmailCodeInfoService {  //这里只是写了一个储存的容器，还没有真正的把数据存储进去
    private static final Map<String,EmailCodeInfoUtil> codeMap = new ConcurrentHashMap<>() ;
    //ConcurrentMap:线程安全的键值容器，能同时处理同多用户访的请求

    //保存验证码
    public static void saveCode(String email, String code) {
        long expireTime = System.currentTimeMillis() + 5 * 60 * 1000L;
        //计算过期时间

        EmailCodeInfoUtil info = new EmailCodeInfoUtil(code, expireTime);
        //把验证码和过期时间封装起来

        codeMap.put(email, info);
        //存到map里面
    }

    //验证验证码
    public static boolean verifyCode(String email, String inputCode) {
        EmailCodeInfoUtil info = codeMap.get(email);

        if (info == null) {
            return false;
        }

        if (System.currentTimeMillis() >= info.getExpireTime()) {
            codeMap.remove(email);
            return false;
        }

        if (!info.getCode().equals(inputCode)) {
            return false;
        }

        codeMap.remove(email);
        return true;
    }
}
