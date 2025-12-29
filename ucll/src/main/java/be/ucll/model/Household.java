package be.ucll.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(schema = "resqfood",name = "households")
public class Household {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false, name = "invite_code")
  private String inviteCode;

  @OneToMany(mappedBy = "household")
  private List<User> members = new ArrayList<>();

  public Household() {}
  
  public Household(String inviteCode) {
    setInviteCode(inviteCode);
  }

  // Getters
  public Long getId() { return this.id; }
  public String getInviteCode() { return this.inviteCode; }
  public List<User> getMembers() { return this.members; }

  // Setters
  public void setId(Long newId) { this.id = newId; }
  public void setInviteCode(String newInviteCode) { this.inviteCode = newInviteCode; }
  public void setMembers(List<User> newMembers) { this.members = newMembers; }

  public void addMember(User user) { 
    this.members.add(user);
    user.setHousehold(this);
  }
  public void removeMember(User user) {
    this.members.remove(user);
    user.setHousehold(null);
  }
}
