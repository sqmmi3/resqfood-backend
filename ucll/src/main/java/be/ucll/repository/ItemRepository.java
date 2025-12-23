package be.ucll.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import be.ucll.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
  Optional<Item> findByNameContainingIgnoreCase(String name);
}
