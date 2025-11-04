package be.ucll.model;

import java.time.LocalDate;

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
    

    private String name;
    private LocalDate expirationDate;
    private Type type;

    public Item(String name, LocalDate expirationDate, Type type) {
        this.name = name;
        this.expirationDate = expirationDate;
        this.type = type;
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
