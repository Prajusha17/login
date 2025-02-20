package com.example.loginpage.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.loginpage.model.User;
import com.example.loginpage.repo.userRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class authService {

    @Autowired
    private userRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private otpService otpService;
    
    private static final Map<String, User> tempUserStore = new HashMap<>();

    // 1️⃣ Initiate Sign-up - Store in temp memory & send OTP
    public String initiateSignup(String firstName, String lastName, String email, String password, HttpSession session) {
        if (userRepository.existsByEmail(email)) {
            return "Email is already registered!";
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Hash password
        user.setVerified(false); // Not verified yet

        // Store temporarily in memory
        tempUserStore.put(email, user);

        // Send OTP
        otpService.generateOtp(email);
        
        // Store email in session
        session.setAttribute("email", email);

        return "OTP sent to email. Please verify.";
    }

    // 2️⃣ Verify OTP
    public String verifyOtp(String email, String otp, HttpSession session) {
        if (!otpService.verifyOtp(email, otp)) {
            return "Invalid OTP!";
        }

        // Retrieve user from temp storage
        User user = tempUserStore.get(email);
        if (user == null) {
            return "User details not found! Please restart the signup process.";
        }

        user.setVerified(true); // Mark as verified (But not saved yet)
        session.setAttribute("email", email);  // Store the email in the session

        return "OTP verified! Now, set your username.";
    }

    // 3️⃣ Set User-name & Store User in DB
    @Transactional
    public String setUsername(String email, String username) {
        // Retrieve user from temp storage (ensuring OTP verification happened)
        User user = tempUserStore.get(email);

        if (user == null || !user.getIsVerified()) {
            return "User not verified or OTP incorrect!";
        }

        // Check if user-name is already taken
        if (userRepository.existsByUsername(username)) {
            return "Username is already taken!";
        }

        // Assign user-name
        user.setUsername(username);
        user.setActive(true);
        // Save user to database (only now)
        userRepository.save(user);

        // Remove from temporary storage after saving
        tempUserStore.remove(email);

        return "User registered successfully!";
    }

    // 4️⃣ Login with Username & Password
    public String loginWithUsernamePassword(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get(); // Extract the user object once

            if (!user.getIsActive()) {
                return "Account is inactive! Please complete registration.";
            }

            if (user.isVerified() && passwordEncoder.matches(password, user.getPassword())) {
                user.updateLastLogin(); // Update last login timestamp
                userRepository.save(user); // Save changes to the database
                return "Login successful!";
            }
        }
        
        return "Invalid credentials or user not verified!";
    }


    // 5️⃣ Google Authentication (Dummy)
    public String googleAuth(String googleToken) {
        return "Google Authentication Successful!";
    }

    // 6️⃣ Update Username
    @Transactional
    public String updateUsername(String email, String newUsername) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Check if the new username is already taken
            if (userRepository.existsByUsername(newUsername)) {
                return "Username already taken!";
            }

            user.setUsername(newUsername);
            userRepository.save(user);
            return "Username updated successfully!";
        }
        return "User not found!";
    }
}