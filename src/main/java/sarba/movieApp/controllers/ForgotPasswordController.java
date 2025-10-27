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

// --- 1. Define the OTP Display Area (Making it stand out) ---
        String otpDisplay = String.format("""
                <p style="font-size: 28px; font-weight: bold; color: #0047AB; margin: 20px 0;">
                    %d
                </p>
                """, otp);

// --- 2. Construct the Full HTML Body ---
        String htmlBody = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Password Reset OTP</title>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333333; }
                        .container { width: 100%%; max-width: 600px; margin: 0 auto; border: 1px solid #dddddd; padding: 20px; border-radius: 8px; }
                        .header { background-color: #f4f4f4; padding: 10px 20px; border-radius: 8px 8px 0 0; text-align: center; }
                        .otp-box { text-align: center; padding: 20px; border: 1px dashed #cccccc; margin: 20px 0; }
                        .footer { font-size: 12px; color: #aaaaaa; text-align: center; margin-top: 20px; }
                        .button { display: inline-block; background-color: #007bff; color: white !important; padding: 10px 20px; text-decoration: none; border-radius: 5px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2 style="color: #333333;">%s</h2>
                        </div>
                
                        <p>Dear Customer,</p>
                
                        <p>We have received a request to verify your account for a password reset.</p>
                
                        <p>For security, please use the following **One-Time Password (OTP)** immediately:</p>
                
                        <div class="otp-box">
                            %s
                        </div>
                
                        <p>This code will expire in **10 minutes**. Do not share this code with anyone.</p>
                
                        <p style="text-align: center; margin: 30px 0;">
                            <a href="[Link to your app's reset page, if applicable]" class="button">
                                Continue to Reset Password
                            </a>
                        </p>
                
                        <p>If you did not initiate this request, please disregard this email or contact us immediately at <a href="mailto:%s">%s</a>.</p>
                
                        <p>Best regards,</p>
                        <p>The %s Team</p>
                
                        <div class="footer">
                            This is an automated message. Please do not reply to this email.
                        </div>
                    </div>
                </body>
                </html>
                """, companyName, otpDisplay, supportEmail, supportEmail, companyName);


// --- 3. Build the Final MailBody Object ---
        MailBody mailBody = MailBody.builder()
                .to(email)
                .subject("Your One-Time Password (OTP) for Password Reset")
                // Plain text fallback for very old email clients
                .text("Your OTP is: " + otp + ". Valid for 10 minutes.")
                .htmlText(htmlBody) // <-- Set the HTML content here
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 60 * 100000))
//                .user(user)
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


    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword,
                                                        @PathVariable String email) {

        ForgotPassword fp=forgotPasswordRepository.findByEmail(email);

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