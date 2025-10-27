package sarba.movieApp.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sarba.movieApp.auth.entities.RefreshToken;
import sarba.movieApp.auth.entities.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Integer> {

     Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
