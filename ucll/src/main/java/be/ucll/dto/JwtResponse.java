package be.ucll.dto;

public class JwtResponse {
  private String token;

  public JwtResponse() {}

  public JwtResponse(String token) {
    setToken(token);
  }

  public String getToken() { return this.token; }

  public void setToken(String newToken) { this.token = newToken; }
}
