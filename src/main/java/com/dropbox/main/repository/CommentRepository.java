package com.dropbox.main.repository;

import com.dropbox.main.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query(value = "select * from comments c where c.file_id = ?1", nativeQuery = true)
    List<Comment> findCommentByFileId(int fileId);
}
