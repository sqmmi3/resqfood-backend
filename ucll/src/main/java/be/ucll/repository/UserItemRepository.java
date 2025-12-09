package be.ucll.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import be.ucll.model.UserItem;

public interface UserItemRepository extends JpaRepository<UserItem, Long>{
    List<UserItem> findByUser_Username(String username);
}
