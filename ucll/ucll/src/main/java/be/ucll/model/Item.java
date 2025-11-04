package be.ucll.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
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

    private String name;
    private LocalDate expirationDate;
    private Type type;

    @ManyToMany(mappedBy = "items")
    private List<User> users;

    protected Item() {
    }

    public Item(String name, LocalDate expirationDate, Type type) {
        this.name = name;
        this.expirationDate = expirationDate;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public LocalDate getExpirationDate() {
        return expirationDate;
    }
    public Type getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
    public void setType(Type type) {
        this.type = type;
    }
}
