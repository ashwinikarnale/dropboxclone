package com.dropbox.main.repository;

import com.dropbox.main.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer> {

    @Query(value = "select * from folders where user_id = ?1 and is_deleted = false", nativeQuery = true)
    List<Folder> findAllByUserId(@Param("id") int id);

    @Query(value = "select * from folders where user_id = ?1 and is_deleted = true", nativeQuery = true)
    List<Folder> findAllDeletedFolders(@Param("id") int id);

    @Query(value = "select * from folders where folders.name like ?1", nativeQuery = true)
    Folder findFolder(@Param("name") String name);
}
