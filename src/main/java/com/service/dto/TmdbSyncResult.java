package com.service.dto;

import java.util.ArrayList;
import java.util.List;

public class TmdbSyncResult {

    private int inserted;
    private int updated;
    private int skipped;
    private int archived;
    private final List<String> errors = new ArrayList<String>();

    public int getInserted() {
        return inserted;
    }

    public void incrementInserted() {
        inserted++;
    }

    public int getUpdated() {
        return updated;
    }

    public void incrementUpdated() {
        updated++;
    }

    public int getSkipped() {
        return skipped;
    }

    public void incrementSkipped() {
        skipped++;
    }

    public int getArchived() {
        return archived;
    }

    public void incrementArchived() {
        archived++;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addError(String error) {
        errors.add(error);
    }
}
