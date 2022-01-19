package com.dropbox.main.model;

import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "owner_guest")
public class OwnerGuest {

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

    @Column(name = "user_id")
    private int userId;

    @Column(name = "file_id")
    private int fileId;

    @Column(name = "guest_id")
    private int guestId;

    @Column(name = "access")
    private boolean access;

    @UpdateTimestamp
    @Column(name = "date")
    private Timestamp date;

    public OwnerGuest() {
    }

    public OwnerGuest(int userId, int fileId, int guestId, boolean access) {
        this.userId = userId;
        this.fileId = fileId;
        this.guestId = guestId;
        this.access = access;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}