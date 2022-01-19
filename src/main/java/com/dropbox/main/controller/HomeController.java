package com.dropbox.main.controller;

import com.dropbox.main.model.*;
import com.dropbox.main.repository.FileRepository;
import com.dropbox.main.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/")
public class HomeController {

    private final FileService fileService;
    private final UserService userService;
    private final OwnerGuestService ownerGuestService;
    private final StorageService storageService;
    private final FolderService folderService;
    private final FileRepository fileRepository;
    JavaMailSender javaMailSender = getJavaMailSender();
    private int fileId;
    private String url;
    private Set<String> emailsSelected = new HashSet<>();
    private User user;

    @Autowired
    public HomeController(FileService fileService,
                          UserService userService,
                          StorageService storageService,
                          FolderService folderService,
                          OwnerGuestService ownerGuestService,
                          FileRepository fileRepository) {
        this.fileRepository = fileRepository;
        this.ownerGuestService = ownerGuestService;
        this.userService = userService;
        this.fileService = fileService;
        this.storageService = storageService;
        this.folderService = folderService;
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("narasimha8186094547@gmail.com");
        mailSender.setPassword("fubwbpumstgwnxef");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @ModelAttribute
    public void addModelAttributes(Model model) {
        model.addAttribute("edit", "false");
        model.addAttribute("createFolder", "false");
    }

    @GetMapping("/")
    public String getHome(Model model) {
        this.user = userService.getCurrentUser();
        if (this.user == null) {
            return "main";
        } else {
            model.addAttribute("files", fileService.getFiles(this.user.getId()));
            model.addAttribute("folders", folderService.getFoldersByUserId(this.user.getId()));
            model.addAttribute("createFolder", "false");
            return "home";
        }
    }

    @PostMapping("/folder")
    public String createFolder(@ModelAttribute("folder") Folder folder) {
        folder.setUser(this.user);
        folderService.save(folder);
        return "redirect:/";
    }

    @GetMapping("/folder/{folderName}")
    public String getFolder(@PathVariable("folderName") String folderName, Model model) {
        Folder folder = folderService.getFolder(folderName);
        model.addAttribute("folderName", folderName);
        model.addAttribute("files", fileService.getFilesByFolderId(this.user.getId(), folder.getId()));
        return "folder";
    }

    @PostMapping("/folder/{folderName}")
    public String uploadFileInFolder(@PathVariable("folderName") String folderName,
                                     @RequestParam("file") MultipartFile file) {
        folderService.saveFileInFolder(file, folderName);
        return "redirect:/folder/" + folderName;
    }

    @GetMapping(value = "/", params = {"createFolder"})
    public String createFolder(@RequestParam("createFolder") boolean createFolder, Model model) {
        model.addAttribute("createFolder", createFolder);
        model.addAttribute("folder", new Folder());
        return "home";
    }

    @PostMapping("upload")
    public String saveFile(@RequestParam("file") MultipartFile file) throws IOException {
        fileService.save(file);
        return "redirect:/";
    }

    @GetMapping("/download/file{fileId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable("fileId") int id) throws FileNotFoundException {
        File file = fileService.getFile(id);
        String fileName = file.getId() + "_" + file.getName();
        byte[] fileData = storageService.downloadFile(fileName);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(file.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(new ByteArrayResource(fileData));
    }

    @GetMapping("/download/{folderName}.zip")
    public ResponseEntity<ByteArrayResource> downloadFolder(@PathVariable("folderName") String folderName) throws IOException {
        Folder folder = folderService.getFolder(folderName);
        List<File> filesInFolder = folder.getFiles();
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(bo);
        for (File file : filesInFolder) {
            String fileName = file.getId() + "_" + file.getName();
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(storageService.downloadFile(fileName));
            zipOutputStream.closeEntry();
        }
        zipOutputStream.close();
        return ResponseEntity.ok().contentType(MediaType.valueOf("application/zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + folderName + ".zip" + "\"")
                .body(new ByteArrayResource(bo.toByteArray()));
    }

    @GetMapping("/share/{folderName}.zip")
    public String shareFolder(@PathVariable("folderName") String folderName, Model model) {
        this.url = MvcUriComponentsBuilder
                .fromMethodName(HomeController.class, "downloadFolder", folderName).build().toString();
        this.fileId = -1;
        model.addAttribute("guestUsers", userService.getAllUsers());
        return "sharefile";
    }

    @GetMapping("/view/file{fileId}")
    public ResponseEntity<ByteArrayResource> viewFile(@PathVariable("fileId") int id) throws FileNotFoundException {
        File file = fileService.getFile(id);
        String fileName = file.getId() + "_" + file.getName();
        byte[] fileData = storageService.downloadFile(fileName);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(file.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(new ByteArrayResource(fileData));
    }

    @GetMapping("/delete/file{fileId}")
    public String deleteFile(@PathVariable int fileId) throws FileNotFoundException {
        File file = fileService.getFile(fileId);
        file.setDeleted(true);
        fileService.saveFile(file);
        return "redirect:/";
    }

    @GetMapping("/delete/{folderName}")
    public String deleteFolder(@PathVariable("folderName") String folderName) {
        Folder folder = folderService.getFolder(folderName);
        folder.setDeleted(true);
        folderService.save(folder);
        return "redirect:/";
    }

    @GetMapping("/addstar/file{fileId}")
    public String addStar(@PathVariable("fileId") int fileId) throws FileNotFoundException {
        File file = fileService.getFile(fileId);
        file.setStarred(true);
        fileService.saveFile(file);
        return "redirect:/";
    }

    @GetMapping("/removestar/file{fileId}")
    public String removeStar(@PathVariable("fileId") int fileId) throws FileNotFoundException {
        File file = fileService.getFile(fileId);
        file.setStarred(false);
        fileService.saveFile(file);
        return "redirect:/";
    }

    @GetMapping("/starred")
    public String getStarredFiles(Model model) {
        model.addAttribute("files", fileService.getAllStarredFiles(this.user.getId()));
        return "starred";
    }

    @GetMapping("/edit")
    public String editPage(@RequestParam int fileId, Model model) {
        model.addAttribute("edit", "true");
        model.addAttribute("fileId", fileId);
        return getHome(model);
    }

    @PostMapping("/update/file{fileId}")
    public String updateFile(@PathVariable int fileId, @RequestParam("file") MultipartFile file) throws IOException {
        fileService.update(fileId, file);
        ownerGuestService.updateNotification(fileId);
        return "redirect:/";
    }

    @GetMapping("/share/file{fileId}")
    public String shareFile(@PathVariable int fileId, Model model) {
        String url = MvcUriComponentsBuilder
                .fromMethodName(HomeController.class, "downloadFile", fileId).build().toString();
        this.fileId = fileId;
        this.url = url;
        model.addAttribute("guestUsers", userService.getAllUsers());
        return "sharefile";
    }

    @PostMapping(value = "/share", params = {"add"})
    public String addEmail(@RequestParam("email") String email, Model model) {
        emailsSelected.add(email);
        model.addAttribute("emailsSelected", emailsSelected);
        model.addAttribute("guestUsers", userService.getAllUsers());
        return "sharefile";
    }

    @PostMapping(value = "/share", params = {"removeEmail"})
    public String removeEmail(@RequestParam("removeEmail") String email, Model model) {
        emailsSelected.remove(email);
        model.addAttribute("emailsSelected", emailsSelected);
        model.addAttribute("guestUsers", userService.getAllUsers());
        return "sharefile";
    }

    @PostMapping("/share")
    public String sendFile(@RequestParam("edit") boolean access) throws MessagingException, IOException {
        int userId = this.user.getId();
        int[] guestIds = userService.getIdsByEmail(emailsSelected);
        if(this.fileId >= 0) {
            ownerGuestService.save(userId, fileId, guestIds, access);
        }
        String[] emails = Arrays.copyOf(emailsSelected.toArray(), emailsSelected.size(),
                String[].class);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(emails);
        msg.setSubject("Shared File Link");
        msg.setText("click on link to download file : " + url);
        javaMailSender.send(msg);
        emailsSelected.clear();
        return "redirect:/";
    }

    @GetMapping("/notification")
    public String notification(Model model) {
        int loginUserId = this.user.getId();
        List<OwnerGuest> list = ownerGuestService.findByGuestId(loginUserId);
        List<Notification> notificationList = ownerGuestService.getNotificationList(loginUserId);
        model.addAttribute("notificationList", notificationList);
        model.addAttribute("fileId", fileId);
        return "notification";
    }

    @GetMapping("/editNotification")
    public String editFile(@RequestParam int id, Model model) {
        int loginUserId = this.user.getId();
        List<OwnerGuest> list = ownerGuestService.findByGuestId(loginUserId);
        List<Notification> notificationList = ownerGuestService.getNotificationList(loginUserId);
        model.addAttribute("notificationList", notificationList);
        model.addAttribute("edit", true);
        model.addAttribute("id", id);
        return "notification";
    }

    @GetMapping("/shared")
    public String shared(Model model) {
        int loginUserId = this.user.getId();
        List<OwnerGuest> ownerGuestList = ownerGuestService.findByUserId(loginUserId);
        if (ownerGuestList.size() > 0) {
            List<Share> shareList = ownerGuestService.getShareList(ownerGuestList);
            model.addAttribute("shareList", shareList);
        }
        return "share";
    }

    @GetMapping("/search")
    public String viewSearchedPost(@RequestParam(name = "search") String keyword, Model model) {
        List<File> files = fileRepository.getFilesByKeyword(this.user.getId(), keyword.toLowerCase(Locale.ROOT));
        model.addAttribute("files", files);
        return "home";
    }
}