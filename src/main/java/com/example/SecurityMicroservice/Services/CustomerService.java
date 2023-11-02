package com.example.SecurityMicroservice.Services;


import com.example.SecurityMicroservice.Models.User;
import com.example.SecurityMicroservice.Repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Data
@AllArgsConstructor
@Service
public class CustomerService {

    private final UserRepository userRepository;

    public List<User> getAllCustomers() {
        return userRepository.findAll();
    }

    public User getCustomerById(String customerId) {
        return userRepository.findById(customerId).orElse(null);
    }

    public User findCustomerById(String customerId) {
        return userRepository.findById(customerId).orElse(null);
    }

    public boolean isEmailUnique(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        return existingUser.isEmpty();
    }

    public List<User> getCustomerByFirstName(String customerFirstName) {
        return userRepository.findByFirstName(customerFirstName);
    }

    public List<User> findCustomersByFirstName(String firstName) {
        return userRepository.findByFirstName(firstName);
    }


    public List<User> findCustomersWithSorting(String field) {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, field));
    }


    public Page<User> findCustomersWithPagination(int offset, int pageSize) {
        return userRepository.findAll(PageRequest.of(offset, pageSize));
    }

    public Page<User> findCustomersWithPagination(int offset, int pageSize, String field) {
        return userRepository.findAll(PageRequest.of(offset, pageSize, Sort.by(Sort.Direction.DESC, field)));
    }


    public Page<User> findCustomersWithPagination(int offset, int pageSize, String field, String sort) {

        Sort.Direction direction = Sort.Direction.DESC;

        if ("ASC".equalsIgnoreCase(sort)) {
            direction = Sort.Direction.ASC;
        }
        return userRepository.findAll(PageRequest.of(offset, pageSize, Sort.by(direction, field)));
    }

    //----------------------------------------------------------------
    @PersistenceContext
    private EntityManager entityManager;

    public List<User> searchCustomers(String query) {
        String jpqlQuery = "SELECT u FROM User u " +  // Entity name is "User," not "users"
                "WHERE u.firstName LIKE :query " +
                "OR u.lastName LIKE :query " +
                "OR u.email LIKE :query";

        List<User> users = entityManager.createQuery(jpqlQuery, User.class)
                .setParameter("query", "%" + query + "%")
                .getResultList();

        return users;
    }


    public Page<User> searchCustomers(String query, int offset, int pageSize, String field, Sort.Direction direction) {
        String jpqlQuery = "SELECT u FROM User u " +
                "WHERE u.firstName LIKE :searchQuery " +
                "OR u.lastName LIKE :searchQuery " +
                "OR u.email LIKE :searchQuery ";

        // Add the ORDER BY clause based on the field and direction
        if (field != null && direction != null) {
            jpqlQuery += "ORDER BY u." + field + " " + (direction == Sort.Direction.ASC ? "ASC" : "DESC");
        }

        TypedQuery<User> jpqlTypedQuery = entityManager.createQuery(jpqlQuery, User.class)
                .setParameter("searchQuery", "%" + query + "%");

        // Execute the query and get the result list
        List<User> users = jpqlTypedQuery
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();

        // Create a Page object
        long totalUsers = users.size(); // You may need to count total users differently
        Pageable pageable = PageRequest.of(offset, pageSize);
        return new PageImpl<>(users, pageable, totalUsers);
    }



    //----------------------------------------------------------------

    public User createCustomer(User user) {
        return userRepository.save(user);
    }

    //----------------------------------------------------------------

    public void deleteCustomer(String customerId) {
        userRepository.deleteById(customerId);
    }

    //----------------------------------------------------------------

    public User updateCustomer(String customerId, User updatedUser) {
        User existingUser = userRepository.findById(customerId).orElse(null);
        if (existingUser != null) {
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPassword(updatedUser.getPassword());

            return userRepository.save(existingUser);
        } else {
            return null;
        }
    }

    //----------------------------------------------------------------

    public User applyPatchToCustomer(JsonPatch patch, User targetUser) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(targetUser, JsonNode.class));
        return objectMapper.treeToValue(patched, User.class);
    }

}
