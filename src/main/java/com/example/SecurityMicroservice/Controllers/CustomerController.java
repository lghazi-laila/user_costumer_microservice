package com.example.SecurityMicroservice.Controllers;

import com.example.SecurityMicroservice.DTO.Customer;
import com.example.SecurityMicroservice.Models.User;
import com.example.SecurityMicroservice.Services.CustomerService;
import com.example.SecurityMicroservice.Services.JwtService;
import com.example.SecurityMicroservice.Utils.CustomerMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@RestController
@RequestMapping("/v1/customers")
public class CustomerController {

    @Autowired
    private final CustomerService customerService;

    @GetMapping()
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<User> users = customerService.getAllCustomers();
        List<Customer> customer = users.stream()
                .map(c -> {
                    CustomerMapper customerProfile = new CustomerMapper();
                    return customerProfile.mapUserToCustomer(c);
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = customerService.getAllCustomers();
        List<Customer> customer = users.stream()
                .map(c -> {
                    CustomerMapper customerProfile = new CustomerMapper();
                    return customerProfile.mapUserToCustomer(c);
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String customerId) {
        User user = customerService.getCustomerById(customerId);

        if (user != null) {
            CustomerMapper customerProfile = new CustomerMapper();
            Customer customer = customerProfile.mapUserToCustomer(user);
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam("query") String query) {
        List<User> users = customerService.searchCustomers(query);

        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Customer> customers = users.stream()
                .map(user -> new CustomerMapper().mapUserToCustomer(user))
                .collect(Collectors.toList());

        return ResponseEntity.ok(customers);
    }

    @GetMapping("/byPageWithSort")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Page<Customer>> getCustomersByPageWithSort(
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "field", required = false) String field,
            @RequestParam(name = "sort", defaultValue = "ASC") String sort
    ) {
        if (!"ASC".equalsIgnoreCase(sort) && !"DESC".equalsIgnoreCase(sort)) {
            return ResponseEntity.badRequest().build();
        }

        Page<User> users = customerService.findCustomersWithPagination(offset, pageSize, field, sort);
        Page<Customer> customers = users.map(user -> new CustomerMapper().mapUserToCustomer(user));

        return new ResponseEntity<>(customers, HttpStatus.OK);
    }


    @GetMapping("/byPageWithQuery")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Page<Customer>> getCustomersByQueryByPageWithSort(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "field", required = false) String field,
            @RequestParam(name = "sort", defaultValue = "ASC") String sort
    ) {
        if (!"ASC".equalsIgnoreCase(sort) && !"DESC".equalsIgnoreCase(sort)) {
            return ResponseEntity.badRequest().build();
        }

        Page<User> users = customerService.searchCustomers(query, offset, pageSize, field, Sort.Direction.valueOf(sort));
        Page<Customer> customers = users.map(user -> new CustomerMapper().mapUserToCustomer(user));

        return new ResponseEntity<>(customers, HttpStatus.OK);
    }




    //----------------------------------------------------------------

    @Autowired
    private JwtService jwtService;

    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        customerService.deleteCustomer(customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

//    @PatchMapping("/delete") // soft delete customer
//    public ResponseEntity<Void> deactivateCustomer(@RequestHeader("Authorization") String token) {
//
//
//        String id = jwtService.extractUserId(token);
//
//        User user = customerService.getCustomerById(id);
//        if (user == null) {
//            return ResponseEntity.notFound().build();
//        }
//        user.setActive(false);
//        customerService.createCustomer(user);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
    @PatchMapping("/delete") // Soft delete customer
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<String> deactivateCustomer(@RequestHeader("Authorization") String token) {

        token = token.split(" ")[1].trim();
    String userId = jwtService.extractUserId(token);
    User user = customerService.getCustomerById(userId);

    if (user == null) {
        return ResponseEntity.notFound().build();
    }

    if (!user.isActive()) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Customer is already deactivated.");
    }

    // Perform the soft delete
    user.setActive(false);
    customerService.createCustomer(user);

    // You may want to log the soft delete operation here.

    return ResponseEntity.noContent().build();
}










    //----------------------------------------------------------------

    @PutMapping("/{customerId}")
    public ResponseEntity<User> updateCustomer(@PathVariable String customerId, @RequestBody User updatedUser) {
        User updated = customerService.updateCustomer(customerId, updatedUser);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/validate/{customerId}") // validate email
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Void> validateCustomer(@PathVariable String customerId) {
        User user = customerService.getCustomerById(customerId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if(user.isValidAccount()){
            return ResponseEntity.badRequest().build();
        }

        user.setValidAccount(true);
        customerService.createCustomer(user);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //----------------------------------------------------------------

    @PatchMapping(path = "/profile/update/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<User> updateCustomer(
            @PathVariable String id,
            @RequestBody JsonPatch patch) throws JsonPatchException, JsonProcessingException {

        User user = customerService.getCustomerById(id);
        User userPatched = customerService.applyPatchToCustomer(patch, user);
        return ResponseEntity.ok(userPatched);
    }

}
