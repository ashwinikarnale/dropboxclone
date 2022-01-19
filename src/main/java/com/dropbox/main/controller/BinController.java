package com.dropbox.main.controller;

import com.dropbox.main.model.File;
import com.dropbox.main.model.Folder;
import com.dropbox.main.model.User;
import com.dropbox.main.service.FileService;
import com.dropbox.main.service.FolderService;
import com.dropbox.main.service.StorageService;
import com.dropbox.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileNotFoundException;

@Controller
@RequestMapping("/")
public class BinController {

    private final FileService fileService;
    private final UserService userService;
    private final StorageService storageService;
    private final FolderService folderService;
    private User user;

    @Autowired
    public BinController(FileService fileService, UserService userService, StorageService storageService,
                         FolderService folderService) {
        this.fileService = fileService;
        this.userService = userService;
        this.storageService = storageService;
        this.folderService = folderService;
    }

    @GetMapping("/bin")
    public String getDeletedFiles(Model model) {
        this.user = userService.getCurrentUser();
        model.addAttribute("files", fileService.getDeletedFiles(this.user.getId()));
        model.addAttribute("deletedFolders", folderService.getAllDeletedFolders(this.user.getId()));
        return "bin";
    }

    @GetMapping("/bin/delete/file{fileId}")
    public String delete(@PathVariable("fileId") int fileId) throws FileNotFoundException {
        File file = fileService.delete(fileId);
        storageService.deleteFile(file.getId() + "_" + file.getName());
        return "redirect:/bin";
    }

    @GetMapping("/restore/file{fileId}")
    public String restoreFile(@PathVariable("fileId") int fileId) throws FileNotFoundException {
        File file = fileService.getFile(fileId);
        file.setDeleted(false);
        fileService.saveFile(file);
        return "redirect:/";
    }

    @GetMapping("/restore/{folderName}")
    public String restoreFolder(@PathVariable("folderName") String folderName) {
        Folder folder = folderService.getFolder(folderName);
        folder.setDeleted(false);
        folderService.save(folder);
        return "redirect:/";
    }

    @GetMapping("/bin/delete/{folderName}")
    public String removeFolder(@PathVariable("folderName") String folderName) {
        Folder folder = folderService.getFolder(folderName);
        fileService.deleteAll(folder.getFiles());
        folderService.deleteFolder(folder);
        return "redirect:/bin";
    }
}