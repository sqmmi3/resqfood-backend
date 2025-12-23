package be.ucll.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import be.ucll.model.UserItem;

public interface UserItemRepository extends JpaRepository<UserItem, Long>{
    List<UserItem> findByUser_Username(String username);
    
    @Query("SELECT ui FROM UserItem ui WHERE ui.expirationDate <= :limitDate OR ui.openedDate IS NOT NULL")
    List<UserItem> findPotentialExpiries(@Param("limitDate") LocalDate limitDate);
}
