package com.example.SecurityMicroservice.Services;

import com.example.SecurityMicroservice.Models.User;
import com.example.SecurityMicroservice.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }

    public User loadUserById(String Id){
        return userRepository.findById(Id).orElseThrow(()-> new UsernameNotFoundException("User not found"));

    }

    public User save(User newUser) {
        if (newUser.getId() == null) {
            newUser.setCreationDate(LocalDateTime.now());
        }
        newUser.setLastUpdate(LocalDateTime.now());
        newUser.setLastLogin(LocalDateTime.now());
        return userRepository.save(newUser);
    }

    public User updateLastLogin(User user){
        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }

    public boolean checkUserByUsername(String userName){
        return userRepository.existsByUserName(userName);
    }

    public boolean checkUserByEmail(String userName){
        return userRepository.existsByEmail(userName);
    }


}
