package com.Hello.fileExplorer.service;

import java.time.Instant;
import java.util.List;

public class FolderMetadata {
    private Instant created;
    private Instant modified;
    private String sortBy;
    private List<String> subFolders;

    // Getters and setters
    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getModified() {
        return modified;
    }

    public void setModified(Instant modified) {
        this.modified = modified;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public List<String> getSubFolders() {
        return subFolders;
    }

    public void setSubFolders(List<String> subFolders) {
        this.subFolders = subFolders;
    }
}
