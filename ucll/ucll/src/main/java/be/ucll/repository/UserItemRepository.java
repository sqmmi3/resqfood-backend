package be.ucll.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import be.ucll.model.UserItem;

public interface UserItemRepository extends JpaRepository<UserItem, Long>{
    
}
