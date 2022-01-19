package com.dropbox.main.service;

import com.dropbox.main.model.Comment;
import com.dropbox.main.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getCommentsByFileId(int fileId){
        return commentRepository.findCommentByFileId(fileId);
    }
}