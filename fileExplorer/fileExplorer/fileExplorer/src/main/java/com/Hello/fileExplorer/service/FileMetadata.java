package com.Hello.fileExplorer.service;

public class FileMetadata {
    private String sort;
    private String created;
    private String modified;

    public FileMetadata() {} // Needed for Jackson

    public FileMetadata(String sort, String created, String modified) {
        this.sort = sort;
        this.created = created;
        this.modified = modified;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
}
