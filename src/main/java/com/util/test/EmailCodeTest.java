package com.util.test;

import com.util.EmailCodeInfoService;

public class EmailCodeTest {
    public static void main(String[] args) {
        String email = "13615996167@163.com";
        String code = "027491";

        // 1. 保存验证码
        EmailCodeInfoService.saveCode(email, code);
        System.out.println("验证码已保存");

        // 2. 测试正确验证码
        boolean result1 = EmailCodeInfoService.verifyCode(email, code);
        System.out.println("正确验证码校验结果：" + result1);

        // 3. 再次校验同一个验证码
        boolean result2 = EmailCodeInfoService.verifyCode(email, code);
        System.out.println("重复使用验证码的结果：" + result2);
        }

}
