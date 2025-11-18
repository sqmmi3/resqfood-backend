package be.ucll.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import be.ucll.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
}
