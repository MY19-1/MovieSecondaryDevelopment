
package com.util.test;

import com.util.EmailCodeInfoService;
import com.util.MailUtil;
import com.util.VerifyCodeUtil;

public class MailTest {

    public static void main(String[] args) {
        String code = VerifyCodeUtil.generateCode();
        String content = "您的验证码为：" + code + "\n" + "注意：验证码五分钟内有效";
        String email = "13615996167@163.com";

        try {
            MailUtil.sendMail(
                    email,
                    "鹰眼电影账户注册验证码",
                    content
            );

            System.out.println("邮件发送请求已成功完成！");
        } catch (Exception e) {
            System.out.println("邮件发送失败：");
            e.printStackTrace();
        }

        EmailCodeInfoService.saveCode(email, code);
        EmailCodeInfoService.verifyCode(email, code);
    }
}
