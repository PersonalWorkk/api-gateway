package com.wedeliver.apigateway;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class IntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    public void test_restaurant_microservice_retrieve_all_restaurants() throws Exception{
        mockMvc.perform(get("http://20.126.245.90/api/restaurants/1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());     
    }
}
