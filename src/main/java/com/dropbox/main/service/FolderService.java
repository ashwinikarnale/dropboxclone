package com.dropbox.main.service;

import com.dropbox.main.model.Folder;
import com.dropbox.main.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FolderService {

    private final FolderRepository folderRepository;
    private final FileService fileService;

    @Autowired
    public FolderService(FolderRepository folderRepository, FileService fileService) {
        this.folderRepository = folderRepository;
        this.fileService = fileService;
    }

    public void save(Folder folder) {
        folderRepository.save(folder);
    }

    public List<Folder> getFoldersByUserId(int userId) {
        List<Folder> folders = folderRepository.findAllByUserId(userId);
        if (!folders.isEmpty()) {
            return folders;
        } else {
            return null;
        }
    }

    public void saveFileInFolder(MultipartFile file, String folderName) {
        Folder folder = getFolder(folderName);
        fileService.saveFileInFolder(file, folder);
    }

    public List<Folder> getAllDeletedFolders(int userId) {
        List<Folder> folders = folderRepository.findAllDeletedFolders(userId);
        if (!folders.isEmpty()) {
            return folders;
        } else {
            return null;
        }
    }

    public void deleteFolder(Folder folder) {
        folderRepository.delete(folder);
    }

    public Folder getFolder(String folderName) {
        return folderRepository.findFolder(folderName);
    }
}