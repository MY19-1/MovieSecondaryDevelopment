package com.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.entity.Movie;
import com.service.IMovieService;
import com.service.ITmdbMovieSyncService;
import com.service.dto.TmdbSyncResult;
import com.mapper.MovieMapper;
import com.util.TmdbApiUtil;
import com.util.TmdbMovieConverter;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class TmdbMovieSyncServiceImp implements ITmdbMovieSyncService {

    private static final String API_KEY = "047bbd4cbfd81f2b5fadf8894047ebf3";
    private static final String API_BASE_URL = "https://api.themoviedb.org/3";
    private static final int MAX_PAGES_PER_LIST = 3;

    @Resource
    private IMovieService movieService;
    @Resource
    private MovieMapper movieMapper;

    @Override
    public TmdbSyncResult syncNowPlayingAndUpcoming() throws Exception {
        TmdbSyncResult result = new TmdbSyncResult();
        Map<Long, Integer> tmdbMovies = new LinkedHashMap<Long, Integer>();
        Set<String> cinemaNames = new HashSet<String>();
        Set<String> syncedNames = new HashSet<String>();
        loadMovieList("now_playing", 1, tmdbMovies, cinemaNames);
        loadMovieList("upcoming", 0, tmdbMovies, cinemaNames);

        if (tmdbMovies.isEmpty()) {
            throw new IllegalStateException("TMDB 未返回院线上映或即将上映电影");
        }

        for (Map.Entry<Long, Integer> entry : tmdbMovies.entrySet()) {
            syncOneMovie(entry.getKey(), entry.getValue(), result, syncedNames);
        }

        archiveOldMovies(cinemaNames, result);
        return result;
    }

    @Override
    public boolean refreshTmdbCast(long movieId, long tmdbMovieId) throws Exception {
        JSONObject creditsResult = JSONObject.parseObject(TmdbApiUtil.get(
                API_BASE_URL + "/movie/" + tmdbMovieId
                        + "/credits?api_key=" + API_KEY
                        + "&language=" + encode("zh-CN")));
        String castJson = TmdbMovieConverter.convertCast(creditsResult);
        if (castJson == null) {
            return false;
        }

        Integer updatedRows = movieMapper.updateTmdbCast(movieId, castJson);
        return updatedRows != null && updatedRows > 0;
    }

    private void loadMovieList(String endpoint, int state,
                               Map<Long, Integer> tmdbMovies,
                               Set<String> cinemaNames) throws Exception {
        for (int page = 1; page <= MAX_PAGES_PER_LIST; page++) {
            String url = API_BASE_URL + "/movie/" + endpoint
                    + "?api_key=" + API_KEY
                    + "&language=" + URLEncoder.encode("zh-CN", StandardCharsets.UTF_8.name())
                    + "&region=" + URLEncoder.encode("CN", StandardCharsets.UTF_8.name())
                    + "&page=" + page;
            JSONObject response = JSONObject.parseObject(TmdbApiUtil.get(url));
            JSONArray results = response.getJSONArray("results");
            if (results == null || results.isEmpty()) {
                break;
            }

                for (int index = 0; index < results.size(); index++) {
                JSONObject item = results.getJSONObject(index);
                if (item != null && item.getLongValue("id") > 0) {
                    long tmdbMovieId = item.getLongValue("id");
                    addCinemaName(cinemaNames, item.getString("title"));
                    addCinemaName(cinemaNames, item.getString("original_title"));
                    if (state == 1 || !tmdbMovies.containsKey(tmdbMovieId)) {
                        tmdbMovies.put(tmdbMovieId, state);
                    }
                }
            }

            Integer totalPages = response.getInteger("total_pages");
            if (totalPages == null || page >= totalPages) {
                break;
            }
        }
    }

    private void syncOneMovie(long tmdbMovieId, int state, TmdbSyncResult result,
                              Set<String> syncedNames) {
        try {
            JSONObject detailResult = JSONObject.parseObject(TmdbApiUtil.get(
                    API_BASE_URL + "/movie/" + tmdbMovieId
                            + "?api_key=" + API_KEY
                            + "&language=" + encode("zh-CN")));
            JSONObject creditsResult = JSONObject.parseObject(TmdbApiUtil.get(
                    API_BASE_URL + "/movie/" + tmdbMovieId
                            + "/credits?api_key=" + API_KEY
                            + "&language=" + encode("zh-CN")));

            Movie movie = TmdbMovieConverter.convert(
                    detailResult, detailResult, creditsResult);

            Movie existingMovie = movieService.findMovieByName(movie.getMovie_cn_name());
            if (existingMovie == null) {
                Integer insertedRows = movieService.addMovie(movie);
                if (insertedRows == null || insertedRows <= 0) {
                    throw new IllegalStateException("新增失败：" + movie.getMovie_cn_name());
                }
                existingMovie = movieService.findMovieByName(movie.getMovie_cn_name());
                result.incrementInserted();
            } else {
                movie.setMovie_id(existingMovie.getMovie_id());
                Integer updatedRows = movieService.updateMovie(movie);
                if (updatedRows == null || updatedRows <= 0) {
                    throw new IllegalStateException("更新失败：" + movie.getMovie_cn_name());
                }
                result.incrementUpdated();
            }

            if (existingMovie == null
                    || movieMapper.updateMovieState(existingMovie.getMovie_id(), state) <= 0) {
                throw new IllegalStateException("更新状态失败：" + movie.getMovie_cn_name());
            }
            syncedNames.add(movie.getMovie_cn_name());
        } catch (Exception exception) {
            result.incrementSkipped();
            result.addError(tmdbMovieId + ": " + exception.getMessage());
        }
    }

    private void addCinemaName(Set<String> cinemaNames, String name) {
        if (name != null && !name.trim().isEmpty()) {
            cinemaNames.add(name.trim());
        }
    }

    private void archiveOldMovies(Set<String> cinemaNames, TmdbSyncResult result) {
        for (Movie movie : movieService.findAllMovies(1)) {
            if (!cinemaNames.contains(movie.getMovie_cn_name())) {
                if (movieMapper.updateMovieState(movie.getMovie_id(), 0) > 0) {
                    result.incrementArchived();
                }
            }
        }
    }

    private static String encode(String value) throws Exception {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
    }
}
