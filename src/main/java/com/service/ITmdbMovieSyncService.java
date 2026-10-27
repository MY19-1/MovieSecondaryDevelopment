package com.service;

import com.service.dto.TmdbSyncResult;

public interface ITmdbMovieSyncService {
    TmdbSyncResult syncNowPlayingAndUpcoming() throws Exception;
    boolean refreshTmdbCast(long movieId, long tmdbMovieId) throws Exception;
}
