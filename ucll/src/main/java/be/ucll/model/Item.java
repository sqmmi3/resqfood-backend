package be.ucll.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(schema = "resqfood", name = "items")
public class Item {

  public enum Category{
    FRUIT,
    VEGETABLE,
    GRAIN,
    PROTEIN,
    DAIRY,
    SWEETS,
    BEVERAGE,
    READY_MEAL,
    SPICE,
    BAKING,
    FROZEN,
    CANNED,
    PANTRY
  }
    
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Name is required.")
  private String name;
    
  @Enumerated(EnumType.STRING)
  private Category category;

  private Integer quantity;

  @NotNull(message = "Expiration date is required.")
  private LocalDate expirationDate;

  private LocalDate openedDate;

  private String description;

  @OneToMany(mappedBy = "item")
  private List<UserItem> userItems = new ArrayList<>();

  protected Item() {}

  public Item(String name, LocalDate expirationDate) {
    this.name = name;
    this.expirationDate = expirationDate;
  }

  public Item(String name, Category category, Integer quantity, LocalDate expirationDate, String description) {
    this.name = name;
    this.category = category;
    this.quantity = quantity;
    this.expirationDate = expirationDate;
    this.description = description;
  }

  // Getters
  public Long getId() { return id; }
  public String getName() { return name; }
  public Category getCategory() { return category; }
  public Integer getQuantity() { return quantity; }
  public LocalDate getExpirationDate() { return expirationDate; }
  public LocalDate getOpenedDate() { return openedDate; }
  public String getDescription() { return description; }
  public List<UserItem> getUserItems() { return userItems; }

  // Setters
  public void setName(String name) { this.name = name; }
  public void setCategory(Category category) { this.category = category; }
  public void setQuantity(Integer quantity) { this.quantity = quantity; }
  public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
  public void setOpenedDate(LocalDate openedDate) { this.openedDate = openedDate; }
  public void setDescription(String description) { this.description = description; }

  public void addUserItem(UserItem userItem) {
    if (!userItems.contains(userItem)) {
      userItems.add(userItem);
      userItem.setItem(this);
    }
  }

  public void removeUserItem(UserItem userItem) {
    if (userItems.remove(userItem)) {
      userItem.setItem(null);
    }
  }

  @Override
  public String toString() {
    return "Item{id=" + id + ", name=" + name + ", category=" + category + ", quantity=" + quantity + ", expirationDate=" + expirationDate + ", description=" + description + "}";
  }
}
