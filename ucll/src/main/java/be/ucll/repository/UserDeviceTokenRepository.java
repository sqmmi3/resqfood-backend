package be.ucll.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import be.ucll.model.UserDeviceToken;

public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {
  List<UserDeviceToken> findAllByUserId(Long userId);
  Optional<UserDeviceToken> findByToken(String token);
}
