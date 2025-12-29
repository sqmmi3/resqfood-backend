package be.ucll.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import be.ucll.model.User;
import be.ucll.model.UserItem;

public interface UserItemRepository extends JpaRepository<UserItem, Long>{
    List<UserItem> findByUser_Username(String username);

    List<UserItem> findByUser_Household_Id(Long householdId);
    
    @Query("SELECT ui FROM UserItem ui WHERE ui.expirationDate <= :limitDate OR ui.openedDate IS NOT NULL")
    List<UserItem> findPotentialExpiries(@Param("limitDate") LocalDate limitDate);

    @Query("SELECT COUNT(ui) FROM UserItem ui WHERE ui.user = :user AND ui.expirationDate < CURRENT_DATE")
    int countExpiredItemsForUser(@Param("user") User user);

    @Query("SELECT COUNT(ui) FROM UserItem ui WHERE ui.user = :user AND ui.expirationDate BETWEEN CURRENT_DATE AND :futureDate")
    int countNearingExpiry(@Param("user") User user, @Param("futureDate") LocalDate futureDate);
}
