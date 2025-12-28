package be.ucll.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import be.ucll.model.Household;

public interface HouseholdRepository extends JpaRepository<Household, Long>{
  Optional<Household> findByInviteCode(String inviteCode);
}
