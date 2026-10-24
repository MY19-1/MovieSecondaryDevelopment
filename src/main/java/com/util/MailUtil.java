
package com.util;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;

public class MailUtil {

    public static void sendMail(String to, String subject, String content)
            throws Exception {

        // 1. 读取 resources 目录中的邮件配置
        Properties props = new Properties();

        try (InputStream in = MailUtil.class
                .getClassLoader()
                .getResourceAsStream("mail.properties")) {

            if (in == null) {
                throw new IllegalStateException(
                        "没有找到 mail.properties，请检查 resources 目录");
            }

            props.load(in);
        }

        // 2. 创建邮件会话，并使用授权码进行身份验证
        final String username = props.getProperty("mail.smtp.username");
        final String password = props.getProperty("mail.smtp.password");
        final String from = props.getProperty("mail.smtp.from");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // 3. 组织邮件内容
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipient(
                Message.RecipientType.TO,
                new InternetAddress(to)
        );
        message.setSubject(subject, "UTF-8");
        message.setText(content, "UTF-8");
        message.setSentDate(new Date());

        // 4. 连接 SMTP 服务器并发送
        Transport.send(message);
    }
}
