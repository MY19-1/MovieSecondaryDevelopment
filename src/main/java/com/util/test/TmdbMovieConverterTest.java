package com.util.test;

import com.service.ITmdbMovieSyncService;
import com.service.dto.TmdbSyncResult;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TmdbMovieConverterTest {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("spring.xml");
        try {
            ITmdbMovieSyncService syncService =
                    context.getBean(ITmdbMovieSyncService.class);
            TmdbSyncResult result = syncService.syncNowPlayingAndUpcoming();
            System.out.println("TMDB院线同步完成");
            System.out.println("新增=" + result.getInserted());
            System.out.println("更新=" + result.getUpdated());
            System.out.println("跳过=" + result.getSkipped());
            System.out.println("下架旧片=" + result.getArchived());
            System.out.println("错误=" + result.getErrors());
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            context.close();
        }
    }
}
