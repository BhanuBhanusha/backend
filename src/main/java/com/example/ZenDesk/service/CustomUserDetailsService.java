package com.example.ZenDesk.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.ZenDesk.entity.AppUser;
import com.example.ZenDesk.repository.AppUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final AppUserRepository repo;

    public CustomUserDetailsService(AppUserRepository repo) {
        this.repo = repo;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
        AppUser user = repo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not Found"));
        return User.builder()
            .username(email)
            .password(user.getPassword())
            .roles(user.getRole().name())
            .build();
    }
}
