package sarba.movieApp.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sarba.movieApp.auth.entities.ForgotPassword;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword,Integer> {

//    @Query("select fp from ForgotPassword fp where fp.otp = ?1 and fp.user = ?2")
//    Optional<ForgotPassword> findByOtpAndUser(Integer otp, User user);

    @Query("select fp from ForgotPassword fp where fp.otp = ?1 and fp.email = ?2")
    Optional<ForgotPassword> findByOtpAndEmail(Integer otp, String email);

    ForgotPassword findByEmail(String email);
}
