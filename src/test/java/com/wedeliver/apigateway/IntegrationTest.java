package com.wedeliver.apigateway;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
public class IntegrationTest {
    
    private MockMvc mockMvc;

    @Test
    @Order(1)
    public void test_restaurant_microservice_retrieve_all_restaurants() throws Exception{
        mockMvc.perform(get("http://20.126.245.90/api/restaurants/1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(2)
    public void test_account_microservice_retrieve_all_users() throws Exception{
        mockMvc.perform(get("http://20.126.245.90/api/auth/users")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }
}
