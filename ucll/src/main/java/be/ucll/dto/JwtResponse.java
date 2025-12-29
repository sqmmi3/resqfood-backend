package be.ucll.dto;

public record JwtResponse (
  String token,
  String username,
  String householdCode
) {}
