package com.example.SecurityMicroservice.Services;

import com.example.SecurityMicroservice.Models.Role;
import com.example.SecurityMicroservice.Repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role createRole(String name) {
        Role role = new Role();
        role.setName(name);
        return roleRepository.save(role);
    }

    public void deleteRoleById(Long roleId) {
        roleRepository.deleteById(roleId);
    }

    public Role getRoleByName(String name){ return roleRepository.findByName(name); };
}
