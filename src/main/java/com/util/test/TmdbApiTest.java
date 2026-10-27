package com.util.test;

import com.util.TmdbApiUtil;

public class TmdbApiTest {

    public static void main(String[] args) throws Exception {

        String url =
                "https://api.themoviedb.org/3/search/movie"
                        + "?api_key=047bbd4cbfd81f2b5fadf8894047ebf3"
                        + "&query=Venom"
                        + "&language=zh-CN";

        String json = TmdbApiUtil.get(url);

        System.out.println(json);


        
    }
}
