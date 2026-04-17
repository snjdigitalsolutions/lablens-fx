package com.snjdigitalsolutions.lablensfx.orm;

import java.time.Instant;

public class FileSystemObject {

    private Instant modifiedTime;
    private int permission;
    private String fileType;
    private String parentPath;
    private String fileName;
    private long fileSize;
    private boolean trackFile;

    public Instant getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Instant modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isTrackFile() {
        return trackFile;
    }

    public void setTrackFile(boolean trackFile) {
        this.trackFile = trackFile;
    }
}
