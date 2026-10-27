
package com.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.entity.Movie;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class TmdbMovieConverter {

    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";
    private static final int DATABASE_CN_NAME_FIELD_LENGTH = 30;
    private static final int DATABASE_FG_NAME_FIELD_LENGTH = 50;
    private static final int DATABASE_ACTOR_FIELD_LENGTH = 100;
    private static final int DATABASE_DIRECTOR_FIELD_LENGTH = 20;
    private static final int DATABASE_DETAIL_FIELD_LENGTH = 350;
    private static final int DATABASE_DURATION_FIELD_LENGTH = 20;
    private static final int DATABASE_TYPE_FIELD_LENGTH = 20;
    private static final int DATABASE_COUNTRY_FIELD_LENGTH = 20;
    private static final int DATABASE_PICTURE_FIELD_LENGTH = 100;

    /**
     * 仅转换搜索接口可直接提供的字段。
     * 搜索结果不包含数据库中的演员、导演、片长、类型和制片地区字段。
     */
    public static Movie convert(JSONObject searchResult) {
        if (searchResult == null) {
            throw new IllegalArgumentException("TMDB 搜索结果不能为空");
        }

        Movie movie = new Movie();
        movie.setMovie_cn_name(limitText(text(searchResult, "title"), DATABASE_CN_NAME_FIELD_LENGTH));
        movie.setMovie_fg_name(limitText(text(searchResult, "original_title"), DATABASE_FG_NAME_FIELD_LENGTH));
        movie.setMovie_detail(limitText(text(searchResult, "overview"), DATABASE_DETAIL_FIELD_LENGTH));
        movie.setMovie_score(searchResult.getFloatValue("vote_average"));
        movie.setMovie_commentCount(searchResult.getLongValue("vote_count"));
        movie.setReleaseDate(parseDate(searchResult.getString("release_date")));
        movie.setMovie_picture(buildImageUrl(searchResult.getString("poster_path")));
        return movie;
    }
    public static Movie convert(JSONObject searchResult,
                               JSONObject detailResult,
                               JSONObject creditsResult) {
        if (detailResult == null) {
            throw new IllegalArgumentException("TMDB 详情结果不能为空");
        }
        if (creditsResult == null) {
            throw new IllegalArgumentException("TMDB 演职员结果不能为空");
        }

        Movie movie = convert(searchResult);
        movie.setMovie_duration(formatRuntime(detailResult.getInteger("runtime")));
        movie.setMovie_type(limitText(joinNames(detailResult.getJSONArray("genres")), DATABASE_TYPE_FIELD_LENGTH));
        movie.setMovie_country(findCountry(detailResult));
        movie.setMovie_director(limitText(
                joinCrewByJob(creditsResult.getJSONArray("crew"), "Director"),
                DATABASE_DIRECTOR_FIELD_LENGTH));
        JSONArray cast = creditsResult.getJSONArray("cast");
        movie.setMovie_actor(joinCast(cast));
        movie.setMovie_tmdb_cast(buildTmdbCastJson(cast));

        validateForInsert(movie);
        return movie;
    }

    public static String convertCast(JSONObject creditsResult) {
        if (creditsResult == null) {
            throw new IllegalArgumentException("TMDB 演职员结果不能为空");
        }
        return buildTmdbCastJson(creditsResult.getJSONArray("cast"));
    }

    private static void validateForInsert(Movie movie) {
        requireText(movie.getMovie_cn_name(), "电影中文名");
        requireText(movie.getMovie_actor(), "演职人员");
        requireText(movie.getMovie_director(), "导演");
        requireText(movie.getMovie_detail(), "电影详情");
        requireText(movie.getMovie_duration(), "电影时长");
        requireText(movie.getMovie_type(), "电影类型");
        requireText(movie.getMovie_country(), "制片地区");
        requireText(movie.getMovie_picture(), "电影海报");

        if (movie.getReleaseDate() == null) {
            throw new IllegalArgumentException("上映日期不能为空");
        }
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
    }

    private static String text(JSONObject object, String fieldName) {
        String value = object.getString(fieldName);
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private static Date parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return Date.valueOf(value.trim());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private static String findCountry(JSONObject detailResult) {
        String country = joinNamesWithinLimit(
                detailResult.getJSONArray("production_countries"),
                DATABASE_COUNTRY_FIELD_LENGTH);
        if (country != null) {
            return country;
        }

        JSONArray originCountries = detailResult.getJSONArray("origin_country");
        return joinCodesWithinLimit(originCountries, DATABASE_COUNTRY_FIELD_LENGTH);
    }

    private static String joinCodesWithinLimit(JSONArray items, int maxLength) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        List<String> codes = new ArrayList<String>();
        int usedLength = 0;
        for (int index = 0; index < items.size(); index++) {
            String code = limitText(items.getString(index), maxLength);
            if (code == null) {
                continue;
            }

            int separatorLength = codes.isEmpty() ? 0 : 1;
            if (usedLength + separatorLength + code.length() > maxLength) {
                break;
            }
            codes.add(code);
            usedLength += separatorLength + code.length();
        }
        return codes.isEmpty() ? null : String.join(",", codes);
    }

    private static String limitText(String value, int maxLength) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength);
    }

    private static String buildImageUrl(String posterPath) {
        if (posterPath == null || posterPath.trim().isEmpty()) {
            return null;
        }

        String path = posterPath.trim();
        String imageUrl = path.startsWith("http") ? path : IMAGE_BASE_URL + path;
        if (imageUrl.length() > DATABASE_PICTURE_FIELD_LENGTH) {
            throw new IllegalArgumentException("电影海报地址超过数据库列长度");
        }
        return imageUrl;
    }

    private static String formatRuntime(Integer runtime) {
        if (runtime == null || runtime <= 0) {
            return null;
        }

        String value = runtime + "分钟";
        return value.length() > DATABASE_DURATION_FIELD_LENGTH ? null : value;
    }

    private static String joinNames(JSONArray items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        List<String> names = new ArrayList<String>();
        for (int index = 0; index < items.size(); index++) {
            JSONObject item = items.getJSONObject(index);
            if (item != null) {
                String name = text(item, "name");
                if (name != null) {
                    names.add(name);
                }
            }
        }
        return names.isEmpty() ? null : String.join(",", names);
    }

    private static String joinNamesWithinLimit(JSONArray items, int maxLength) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        List<String> names = new ArrayList<String>();
        int usedLength = 0;
        for (int index = 0; index < items.size(); index++) {
            JSONObject item = items.getJSONObject(index);
            if (item == null) {
                continue;
            }

            String name = text(item, "name");
            if (name == null) {
                continue;
            }

            int separatorLength = names.isEmpty() ? 0 : 1;
            if (usedLength + separatorLength + name.length() > maxLength) {
                break;
            }

            names.add(name);
            usedLength += separatorLength + name.length();
        }
        return names.isEmpty() ? null : String.join(",", names);
    }

    private static String joinCrewByJob(JSONArray crew, String job) {
        if (crew == null || crew.isEmpty()) {
            return null;
        }

        List<String> names = new ArrayList<String>();
        for (int index = 0; index < crew.size(); index++) {
            JSONObject person = crew.getJSONObject(index);
            if (person != null && job.equals(person.getString("job"))) {
                String name = text(person, "name");
                if (name != null && !names.contains(name)) {
                    names.add(name);
                }
            }
        }
        return names.isEmpty() ? null : String.join(",", names);
    }

    private static String buildTmdbCastJson(JSONArray cast) {
        if (cast == null || cast.isEmpty()) {
            return null;
        }

        JSONArray result = new JSONArray();
        int maxLength = DATABASE_ACTOR_FIELD_LENGTH;
        int usedLength = 0;
        for (int index = 0; index < cast.size(); index++) {
            JSONObject person = cast.getJSONObject(index);
            if (person == null) {
                continue;
            }

            String name = text(person, "name");
            String character = text(person, "character");
            if (name == null) {
                continue;
            }

            JSONObject item = new JSONObject();
            item.put("person_id", person.getLong("id"));
            item.put("name", name);
            item.put("character", character);
            item.put("profile_path", buildImageUrl(person.getString("profile_path")));

            String entry = character == null ? name : name + ":" + character;
            int separatorLength = result.isEmpty() ? 0 : 1;
            if (usedLength + separatorLength + entry.length() > maxLength) {
                break;
            }

            result.add(item);
            usedLength += separatorLength + entry.length();
        }
        return result.isEmpty() ? null : result.toJSONString();
    }

    private static String joinCast(JSONArray cast) {
        if (cast == null || cast.isEmpty()) {
            return null;
        }

        List<String> names = new ArrayList<String>();
        int usedLength = 0;
        for (int index = 0; index < cast.size(); index++) {
            JSONObject person = cast.getJSONObject(index);
            if (person == null) {
                continue;
            }

            String name = text(person, "name");
            String character = text(person, "character");
            if (name == null) {
                continue;
            }

            String entry = character == null ? name : name + ":" + character;
            int separatorLength = names.isEmpty() ? 0 : 1;
            if (usedLength + separatorLength + entry.length() > DATABASE_ACTOR_FIELD_LENGTH) {
                break;
            }

            names.add(entry);
            usedLength += separatorLength + entry.length();
        }
        return names.isEmpty() ? null : String.join("/", names);
    }
}
