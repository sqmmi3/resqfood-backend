package be.ucll.dto;

public class UserDTO {
  private Long id;
  private String username;
  private String email;
  private String householdCode;

  public UserDTO(Long id, String username, String email, String householdCode) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.householdCode = householdCode;
  }

  public Long getId() { return this.id; }

  public String getUsername() { return this.username; }

  public String getEmail() { return this.email; }

  public String getHouseholdCode() { return this.householdCode; }
}
