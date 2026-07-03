package io.snackdeal.backand.api.user.delivery.controller;

import io.snackdeal.backand.domain.delivery.service.DeliveryService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:test-config.properties")
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DeliveryService deliveryService;

    @Disabled("TODO: implement")
    @Test
    @DisplayName("list - TODO")
    void list_Success() throws Exception {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("save - TODO")
    void save_Success() throws Exception {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("update - TODO")
    void update_Success() throws Exception {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("markDefault - TODO")
    void markDefault_Success() throws Exception {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("delete - TODO")
    void delete_Success() throws Exception {
        fail("not implemented");
    }

}