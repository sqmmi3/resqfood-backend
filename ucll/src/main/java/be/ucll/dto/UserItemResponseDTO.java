package be.ucll.dto;

import java.time.LocalDate;

public class UserItemResponseDTO {
  private Long id;
  private Long itemId;
  private String itemName;
  private String type;
  private LocalDate expirationDate;
  private LocalDate openedDate;
  private Integer openedRule;

  public UserItemResponseDTO() {}

  public UserItemResponseDTO(
    Long id,
    Long itemId,
    String itemName,
    String type,
    LocalDate expirationDate,
    LocalDate openedDate,
    Integer openedRule
  ) {
    this.id = id;
    this.itemId = itemId;
    this.itemName = itemName;
    this.type = type;
    this.expirationDate = expirationDate;
    this.openedDate = openedDate;
    this.openedRule = openedRule;
  }

  // Getters
  public Long getId() { return this.id; }
  public Long getItemId() { return this.itemId; }
  public String getItemName() { return this.itemName; }
  public String getType() { return this.type; }
  public LocalDate getExpirationDate() { return this.expirationDate; }
  public LocalDate getOpenedDate() { return this.openedDate; }
  public Integer getOpenedRule() { return this.openedRule; }
}
