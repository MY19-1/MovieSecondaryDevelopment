package com.util;

import java.util.Random;

//生成六位数字验证码
public class VerifyCodeUtil {
    public static String generateCode(){
        String code = "";
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int num = random.nextInt(10);
            code += num;
        }
        return code;
    }
}
