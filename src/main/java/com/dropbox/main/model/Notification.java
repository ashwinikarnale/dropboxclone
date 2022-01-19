package com.dropbox.main.model;

import javax.persistence.*;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "entity_id_seq"
    )
    @SequenceGenerator(
            name = "entity_id_seq",
            sequenceName = "global_id_sequence",
            allocationSize = 1
    )
    @Column(name = "id")
    private int id;

    @Column(name = "file_id")
    private int fileId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "filename")
    private String fileName;

    @Column(name = "sharedby")
    private String sharedBy;

    @Column(name = "access")
    private boolean access;

    public Notification(int fileId, int userId, String fileName, String sharedBy, boolean access) {
        this.fileId = fileId;
        this.userId = userId;
        this.fileName = fileName;
        this.sharedBy = sharedBy;
        this.access = access;
    }

    public Notification() {
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSharedBy() {
        return sharedBy;
    }

    public void setSharedBy(String sharedBy) {
        this.sharedBy = sharedBy;
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}