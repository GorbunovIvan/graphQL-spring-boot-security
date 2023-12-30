package org.example.controller;

import org.example.model.Customer;
import org.example.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTest {

    private HttpGraphQlTester graphQlTester;

    @MockBean
    private CustomerService customerService;

    private List<Customer> customers;

    private Customer newCustomer;

    @BeforeEach
    void setUp(@Autowired ApplicationContext applicationContext) {

        if (graphQlTester == null) {

            var webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
                    .configureClient()
                    .baseUrl("/graphql")
                    .build();

            graphQlTester = HttpGraphQlTester.create(webTestClient);
        }

        customers = new ArrayList<>(List.of(
                new Customer(1, "first name"),
                new Customer(2, "second name"),
                new Customer(3, "third name")
        ));

        newCustomer = new Customer(99, "new name");

        // Mocking
        when(customerService.insert(anyString())).thenReturn(newCustomer);

        for (var customer : customers) {
            when(customerService.customerById(customer.id())).thenReturn(customer);
        }
    }

    @Test
    void testCustomerById() {

        for (var customer : customers) {

            String query = """
                {
                    customerById(id: %d) {
                      id
                      name
                    }
                }
                """;

            query = String.format(query, customer.id());

            graphQlTester.document(query)
                    .execute()
                    .path("data.customerById")
                    .hasValue()
                    .entity(Customer.class)
                    .isEqualTo(customer);

            verify(customerService, times(1)).customerById(customer.id());
        }
    }

    @Test
    void testCustomerByIdNotFound() {

        int id = -1;

        String query = """
            {
                customerById(id: %d) {
                  id
                  name
                }
            }
            """;

        query = String.format(query, id);

        graphQlTester.document(query)
                .execute()
                .path("data.customerById")
                .valueIsNull();

        verify(customerService, times(1)).customerById(id);
    }

    @Test
    void testInsert() {

        String query = """
            mutation {
                insert(name: "%s") {
                  id
                  name
                }
            }
            """;

        query = String.format(query, newCustomer.name());

        var customerCreated = graphQlTester.document(query)
                .execute()
                .path("data.insert")
                .hasValue()
                .entity(Customer.class)
                .get();

        assertNotNull(customerCreated.id());
        assertEquals(newCustomer.name(), customerCreated.name());

        verify(customerService, times(1)).insert(newCustomer.name());
    }
}