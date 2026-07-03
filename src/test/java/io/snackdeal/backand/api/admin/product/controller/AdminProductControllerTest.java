package io.snackdeal.backand.api.admin.product.controller;

import io.snackdeal.backand.domain.product.service.AdminProductService;
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
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminProductService adminProductService;

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
    @DisplayName("findById - TODO")
    void findById_Success() throws Exception {
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
    @DisplayName("changeStatus - TODO")
    void changeStatus_Success() throws Exception {
        fail("not implemented");
    }

}