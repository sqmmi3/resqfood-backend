package be.ucll.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import be.ucll.exception.DomainException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(schema = "resqfood", name = "users_items")
public class UserItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "item_id", nullable = false)
  private Item item;

  @NotNull(message = "Expiration date is required.")
  @FutureOrPresent(message = "Expiration date must be in the future or present.")
  private LocalDate expirationDate;

  @PastOrPresent(message = "Opened date must be in the past or present.")
  private LocalDate openedDate;

  @Positive(message = "Opened rule must be at least 1.")
  private Integer openedRule;

  private LocalDateTime lastNotifiedAt;

  @Column(name = "description", length = 128)
  @Size(min = 0, max = 128, message = "Description cannot be longer than 128 characters.")
  private String description;

  public UserItem() {}

  public UserItem(User user, Item item, LocalDate expirationDate) {
    setUser(user);
    setItem(item);
    setExpirationDate(expirationDate);
    setOpenedDate(null);
    setOpenedRule(null);
    setDescription(null);
  }

  public UserItem(User user, Item item, LocalDate expirationDate, LocalDate openedDate) {
    setUser(user);
    setItem(item);
    setExpirationDate(expirationDate);
    setOpenedDate(openedDate);
    setOpenedRule(null);
    setDescription(null);
  }

  public UserItem(User user, Item item, LocalDate expirationDate, Integer openedRule) {
    setUser(user);
    setItem(item);
    setExpirationDate(expirationDate);
    setOpenedDate(null);
    setOpenedRule(openedRule);
    setDescription(null);
  }

  public UserItem(User user, Item item, LocalDate expirationDate, LocalDate openedDate, Integer openedRule) {
    setUser(user);
    setItem(item);
    setExpirationDate(expirationDate);
    setOpenedDate(openedDate);
    setOpenedRule(openedRule);
    setDescription(null);
  }

  public UserItem(User user, Item item, LocalDate expirationDate, String description) {
    setUser(user);
    setItem(item);
    setExpirationDate(expirationDate);
    setOpenedDate(null);
    setOpenedRule(null);
    setDescription(description);
  }

  public UserItem(User user, Item item, LocalDate expirationDate, LocalDate openedDate, String description) {
    setUser(user);
    setItem(item);
    setExpirationDate(expirationDate);
    setOpenedDate(openedDate);
    setOpenedRule(null);
    setDescription(description);
  }

  public UserItem(User user, Item item, LocalDate expirationDate, LocalDate openedDate, Integer openedRule, String description) {
    setUser(user);
    setItem(item);
    setExpirationDate(expirationDate);
    setOpenedDate(openedDate);
    setOpenedRule(openedRule);
    setDescription(description);
  }

  // Getters
  public Long getId() {
    return this.id;
  }

  public User getUser() {
    return this.user;
  }

  public Item getItem() {
    return this.item;
  }

  public LocalDate getExpirationDate() {
    return this.expirationDate;
  }

  public LocalDate getOpenedDate() {
    return this.openedDate;
  }

  public Integer getOpenedRule() {
    return this.openedRule != null ? this.openedRule : item.getOpenedRule();
  }

  public LocalDateTime getLastNotifiedAt() {
    return this.lastNotifiedAt;
  }

  public String getDescription() {
    return this.description;
  }

  // Setters
  public void setId(Long newId) {
    this.id = newId;
  }

  public void setUser(User newUser) {
    this.user = newUser;
  }

  public void setItem(Item newItem) {
    this.item = newItem;
  }

  public void setExpirationDate(LocalDate newExpirationDate) {
    this.expirationDate = newExpirationDate;
  }

  public void setOpenedDate(LocalDate newOpenedDate) {
    if (newOpenedDate != null && newOpenedDate.isAfter(this.getExpirationDate())) {
      throw new DomainException("Opened date cannot be after expiration date.");
    }
    this.openedDate = newOpenedDate;
  }

  public void setOpenedRule(Integer newOpenedRule) {
    this.openedRule = newOpenedRule;
  }

  public void setLastNotifiedAt(LocalDateTime newLastNotifiedAt) {
    this.lastNotifiedAt = newLastNotifiedAt;
  }

  public void setDescription(String newDescription) {
    this.description = newDescription;
  }
  
  @Override
  public String toString() {
    return "UserItem{id=" + this.id + 
      ", userId=" + (this.user != null ? user.getId() : null) + 
      ", item=" + (this.item != null ? item.getId() : null) + 
      ", expirationDate=" + this.expirationDate + 
      ", openedDate=" + this.openedDate + 
      ", openedRule=" + this.openedRule +
      ", description=" + this.description + 
    "}";
  }
}
