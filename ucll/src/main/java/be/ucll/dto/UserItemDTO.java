package be.ucll.dto;

import java.time.LocalDate;

public class UserItemDTO {
  private Long userId;
  private String username;
  private LocalDate expirationDate;
  private LocalDate openedDate;
  private Integer openedRule;

  public UserItemDTO() {}

  public UserItemDTO(Long userId, String username, LocalDate expirationDate, LocalDate openedDate, Integer openedRule) {
    this.userId = userId;
    this.username = username;
    this.expirationDate = expirationDate;
    this.openedDate = openedDate;
    this.openedRule = openedRule;
  }

  // Getters
  public Long getUserId() { return this.userId; }
  public String getUsername() { return this.username; }
  public LocalDate getExpirationDate() { return this.expirationDate; }
  public LocalDate getOpenedDate() { return this.openedDate; }
  public Integer getOpenedRule() { return this.openedRule; }

  // Setters
  public void setUserId(Long userId) { this.userId = userId; }
  public void setUsername(String username) { this.username = username; }
  public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
  public void setOpenedDate(LocalDate openedDate) { this.openedDate = openedDate; }
  public void setOpenedRule(Integer openedRule) { this.openedRule = openedRule; }
}
