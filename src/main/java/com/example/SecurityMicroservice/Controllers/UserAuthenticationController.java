package com.example.SecurityMicroservice.Controllers;

import com.example.SecurityMicroservice.Response.JwtAuthenticationResponse;
import com.example.SecurityMicroservice.DTO.SignInRequest;
import com.example.SecurityMicroservice.Models.User;
import com.example.SecurityMicroservice.Response.ResponseHandler;
import com.example.SecurityMicroservice.Services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserAuthenticationController {

    @Autowired
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody SignInRequest request) {
        try {
            JwtAuthenticationResponse response = authenticationService.login(request);
            return ResponseHandler.generateResponse("login success", HttpStatus.OK,response);
        }catch (Exception e){
            return ResponseHandler.generateResponse(e.getMessage(),HttpStatus.UNAUTHORIZED,null );
        }
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> createCustomerAccount (@RequestBody User user){
        try{
            JwtAuthenticationResponse response = authenticationService.addUser(user,"MANAGER");
            return ResponseHandler.generateResponse("user created successfully", HttpStatus.OK,response);
        }catch (Exception e){
            return ResponseHandler.generateResponse(e.getMessage(),HttpStatus.BAD_REQUEST,null);
        }
    }
}
