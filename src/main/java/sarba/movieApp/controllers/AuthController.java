package sarba.movieApp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sarba.movieApp.auth.entities.RefreshToken;
import sarba.movieApp.auth.entities.User;
import sarba.movieApp.auth.services.AuthService;
import sarba.movieApp.auth.services.JwtService;
import sarba.movieApp.auth.services.RefreshTokenService;
import sarba.movieApp.auth.utils.AuthResponse;
import sarba.movieApp.auth.utils.LoginRequest;
import sarba.movieApp.auth.utils.RefreshTokenRequest;
import sarba.movieApp.auth.utils.RegisterRequest;

@RestController
@RequestMapping("/api/v1/auth/")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {

        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = refreshToken.getUser();

        String accessToken = jwtService.generateToken(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .name(user.getName())
                .email(user.getEmail())
                .build());
    }
}
