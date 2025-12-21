package be.ucll.dto;

import java.time.LocalDate;

public record UserItemResponseDTO (
  Long id,
  Long itemId,
  String itemName,
  String type,
  LocalDate expirationDate,
  LocalDate openedDate,
  Integer openedRule,
  String description
) {}
