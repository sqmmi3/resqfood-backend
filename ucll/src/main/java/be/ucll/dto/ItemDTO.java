package be.ucll.dto;

public class ItemDTO {
  private Long id;
  private String name;
  private String type;

  public ItemDTO(Long id, String name, String type) {
    this.id = id;
    this.name = name;
    this.type = type;
  }

  // Getters
  public Long getId() { return this.id; }

  public String getName() { return this.name; }
  
  public String getType() { return this.type; }
}
