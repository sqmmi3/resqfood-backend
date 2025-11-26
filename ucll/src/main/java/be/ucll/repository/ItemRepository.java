package be.ucll.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import be.ucll.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
  List<Item> findByNameContainingIgnoreCase(String name);
}
