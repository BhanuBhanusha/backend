package com.example.ZenDesk.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ZenDesk.dto.AuthRequestDto;
import com.example.ZenDesk.dto.AuthResponseDto;
import com.example.ZenDesk.dto.RegisterRequestDto;
import com.example.ZenDesk.entity.AppUser;
import com.example.ZenDesk.repository.AppUserRepository;
import com.example.ZenDesk.service.AuthService;
import com.example.ZenDesk.service.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthController(AuthenticationManager authenticationManager,JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService=jwtService;
    }

    @Autowired
    AuthService service;

    @Autowired
    AppUserRepository userRepo;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto dto)
    {
        AuthResponseDto response=service.register(dto);
        return ResponseEntity.status(201).body(response);
    }
   
    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody AuthRequestDto user)
    {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        AppUser appUser = userRepo.findByEmail(user.getEmail()).orElseThrow(() -> new com.example.ZenDesk.exception.ResourceNotFoundException("User not found"));
        String token=jwtService.generateToken(appUser);
        Map<String,String> res=new HashMap<>();
        res.put("message","login successfull");
        res.put("token",token);
        return ResponseEntity.ok(res);
        
    }

}
