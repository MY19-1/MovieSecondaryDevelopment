package com.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TmdbApiUtil {
    public static String get(String urlString) throws Exception {

        // 1. 根据请求地址创建 URL 对象
        URL url = new URL(urlString);

        // 2. 建立 HTTP 连接
        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        // 3. 设置请求方式和超时时间
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);

        try {
            // 4. 获取服务器返回的状态码
            int statusCode = connection.getResponseCode();

            // 5. 根据状态码选择响应流
            InputStream inputStream;

            if (statusCode >= 200 && statusCode < 300) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();

                if (inputStream == null) {
                    throw new RuntimeException(
                            "TMDB 请求失败，HTTP 状态码：" + statusCode);
                }
            }

            // 6. 读取服务器返回的内容
            StringBuilder response = new StringBuilder();
            //StringBuilder：进行拼接
            try (BufferedReader reader = new BufferedReader(
                    //BufferedReader:逐行读取
                    new InputStreamReader(
                            inputStream, StandardCharsets.UTF_8))) {

                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            // 7. 返回完整的响应文本
            if (statusCode < 200 || statusCode >= 300) {
                throw new RuntimeException(
                        "TMDB 请求失败，HTTP 状态码："
                                + statusCode + "，响应：" + response);
            }

            return response.toString();

        } finally {
            // 8. 释放 HTTP 连接
            connection.disconnect();
        }
    }
}
