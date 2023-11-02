package com.example.SecurityMicroservice.Utils;


import com.example.SecurityMicroservice.DTO.Customer;
import com.example.SecurityMicroservice.Models.User;

import java.util.Date;

public class CustomerMapper {


    public Customer mapUserToCustomer(User user) {
        if (user == null) {
            return null;
        }
        return new Customer(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getUsername()
        );
    }

    public User mapProfileToCustomer(Customer customer) {

        Date dateNow = new Date();  // Current date

        if (customer == null) {
            return null;
        }

        User user = new User();
        user.setFirstName(customer.getFirstName());
        user.setLastName(customer.getLastName());
        user.setEmail(customer.getEmail());
        user.setPassword(customer.getPassword());

        return user;
    }
}