package sarba.movieApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sarba.movieApp.auth.entities.User;
import sarba.movieApp.auth.entities.ForgotPassword;
import sarba.movieApp.auth.repositories.ForgotPasswordRepository;
import sarba.movieApp.auth.repositories.UserRepository;
import sarba.movieApp.auth.utils.ChangePassword;
import sarba.movieApp.dto.MailBody;
import sarba.movieApp.service.EmailService;
import sarba.movieApp.utils.EmailTemplateUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
@CrossOrigin(origins = "*")
public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    // send mail for email verification
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide an valid email!" + email));

        int otp = otpGenerator();
        String companyName = "Sarba Movie";
        String supportEmail = "contact@sarba.com";
        String resetLink = "[Link to your app's reset page, if applicable]"; // Define the link

        String htmlBody;
        try {
            // --- 1. Generate the HTML Body using the template and utility ---
            htmlBody = EmailTemplateUtils.getPasswordResetHtml(otp, companyName, supportEmail, resetLink);
        } catch (IOException e) {
            // Log the error and handle the failure (e.g., return an Internal Server Error)
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to load email template.");
        }

        // --- 2. Build the Final MailBody Object ---
        MailBody mailBody = MailBody.builder()
                .to(email)
                .subject("Your One-Time Password (OTP) for Password Reset")
                .text("Your OTP is: " + otp + ". Valid for 10 minutes.") // Plain text fallback
                .htmlText(htmlBody) // <-- Use the generated HTML here
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                // Note: 60 * 100000 milliseconds is 100 minutes. Adjust for 10 minutes (60 * 10 * 1000)
                .expirationTime(new Date(System.currentTimeMillis() + 60 * 10 * 1000))
                .email(email)
                .isValidated(false)
                .build();

        emailService.sendHtmlMail(mailBody);
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok("Email sent for verification!");
    }

    @PostMapping("/verifyOtp/{email}/{otp}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide an valid email!"));

//        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user)
//                .orElseThrow(() -> new RuntimeException("Invalid OTP for email: " + email));
        ForgotPassword fp = forgotPasswordRepository.findByOtpAndEmail(otp, email)
                .orElseThrow(() -> new RuntimeException("Invalid OTP for email: " + email));

        if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(fp.getId());
            return new ResponseEntity<>("OTP has expired!", HttpStatus.EXPECTATION_FAILED);
        }
//        System.out.println("-------------->id to be deleted: "+fp.getId());

        fp.setValidated(true);
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok("OTP verified!");
    }


    @PostMapping("/changePassword/{email}/{otp}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword,
                                                        @PathVariable String email,@PathVariable Integer otp) {

        ForgotPassword fp=forgotPasswordRepository.findByOtpAndEmail(otp,email)
                .orElseThrow(() -> new RuntimeException("Invalid OTP for email: " + email));

        System.out.println("email -------------------->"+fp.getEmail());
        System.out.println("validated -------------------->"+fp.isValidated());

        if (!Objects.equals(fp.getEmail(), email) || !fp.isValidated()){
            return new ResponseEntity<>("Please verify email again!", HttpStatus.EXPECTATION_FAILED);
        }

        if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())) {
            return new ResponseEntity<>("Please enter the password again!", HttpStatus.EXPECTATION_FAILED);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encodedPassword);

        System.out.println("id to delete -------------------->"+fp.getId());
        forgotPasswordRepository.deleteById(fp.getId());

        return ResponseEntity.ok("Password has been changed!");
    }

    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}