package com.example.palitrum.repository;

import com.example.palitrum.model.Room;
import com.example.palitrum.model.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // ===== Поиск без фильтра по типу =====
    @Query("SELECT r FROM Room r WHERE " +
            "(:search = '' OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Room> findAllBySearch(@Param("search") String search, Pageable pageable);

    // ===== Поиск с фильтром по типу =====
    @Query("SELECT r FROM Room r WHERE r.type = :type AND " +
            "(:search = '' OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Room> findAllByTypeAndSearch(@Param("type") RoomType type,
                                      @Param("search") String search,
                                      Pageable pageable);

    // ===== Поиск по вместимости =====
    @Query("SELECT r FROM Room r WHERE r.capacity >= :minCapacity")
    List<Room> findByCapacityMin(@Param("minCapacity") int minCapacity);

    // ===== Статистика без типа =====
    @Query("SELECT COUNT(r) FROM Room r WHERE " +
            "(:search = '' OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    long countBySearch(@Param("search") String search);

    // ===== Статистика с типом =====
    @Query("SELECT COUNT(r) FROM Room r WHERE r.type = :type AND " +
            "(:search = '' OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    long countByTypeAndSearch(@Param("type") RoomType type, @Param("search") String search);

    // ===== Группировка по типу (без фильтра типа) =====
    @Query("SELECT r.type, COUNT(r) FROM Room r WHERE " +
            "(:search = '' OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "GROUP BY r.type")
    List<Object[]> countGroupByTypeNoTypeFilter(@Param("search") String search);

    // ===== Группировка по типу с фильтром по типу =====
    @Query("SELECT r.type, COUNT(r) FROM Room r WHERE r.type = :type AND " +
            "(:search = '' OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "GROUP BY r.type")
    List<Object[]> countGroupByTypeWithTypeFilter(@Param("type") RoomType type,
                                                  @Param("search") String search);
}