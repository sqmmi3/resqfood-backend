package be.ucll.dto;

import java.time.LocalDate;
import java.util.List;

public class ItemDTO {
  private Long id;
  private String name;
  private String category;
  private Integer quantity;
  private LocalDate expirationDate;
  private LocalDate openedDate;
  private String description;
  private List<UserItemDTO> users;

  public ItemDTO() {}

  public ItemDTO(Long id, String name, String category, Integer quantity, LocalDate expirationDate, String description) {
    this.id = id;
    this.name = name;
    this.category = category;
    this.quantity = quantity;
    this.expirationDate = expirationDate;
    this.description = description;
  }

  public ItemDTO(Long id, String name, String category, Integer quantity, LocalDate expirationDate, String description, List<UserItemDTO> users) {
    this.id = id;
    this.name = name;
    this.category = category;
    this.quantity = quantity;
    this.expirationDate = expirationDate;
    this.description = description;
    this.users = users;
  }

  // Getters
  public Long getId() { return this.id; }
  public String getName() { return this.name; }
  public String getCategory() { return this.category; }
  public Integer getQuantity() { return this.quantity; }
  public LocalDate getExpirationDate() { return this.expirationDate; }
  public LocalDate getOpenedDate() { return this.openedDate; }
  public String getDescription() { return this.description; }
  public List<UserItemDTO> getUsers() { return this.users; }

  // Setters
  public void setId(Long id) { this.id = id; }
  public void setName(String name) { this.name = name; }
  public void setCategory(String category) { this.category = category; }
  public void setQuantity(Integer quantity) { this.quantity = quantity; }
  public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
  public void setOpenedDate(LocalDate openedDate) { this.openedDate = openedDate; }
  public void setDescription(String description) { this.description = description; }
  public void setUsers(List<UserItemDTO> users) { this.users = users; }
}
