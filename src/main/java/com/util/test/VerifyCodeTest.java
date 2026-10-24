package com.util.test;

import com.util.VerifyCodeUtil;

public class VerifyCodeTest {
    public static void main(String[] args) {
        String code = VerifyCodeUtil.generateCode();
        System.out.println(code);
    }
}
