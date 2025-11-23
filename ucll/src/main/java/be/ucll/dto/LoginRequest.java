package be.ucll.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LoginRequest {

  @NotBlank(message = "Username is required.")
  @Size(min=3, max=50, message = "Username must be between 3 and 50 characters.")
  @Pattern(regexp = "^\\w+$", message = "Username can only contain letters, numbers, and underscores")
  private String username;

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
  private String password;

  public LoginRequest() {}

  public LoginRequest(String username, String password) {
    setUsername(username);
    setPassword(password);
  }

  public String getUsername() { return this.username; }

  public String getPassword() { return this.password; }

  public void setUsername(String newUsername) { this.username = newUsername; }

  public void setPassword(String newPassword) { this.password = newPassword; }
}
