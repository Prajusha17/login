package com.example.loginpage.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.loginpage.service.authService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class authController {
	@Autowired
    private authService authService;

    // 1️⃣ Initiate Sign-up - Store in temp memory & send OTP
    @PostMapping("/signup")
    public ResponseEntity<String> initiateSignup(@RequestBody Map<String, String> requestData, HttpSession session) {
        String firstName = requestData.get("firstName");
        String lastName = requestData.get("lastName");
        String email = requestData.get("email");
        String password = requestData.get("password");
        String confirmPassword = requestData.get("confirmPassword"); // Accept confirmPassword
        
        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("Password and Confirm Password do not match!");
        }

        if (firstName == null || lastName == null || email == null || password == null || confirmPassword==null) {
            return ResponseEntity.badRequest().body("Missing required fields!");
        }

        String result = authService.initiateSignup(firstName, lastName, email, password, session);
        return ResponseEntity.ok(result);
    }

    // 2️⃣ Verify OTP
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> requestData,HttpSession session) {
        String email = requestData.get("email");
        String otp = requestData.get("otp");

        if (email == null || otp == null) {
            return ResponseEntity.badRequest().body("Email or OTP is missing!");
        }

        String verificationMessage = authService.verifyOtp(email, otp, session);
        return ResponseEntity.ok(verificationMessage);
    }

    // 3️⃣ Set User-name & Store User in DB
    @PostMapping("/setUsername")
    public String setUsername(@RequestParam("username") String username, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "Please verify your email first!";
        }

        String result = authService.setUsername(email, username);
        return result;
    }


    // 4️⃣ Login with User-name & Password
    @PostMapping("/login")
    public ResponseEntity<String> loginWithUsernamePassword(@RequestBody Map<String, String> requestData) {
        String username = requestData.get("username");
        String password = requestData.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username or Password is missing!");
        }

        String result = authService.loginWithUsernamePassword(username, password);
        return ResponseEntity.ok(result);
    }

    // 5️⃣ Google Authentication (Dummy)
    @PostMapping("/googleAuth")
    public ResponseEntity<String> googleAuth(@RequestBody Map<String, String> requestData) {
        String googleToken = requestData.get("token");

        if (googleToken == null) {
            return ResponseEntity.badRequest().body("Google authentication token is missing!");
        }

        String result = authService.googleAuth(googleToken);
        return ResponseEntity.ok(result);
    }

    // 6️⃣ Update User-name
    @PutMapping("/updateUsername")
    public ResponseEntity<String> updateUsername(@RequestBody Map<String, String> requestData) {
        String email = requestData.get("email");
        String newUsername = requestData.get("newUsername");

        if (email == null || newUsername == null) {
            return ResponseEntity.badRequest().body("Email or New Username is missing!");
        }

        String result = authService.updateUsername(email, newUsername);
        return ResponseEntity.ok(result);
    }
}

