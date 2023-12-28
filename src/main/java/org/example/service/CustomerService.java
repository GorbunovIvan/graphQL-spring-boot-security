package org.example.service;

import org.example.model.Customer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    private final List<Customer> customers = new ArrayList<>(List.of(
            new Customer(1, "Bob"),
            new Customer(2, "Maria"),
            new Customer(3, "Steve")
    ));

    public Customer customerById(Integer id) {
        return customers.stream()
                .filter(c -> c.id() != null && c.id().equals(id))
                .findAny()
                .orElse(null);
    }

    public Customer insert(String name) {

        int maxId = customers.stream()
                .mapToInt(Customer::id)
                .max()
                .orElse(0);

        var customer = new Customer(maxId + 1, name);
        customers.add(customer);

        return customer;
    }
}
