package be.ucll.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import be.ucll.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
  Item findByNameContainingIgnoreCase(String name);
}
