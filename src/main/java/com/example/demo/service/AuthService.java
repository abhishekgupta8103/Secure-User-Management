package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.service.EmailService;
import com.example.demo.exception.EmailNotVerifiedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

@Service
public class AuthService {
    private static final Logger logger =
            LoggerFactory.getLogger(AuthService.class);
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }
    public User register(RegisterRequest request) {
        logger.info("User registration initiated");
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed: email already registered");
            throw new RuntimeException("Email already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(Role.USER);

        String verificationToken = UUID.randomUUID().toString();

        user.setVerificationToken(verificationToken);
        user.setEmailVerified(false);

        // Save user first
        User savedUser = userRepository.save(user);

        // Create verification link
        String verificationLink =
                "http://localhost:8080/api/auth/verify?token="
                        + savedUser.getVerificationToken();

        // Send verification email
        emailService.sendEmail(
                savedUser.getEmail(),
                "Verify Your Email",
                "Hello " + savedUser.getName() + ",\n\n"
                        + "Please verify your email by clicking this link:\n"
                        + verificationLink
                        + "\n\nThank you!"
        );
        logger.info("Verification email sent successfully");


        return savedUser;
    }

    public String login(LoginRequest request) {

        logger.info("Login attempt received");

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login failed: invalid credentials");
                    return new RuntimeException("Invalid email or password");
                });

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            logger.warn("Login failed: invalid credentials");
            throw new RuntimeException("Invalid email or password");
        }

        if (!user.isEmailVerified()) {
            logger.warn("Login blocked: email not verified");
            throw new EmailNotVerifiedException(
                    "Please verify your email before login"
            );
        }

        logger.info("Login successful");

        return jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
    }

    public void verifyEmail(String token) {

        logger.info("Email verification initiated");

        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> {
                    logger.warn("Email verification failed: invalid token");
                    return new RuntimeException("Invalid verification token");
                });

        user.setEmailVerified(true);
        user.setVerificationToken(null);

        userRepository.save(user);

        logger.info("Email verified successfully");
    }

    public void forgotPassword(String email) {
        logger.info("Forgot password request received");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String resetToken = UUID.randomUUID().toString();

        user.setResetPasswordToken(resetToken);
        user.setResetPasswordTokenExpiry(
                java.time.LocalDateTime.now().plusMinutes(15)
        );

        userRepository.save(user);

        String resetLink =
                "http://localhost:8080/api/auth/reset-password?token="
                        + resetToken;

        emailService.sendEmail(
                user.getEmail(),
                "Reset Your Password",
                "Hello " + user.getName() + ",\n\n"
                        + "Click the following link to reset your password:\n"
                        + resetLink
                        + "\n\n"
                        + "This link will expire in 15 minutes."
        );
        logger.info("Password reset email sent successfully");
    }
    public void resetPassword(String token, String newPassword) {

        logger.info("Password reset initiated");

        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> {
                    logger.warn("Password reset failed: invalid token");
                    return new RuntimeException(
                            "Invalid or expired reset token"
                    );
                });

        if (user.getResetPasswordTokenExpiry() == null ||
                user.getResetPasswordTokenExpiry()
                        .isBefore(java.time.LocalDateTime.now())) {

            logger.warn("Password reset failed: token expired");
            throw new RuntimeException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);

        userRepository.save(user);

        logger.info("Password reset successful");
    }
}