package org.example.controller;

import org.example.model.Customer;
import org.example.service.CustomerService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @QueryMapping
    public Customer customerById(@Argument Integer id) {
        return customerService.customerById(id);
    }

    @MutationMapping
    public Customer insert(@Argument String name) {
        return customerService.insert(name);
    }
}
