package com.example.securestoragesystem.Models;


public class FolderFile {
    String fileName, userId, folderId, fileId, fileType, fileDestination, uri;
    long fileSize;

    public FolderFile(){}

    public FolderFile(String fileName, long fileSize, String userId, String folderId, String fileId, String fileType, String uri) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.userId = userId;
        this.folderId = folderId;
        this.fileId = fileId;
        this.fileType = fileType;
        this.uri = uri;
    }

    public String getFilename() {
        return fileName;
    }

    public void setFilename(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileDestination() {
        return fileDestination;
    }

    public void setFileDestination(String fileDestination) {
        this.fileDestination = fileDestination;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
