package com.example.SecurityMicroservice.Services;

import com.example.SecurityMicroservice.Response.JwtAuthenticationResponse;
import com.example.SecurityMicroservice.Models.Role;
import com.example.SecurityMicroservice.Models.User;
import com.example.SecurityMicroservice.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.SecurityMicroservice.DTO.SignInRequest;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;




    public JwtAuthenticationResponse login (SignInRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword())
            );
        }catch (Exception e){
            throw new IllegalArgumentException("Invalid email or password.");
        }

        //find user by email
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        //check if user is active
//        if(!user.getActive()){
//            throw new IllegalArgumentException("User is not Active.");
//        }

        //update last login data
        var updatedUser = userService.updateLastLogin(user);
        var jwt = jwtService.generateToken(updatedUser);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    public JwtAuthenticationResponse addUser(User user, String roleName){
        if(userService.checkUserByUsername(user.getUsername())){
            throw new IllegalArgumentException("Username is already taken.");
        }
        if(userService.checkUserByEmail(user.getEmail())){
            throw new IllegalArgumentException("Email is already taken.");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(roleService.getRoleByName("CUSTOMER"));
        boolean isValid = false;

        if(roleName.equalsIgnoreCase("MANAGER")){
            roles.add(roleService.getRoleByName("MANAGER"));
            isValid = true;
        }

        var newUser = User
                .builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userName(user.getUserName())
                .password(passwordEncoder.encode(user.getPassword()))
                .roles(roles)
                .validAccount(isValid)
                .active(true)
                .build();

//        user = userService.save(newUser);

        User savedUser = userService.save(newUser);
        String userId = savedUser.getId();

        var jwt = jwtService.generateToken(newUser);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }


}

//    private final UserRepository userRepository;
//    private final UserService userService;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
//    private final AuthenticationManager authenticationManager;
//
//    public JwtAuthenticationResponse signup(SignUpRequest request) {
//        var user = User
//                .builder()
//                .firstName(request.getFirstName())
//                .lastName(request.getLastName())
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .role(Role.ROLE_USER)
//                .build();
//
//        user = userService.save(user);
//        var jwt = jwtService.generateToken(user);
//        return JwtAuthenticationResponse.builder().token(jwt).build();
//    }
//
//
//    public JwtAuthenticationResponse signin(SignInRequest request) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
//        var user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
//        var jwt = jwtService.generateToken(user);
//        return JwtAuthenticationResponse.builder().token(jwt).build();
//    }