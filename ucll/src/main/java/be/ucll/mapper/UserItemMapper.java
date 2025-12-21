package be.ucll.mapper;

import be.ucll.dto.UserItemResponseDTO;
import be.ucll.model.UserItem;

public final class UserItemMapper {
  private UserItemMapper() {}
  
  public static UserItemResponseDTO toDTO(UserItem userItem) {
    return new UserItemResponseDTO(
        userItem.getId(),
        userItem.getItem().getId(),
        userItem.getItem().getName(),
        userItem.getItem().getType().name(),
        userItem.getExpirationDate(),
        userItem.getOpenedDate(),
        userItem.getOpenedRule()
    );
  }
}
