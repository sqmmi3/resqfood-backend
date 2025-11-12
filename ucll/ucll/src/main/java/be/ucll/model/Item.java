package be.ucll.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

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

    @NotNull(message = "Expiration date is required.")
    @FutureOrPresent(message = "Expiration date must be in the future.")
    private LocalDate expirationDate;
    
    @NotNull(message = "Type is required.")
    @Enumerated(EnumType.STRING)
    private Type type;

    @PastOrPresent(message = "Opened date must be in the past or present.")
    private LocalDate openedDate;
    
    @Positive(message = "Opened rule must be at least 1")
    private Integer openedRule = 3;

    @ManyToMany(mappedBy = "items")
    private List<User> users;

    protected Item() {
    }

    public Item(String name, LocalDate expirationDate, Type type, LocalDate openedDate, Integer openedRule) {
        setName(name);
        setExpirationDate(expirationDate);
        setType(type);
        setOpenedDate(openedDate);
        setOpenedRule(openedRule != null ? openedRule : Integer.valueOf(3));
    }

    // Getters
    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
    public LocalDate getExpirationDate() {
        return this.expirationDate;
    }
    public Type getType() {
        return this.type;
    }

    public LocalDate getOpenedDate() {
        return this.openedDate;
    }

    public Integer getOpenedRule() {
        return this.openedRule;
    }

    // Setters
    public void setId(Long newId) {
        this.id = newId;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setExpirationDate(LocalDate newExpirationDate) {
        this.expirationDate = newExpirationDate;
    }

    public void setType(Type newType) {
        this.type = newType;
    }

    public void setOpenedDate(LocalDate newOpenedDate) {
        this.openedDate = newOpenedDate;
    }

    public void setOpenedRule(Integer newOpenedRule) {
        this.openedRule = newOpenedRule;
    }
}
