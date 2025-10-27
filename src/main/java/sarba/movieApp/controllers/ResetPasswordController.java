package sarba.movieApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sarba.movieApp.auth.entities.ForgotPassword;
import sarba.movieApp.auth.entities.User;
import sarba.movieApp.auth.repositories.ForgotPasswordRepository;
import sarba.movieApp.auth.repositories.UserRepository;
import sarba.movieApp.auth.utils.ChangePassword;
import sarba.movieApp.auth.utils.ResetPassword;

import java.util.Objects;

@RestController
@RequestMapping("/resetPassword")
@CrossOrigin(origins = "*")
public class ResetPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/{email}")
    public ResponseEntity<String> resetPasswordHandler(@RequestBody ResetPassword resetPassword,
                                                       @PathVariable String email) {

        System.out.println(resetPassword.toString());

        System.out.println("---------------------------->"+email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide an valid email!"));

        System.out.println("db password ------------>"+user.getPassword());

        if (!passwordEncoder.matches(resetPassword.oldPassword(),user.getPassword())){
            return new ResponseEntity<>("Old password not matched.", HttpStatus.CONFLICT);
        }

        String convertedNewPassword = passwordEncoder.encode(resetPassword.newPassword());
        userRepository.updatePassword(email, convertedNewPassword);

        return ResponseEntity.ok("Password has been changed!");
    }
}
