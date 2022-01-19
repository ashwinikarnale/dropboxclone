package com.dropbox.main.controller;

import com.dropbox.main.model.Comment;
import com.dropbox.main.model.File;
import com.dropbox.main.model.User;
import com.dropbox.main.repository.CommentRepository;
import com.dropbox.main.service.CommentService;
import com.dropbox.main.service.FileService;
import com.dropbox.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileNotFoundException;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    FileService fileService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    HomeController homeController;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;
    private User user;

    @GetMapping("/showComment/file{fileId}")
    public String showComment(@PathVariable("fileId") int fileId, Model model) throws FileNotFoundException {
        this.user = userService.getCurrentUser();
        File file = fileService.getFile(fileId);
        List<Comment> comments = commentService.getCommentsByFileId(fileId);
        model.addAttribute("file", file);
        model.addAttribute("comments", comments);
        model.addAttribute("userEmail", this.user.getEmail());
        return "comment";
    }

    @GetMapping("/comment/file{fileId}")
    public String commentPage(@PathVariable("fileId") int fileId, Model model) throws FileNotFoundException {
        File file = fileService.getFile(fileId);
        model.addAttribute("fileId", fileId);
        model.addAttribute("comment", true);
        return showComment(fileId, model);
    }

    @PostMapping("/saveComment")
    public String saveComment(@RequestParam("fileId") int fileId,
                              @RequestParam("comment") String commentText,
                              Model model) throws FileNotFoundException {
        this.user = userService.getCurrentUser();
        Comment comment = new Comment();
        comment.setComment(commentText);
        comment.setEmail(this.user.getEmail());
        comment.setName(this.user.getName());
        comment.setFileId(fileId);
        commentRepository.save(comment);
        return showComment(fileId, model);
    }

    @GetMapping("/comment/edit/{commentId}")
    public String editComment(@PathVariable("commentId") int commentId,
                              @RequestParam("fileId") int fileId,
                              Model model) throws FileNotFoundException {
        Comment comment = commentRepository.getById(commentId);
        model.addAttribute("editComment", comment);
        return showComment(fileId, model);
    }

    @PostMapping("/comment/update/{commentId}")
    public String updateComment(@PathVariable("commentId") int commentId,
                                @RequestParam("fileId") int fileId,
                                @RequestParam("comment") String commentMessage,
                                Model model) throws FileNotFoundException {

        Comment existingComment = commentRepository.findById(commentId).get();
        existingComment.setComment(commentMessage);
        commentRepository.save(existingComment);
        return showComment(fileId, model);
    }

    @PostMapping("/comment/delete/{commentId}")
    public String deleteComment(@PathVariable("commentId") int commentId,
                                @RequestParam("fileId") int fileId,
                                Model model) throws FileNotFoundException {
        commentRepository.deleteById(commentId);
        return showComment(fileId, model);
    }
}