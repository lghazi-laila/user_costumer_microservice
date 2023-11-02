package com.example.SecurityMicroservice.Config;

import com.example.SecurityMicroservice.Models.User;
import com.example.SecurityMicroservice.Models.Role;
import com.example.SecurityMicroservice.Repositories.RoleRepository;
import com.example.SecurityMicroservice.Repositories.UserRepository;
import com.example.SecurityMicroservice.Services.RoleService;
import com.example.SecurityMicroservice.Services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeedDataConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    @Override
    public void run(String... args) throws Exception {

        if(roleRepository.count() == 0){
            roleService.createRole("ADMIN");
            roleService.createRole("MANAGER");
            roleService.createRole("CUSTOMER");
        }

        if (userRepository.count() == 0) {

            Role adminRole = roleService.getRoleByName("ADMIN");
            Role managerRole = roleService.getRoleByName("MANAGER");
            Role customerRole = roleService.getRoleByName("CUSTOMER");

            User admin = User
                    .builder()
                    .firstName("admin")
                    .lastName("admin")
                    .userName("admin")
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("password"))
                    .roles(Set.of(adminRole,managerRole,customerRole))
                    .validAccount(true)
                    .active(true)
                    .build();

            userService.save(admin);
            log.debug("created ADMIN user - {}", admin);
        }
    }


}