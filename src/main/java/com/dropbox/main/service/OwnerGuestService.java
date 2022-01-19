package com.dropbox.main.service;

import com.dropbox.main.model.*;
import com.dropbox.main.repository.FileRepository;
import com.dropbox.main.repository.NotificationRepository;
import com.dropbox.main.repository.OwnerGuestRepository;
import com.dropbox.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class OwnerGuestService {

    private final OwnerGuestRepository ownerGuestRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private User user;

    @Autowired
    public OwnerGuestService(OwnerGuestRepository ownerGuestRepository,
                             FileRepository fileRepository,
                             UserRepository userRepository,
                             NotificationRepository notificationRepository,
                             UserService userService) {
        this.userService = userService;
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.ownerGuestRepository = ownerGuestRepository;
        this.notificationRepository = notificationRepository;
    }

    public void save(int userId, int fileId, int[] guestIds, boolean access) {
        this.user=userService.getCurrentUser();
        for (int guestId : guestIds) {
            Optional<File> optionalFile = fileRepository.findById(fileId);
            File file = optionalFile.get();
            OwnerGuest ownerGuest = new OwnerGuest();
            ownerGuest.setUserId(userId);
            ownerGuest.setGuestId(guestId);
            ownerGuest.setFileId(fileId);
            ownerGuest.setAccess(access);
            System.out.println(user.getName());
            Notification notification = new Notification(fileId, guestId, file.getName(), user.getName(), access);
            notificationRepository.save(notification);
            ownerGuestRepository.save(ownerGuest);
        }
    }

    public List<OwnerGuest> findByGuestId(int id) {
        return ownerGuestRepository.getByGuestId(id);
    }

    public List<OwnerGuest> findByUserId(int id) {
        return ownerGuestRepository.getByUserId(id);
    }

    public List<Notification> getNotificationList(int userId) {
        return notificationRepository.getNotificationByUserId(userId);
    }

    public void updateNotification(int fileId) {
        Optional<File> optionalFile = fileRepository.findById(fileId);
        File file = optionalFile.get();
        List<Notification> notifications = notificationRepository.getNotificationByFileId(fileId);
        for (Notification notification : notifications) {
            notification.setFileName(file.getName());
            notificationRepository.save(notification);
        }
    }

    public List<Share> getShareList(List<OwnerGuest> list) {
        List<Share> shareList = new ArrayList<>();
        for (OwnerGuest ownerGuest : list) {
            Optional<File> optionalFile = fileRepository.findById(ownerGuest.getFileId());
            Optional<User> optionalUser = userRepository.findById(ownerGuest.getGuestId());
            if(optionalFile.isPresent() && optionalUser.isPresent()) {
                File file = optionalFile.get();
                User user = optionalUser.get();
                Share share = new Share(ownerGuest.getFileId(), file.getName(), user.getName(), ownerGuest.isAccess(), ownerGuest.getDate());
                shareList.add(share);
            }
        }
        return shareList;
    }
}