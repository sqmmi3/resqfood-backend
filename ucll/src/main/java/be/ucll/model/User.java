package be.ucll.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(schema = "resqfood", name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Username is required.")
  @Size(min=3, max=50, message = "Username must be between 3 and 50 characters.")
  @Pattern(regexp = "^\\w+$", message = "Username can only contain letters, numbers, and underscores")
  @Column(unique = true, nullable = false, length = 50, name = "username")
  private String username;

  @NotBlank(message = "Email is required.")
  @Email(message = "Email should be valid.")
  @Column(unique = true, nullable = false, length = 255, name = "email")
  private String email;

  @NotBlank(message = "Password is required.")
  @Size(min = 8, message = "Password must be at least 8 characters long.")
  // Regex:
  // 1. (?=.*\\d)                                   : at least one digit
  // 2. (?=.*[a-z])                                 : at least one lowercase letter
  // 3. (?=.*[A-Z])                                 : at least one uppercase letter
  // 4. (?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]) : at least one special character
  // 5. .{8,}                                       : at least 8 characters long
  @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
    message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character.")
  @Column(nullable = false, length = 72, name = "password")
  private String password;

  @JsonBackReference
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserItem> userItems = new ArrayList<>();

  @JsonManagedReference
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserDeviceToken> deviceTokens = new ArrayList<>();

  protected User() {}

  public User(String username, String email, String password) {
    setUsername(username);
    setEmail(email);
    setPassword(password);
  }

  // Getters
  public Long getId() {
    return this.id;
  }

  public String getUsername() {
    return this.username;
  }

  public String getEmail() {
    return this.email;
  }

  public String getPassword() {
    return this.password;
  }

  public List<UserItem> getUserItems() {
    return this.userItems;
  }

  public List<Item> getItems() {
    return userItems.stream()
                    .map(UserItem::getItem)
                    .toList();
  }

  public List<UserDeviceToken> getDeviceTokens() {
    return this.deviceTokens;
  }

  // Setters
  public void setUsername(String newUsername) {
    this.username = newUsername;
  }

  public void setEmail(String newEmail) {
    this.email = newEmail;
  }

  public void setPassword(String newPassword) {
    this.password = newPassword;
  }

  // Helper Methods
  public void addUserItem(UserItem userItem) {
    if (!this.userItems.contains(userItem)) {
      this.userItems.add(userItem);
        userItem.setUser(this);
    }
  }

  public void removeUserItem(UserItem userItem) {
    if (this.userItems.remove(userItem)) {
      userItem.setUser(null);
    }
  }

  public void addDeviceToken(UserDeviceToken token) {
    deviceTokens.add(token);
    token.setUser(this);
  }

  public void removeDeviceToken(UserDeviceToken token) {
    deviceTokens.remove(token);
    token.setUser(null);
  }

  @Override
  public String toString() {
    return "User{id=" + this.id + ", username=" + this.username + ", email=" + this.email + ", items=" + this.getItems() + "}";
  }    
}
