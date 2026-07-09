package io.snackdeal.backand.api.user.cs.controller;

import io.snackdeal.backand.api.user.cs.dto.FaqResponse;
import io.snackdeal.backand.domain.cs.entity.QnaType;
import io.snackdeal.backand.domain.cs.service.CsService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:test-config.properties")
class CsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CsService csService;

    @Disabled("TODO: implement")
    @Test
    @DisplayName("noticeList - TODO")
    void noticeList_Success() throws Exception {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("noticeDetail - TODO")
    void noticeDetail_Success() throws Exception {
        fail("not implemented");
    }

    @Test
    @DisplayName("faqList - 비로그인 사용자가 전체 FAQ 조회 성공")
    void faqList_Success() throws Exception {
        // given
        FaqResponse response = new FaqResponse(1L, QnaType.ORDER, "주문 내역은 어디서 확인하나요?", "로그인 후 마이페이지에서 확인할 수 있습니다.");
        given(csService.findFaqList(null)).willReturn(List.of(response));

        // when / then
        mockMvc.perform(get("/cs/qna/faq"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("주문 내역은 어디서 확인하나요?"));
    }

    @Test
    @DisplayName("faqList - 비로그인 사용자가 타입별 FAQ 조회 성공")
    void faqList_ByType_Success() throws Exception {
        // given
        FaqResponse response = new FaqResponse(1L, QnaType.ORDER, "주문 내역은 어디서 확인하나요?", "로그인 후 마이페이지에서 확인할 수 있습니다.");
        given(csService.findFaqList(QnaType.ORDER)).willReturn(List.of(response));

        // when / then
        mockMvc.perform(get("/cs/qna/faq").param("type", "ORDER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].type").value("ORDER"));
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("myQnaList - TODO")
    void myQnaList_Success() throws Exception {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("createQna - TODO")
    void createQna_Success() throws Exception {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("qnaDetail - TODO")
    void qnaDetail_Success() throws Exception {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("updateQna - TODO")
    void updateQna_Success() throws Exception {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("deleteQna - TODO")
    void deleteQna_Success() throws Exception {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("askChatbot - TODO")
    void askChatbot_Success() throws Exception {
        fail("not implemented");
    }

}
