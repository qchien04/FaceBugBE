package com.repository;

import com.entity.VideoStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface VideoStorageRepo extends JpaRepository<VideoStorage,Integer> {
    Optional<VideoStorage> findById(int id);


    @Query("SELECT COALESCE(MAX(v.videoNumber), 0) FROM VideoStorage v")
    Integer findMaxVideoNumber();

    Optional<VideoStorage> findByVideoNumberAndAuthorId(Integer videoNumber, Integer authorId);
}