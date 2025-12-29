package be.ucll.dto;

public record UserProfileDTO(
    String username,
    String email,
    String householdCode,
    String memberSince,
    int itemsRescued,
    int itemsExpired
) {}
