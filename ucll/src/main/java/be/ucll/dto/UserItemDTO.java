package be.ucll.dto;

import java.time.LocalDate;

public class UserItemDTO {
  private Long id;
  private Long itemId;
  private String itemName;
  private LocalDate expirationDate;
  private LocalDate openedDate;
  private Integer openedRule;
  private String description;

  public UserItemDTO() {}

  public UserItemDTO(Long id, Long itemId, String itemName, LocalDate expirationDate, LocalDate openedDate, Integer openedRule, String description) {
    this.id = id;
    this.itemId = itemId;
    this.itemName = itemName;
    this.expirationDate = expirationDate;
    this.openedDate = openedDate;
    this.openedRule = openedRule;
    this.description = description;
  }

  // Getters
  public Long getId() { return this.id; }

  public Long getItemId() { return this.itemId; }
  
  public String getItemName() { return this.itemName; }
  
  public LocalDate getExpirationDate() { return this.expirationDate; }
  
  public LocalDate getOpenedDate() { return this.openedDate; }
  
  public Integer getOpenedRule() { return this.openedRule; }

  public String getDescription() { return this.description; }
}
