package com.dropbox.main.repository;

import com.dropbox.main.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {
    @Transactional
    @Query(value = "select * from files f where f.user_id = ?1 and f.is_deleted = false and f.folder_id is null order by f.id",
            nativeQuery = true)
    List<File> allFiles(@Param("userid") int userId);

    @Query(value = "select * from files f where f.user_id = ?1 and f.is_deleted = false and f.folder_id = ?2 order by f.id",
            nativeQuery = true)
    List<File> findAllFilesByFolder(@Param("userid") int userId, @Param("folderId") int folderId);

    @Query(value = "select * from files f where f.user_id = ?1 and f.is_deleted = true order by f.id",
            nativeQuery = true)
    List<File> allDeletedFiles(@Param("userid") int userId);

    @Query(value = "select * from files f where f.user_id = ?1 and f.is_starred = true order by f.id",
            nativeQuery = true)
    List<File> allStarredFiles(@Param("userid") int userId);

    @Query(value = "select * from files f where f.user_id = ?1 and lower(f.name) like %?2%",nativeQuery = true)
    List<File> getFilesByKeyword(@Param("userid") int userId, @Param("keyword") String keyword);

    File save(File file);
}