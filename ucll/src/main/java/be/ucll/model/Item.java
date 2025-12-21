package be.ucll.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(schema = "resqfood", name = "items")
public class Item {

  public enum Type{
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
  private Type type;

  private Integer openedRule = 3;

  @JsonIgnore
  @OneToMany(mappedBy = "item")
  private List<UserItem> userItems = new ArrayList<>();

  protected Item() {}

  public Item(String name, Type type) {
    setName(name);
    setType(type);
  }

  // Getters
  public Long getId() { return this.id; }
  public String getName() { return this.name; }
  public Type getType() { return this.type; }
  public Integer getOpenedRule() { return this.openedRule ;}
  public List<UserItem> getUserItems() { return this.userItems; }

  // Setters
  public void setName(String newName) { this.name = newName; }
  public void setType(Type newType) { this.type = newType; }
  public void setOpenedRule(Integer newOpenedRule) { this.openedRule = newOpenedRule; }

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
    return "Item{id=" + this.id + ", name=" + this.name + ", type=" + this.type + "}";
  }
}
