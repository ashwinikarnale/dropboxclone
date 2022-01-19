package com.dropbox.main.repository;

import com.dropbox.main.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    @Query(value = "select * from notification n where n.user_id = ?1", nativeQuery = true)
    List<Notification> getNotificationByUserId(int userId);

    @Query(value = "select * from notification n where n.file_id = ?1", nativeQuery = true)
    List<Notification> getNotificationByFileId(int fileId);
}