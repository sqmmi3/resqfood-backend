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

  public Item(String name, Category category) {
    setName(name);
    setCategory(category);
  }

  public Item(String name, Category category, Integer quantity, LocalDate expirationDate) {
    setName(name);
    setCategory(category);
    setQuantity(quantity);
    setExpirationDate(expirationDate);
  }

  public Item(String name, Category category, Integer quantity, LocalDate expirationDate, String description) {
    setName(name);
    setCategory(category);
    setQuantity(quantity);
    setExpirationDate(expirationDate);
    setDescription(description);
  }

  // Getters
  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public Category getCategory() {
    return this.category;
  }

  public List<UserItem> getUserItems() {
    return this.userItems;
  }

  public Integer getQuantity() {
    return this.quantity;
  }

  public LocalDate getExpirationDate() {
    return this.expirationDate;
  }

  public LocalDate getOpenedDate() {
    return this.openedDate;
  }

  public String getDescription() {
    return this.description;
  }

  // Setters
  public void setId(Long newId) {
    this.id = newId;
  }

  public void setName(String newName) {
    this.name = newName;
  }

  public void setCategory(Category newCategory) {
    this.category = newCategory;
  }

  public void setQuantity(Integer newQuantity) {
    this.quantity = newQuantity;
  }

  public void setExpirationDate(LocalDate newExpirationDate) {
    this.expirationDate = newExpirationDate;
  }

  public void setOpenedDate(LocalDate newOpenedDate) {
    this.openedDate = newOpenedDate;
  }

  public void setDescription(String newDescription) {
    this.description = newDescription;
  }

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
    return "Item{id=" + this.id + ", name=" + this.name + ", category=" + this.category + ", quantity=" + this.quantity + ", expirationDate=" + this.expirationDate + ", openedDate=" + this.openedDate + ", description=" + this.description + "}";
  }
}
