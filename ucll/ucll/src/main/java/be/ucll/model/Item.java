package be.ucll.model;

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

    public enum Type{
        MEAT,
        FISH,
        DAIRY,
        VEGETABLE,
        FRUIT,
        BAKERY,
        BEVERAGE,
        OTHER
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required.")
    private String name;
    
    @NotNull(message = "Type is required.")
    @Enumerated(EnumType.STRING)
    private Type type;

    @OneToMany(mappedBy = "item")
    private List<UserItem> userItems = new ArrayList<>();

    protected Item() {}

    public Item(String name, Type type) {
        setName(name);
        setType(type);
    }

    // Getters
    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Type getType() {
        return this.type;
    }

    public List<UserItem> getUserItems() {
        return this.userItems;
    }

    // Setters
    public void setId(Long newId) {
        this.id = newId;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setType(Type newType) {
        this.type = newType;
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
        return "Item{id=" + this.id + ", name=" + this.name + ", type=" + this.type + "}";
    }
}
