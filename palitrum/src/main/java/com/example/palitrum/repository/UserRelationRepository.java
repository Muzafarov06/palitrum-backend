package com.example.palitrum.repository;

import com.example.palitrum.model.UserRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRelationRepository extends JpaRepository<UserRelation, Long> {
    boolean existsByParentUserIdAndChildUserId(Long parentUserId, Long childUserId);

    // Поиск всех верифицированных связей по родителю
    List<UserRelation> findByParentUserIdAndVerifiedTrue(Long parentUserId);

    // НОВЫЙ МЕТОД: поиск всех верифицированных связей по ребёнку
    List<UserRelation> findByChildUserIdAndVerifiedTrue(Long childUserId);
}