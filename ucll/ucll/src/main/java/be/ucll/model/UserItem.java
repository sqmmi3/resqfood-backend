package be.ucll.model;

import java.time.LocalDate;

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
    private Integer openedRule = 3;

    protected UserItem() {}

    public UserItem(User user, Item item, LocalDate expirationDate, LocalDate openedDate, Integer openedRule) {
        setUser(user);
        setItem(item);
        setExpirationDate(expirationDate);
        setOpenedDate(openedDate);
        setOpenedRule(openedRule);
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
        return this.openedRule;
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
            throw new IllegalArgumentException("Opened date cannot be after expiration date.");
        }
        this.openedDate = newOpenedDate;
    }

    public void setOpenedRule(Integer newOpenedRule) {
        this.openedRule = newOpenedRule;
    }

    @Override
    public String toString() {
        return "UserItem{id=" + this.id + ", user=" + this.user + ", item=" + this.item + ", expirationDate=" + this.expirationDate + ", openedDate=" + this.openedDate + ", openedRule=" + this.openedRule + "}";
    }
}
