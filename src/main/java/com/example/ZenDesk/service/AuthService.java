package com.example.ZenDesk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ZenDesk.dto.AuthResponseDto;
import com.example.ZenDesk.dto.RegisterRequestDto;
import com.example.ZenDesk.entity.AgentProfile;
import com.example.ZenDesk.entity.AppUser;
import com.example.ZenDesk.entity.Role;
import com.example.ZenDesk.exception.BusinessValidationException;
import com.example.ZenDesk.repository.AgentProfileRepository;
import com.example.ZenDesk.repository.AppUserRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    AppUserRepository userRepo;

    @Autowired
    AgentProfileRepository agentRepo;

    @Transactional
    public AuthResponseDto register(RegisterRequestDto dto)
    {
        if(userRepo.existsByEmail(dto.getEmail()))
        {
            throw new BusinessValidationException("Email already exists");
        }
        AppUser user=new AppUser();
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setRole(dto.getRole());
        user = userRepo.save(user);

        if(dto.getRole() == Role.SUPPORT_AGENT)
        {
            AgentProfile agent=new AgentProfile();
            agent.setUserId(user.getId());
            agent.setRole(user.getRole());
            agent.setDepartment("General Support");
            agentRepo.save(agent);
        }

        AuthResponseDto response=new AuthResponseDto();
        response.setMessage("Registered Successfully");
        response.setFullName(user.getFullName());
        response.setRole(user.getRole().name());
        return response;
        
    }
   
}
