package com.dropbox.main.model;

import java.util.Date;

public class Share {

    private int fileId;
    private String filename;
    private String shareTo;
    private boolean access;
    private Date date;

    public Share(int fileId, String filename, String shareTo, boolean access, Date date) {
        this.fileId = fileId;
        this.filename = filename;
        this.shareTo = shareTo;
        this.access = access;
        this.date = date;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getShareTo() {
        return shareTo;
    }

    public void setShareTo(String shareTo) {
        this.shareTo = shareTo;
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}