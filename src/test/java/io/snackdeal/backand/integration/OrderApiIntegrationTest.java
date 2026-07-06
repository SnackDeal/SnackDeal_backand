package io.snackdeal.backand.integration;

import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.domain.member.entity.Member;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import io.snackdeal.backand.domain.order.client.PortOneClient;
import io.snackdeal.backand.domain.order.client.dto.PortOnePayment;
import io.snackdeal.backand.domain.product.entity.Product;
import io.snackdeal.backand.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 주문/결제 + 관리자 주문관리 + 배송비 정책 API 를 HTTP 스택 전 구간(컨트롤러→서비스→리포지토리→H2)으로 검증한다.
 * 포트원 연동만 Mock 으로 대체하고, 인증은 Spring Security 테스트의 principal 주입으로 처리한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:test-config.properties")
@Transactional // 각 테스트를 롤백해 H2 데이터 격리 (관리자 목록은 전체 주문을 세므로 필수)
class OrderApiIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ProductRepository productRepository;

    @MockitoBean private PortOneClient portOneClient;

    private Member saveMember(String email, MemberRole role) {
        return memberRepository.save(Member.builder()
                .email(email).password("x").name("홍길동").phone("01011112222").role(role).build());
    }

    private Product saveProduct(long price, int stock) {
        return productRepository.save(Product.builder()
                .name("허니버터 프레첼").price(price).description("맛있음").stock(stock).categoryId(1L).build());
    }

    private RequestPostProcessor as(Member member) {
        UserDetails principal = new MemberDetails(
                member.getId(), member.getEmail(), member.getPassword(), member.getRole());
        return authentication(new UsernamePasswordAuthenticationToken(
                principal, "token", principal.getAuthorities()));
    }

    private JsonNode json(String body) {
        return objectMapper.readTree(body);
    }

    @Test
    @DisplayName("사용자 주문 전체 흐름: prepare → complete → list → 상세 → 환불요청")
    void userOrderFullFlow() throws Exception {
        Member user = saveMember("buyer@test.com", MemberRole.USER);
        Product product = saveProduct(4500L, 10);

        // 1) 주문 준비 — 상품 9,000 (기본 배송비 0) → amount 9,000
        String prepareBody = objectMapper.writeValueAsString(Map.of(
                "items", List.of(Map.of("productId", product.getId(), "quantity", 2)),
                "shipping", Map.of(
                        "receiverName", "홍길동", "receiverPhone", "01012345678",
                        "zipcode", "06133", "address", "서울 강남구 테헤란로 123",
                        "detailAddress", "456호", "deliveryRequest", "부재 시 문 앞")));

        String prepareResult = mockMvc.perform(post("/order/prepare").with(as(user))
                        .contentType(MediaType.APPLICATION_JSON).content(prepareBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.amount").value(9000))
                .andExpect(jsonPath("$.data.buyerEmail").value("buyer@test.com"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String paymentId = json(prepareResult).get("data").get("paymentId").asString();

        // 2) 결제 검증 — 포트원 V2 가 동일 금액(9,000) PAID 로 응답하도록 Mock
        when(portOneClient.getPayment(any())).thenReturn(new PortOnePayment(
                paymentId, 9000L, "PAID", "Card", "TOSSPAYMENTS", "http://receipt", LocalDateTime.now()));

        String completeBody = objectMapper.writeValueAsString(Map.of("paymentId", paymentId));

        String completeResult = mockMvc.perform(post("/order/complete").with(as(user))
                        .contentType(MediaType.APPLICATION_JSON).content(completeBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAYMENT_COMPLETED"))
                .andExpect(jsonPath("$.data.payment.status").value("PAID"))
                .andExpect(jsonPath("$.data.payment.paymentId").value(paymentId))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        long orderId = json(completeResult).get("data").get("orderId").asLong();

        // 재고 차감 확인 (10 - 2 = 8)
        org.junit.jupiter.api.Assertions.assertEquals(8, productRepository.findById(product.getId()).get().getStock());

        // 3) 주문내역 조회
        mockMvc.perform(get("/order/list").with(as(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.orders[0].mainProductName").value("허니버터 프레첼"));

        // 4) 주문 상세
        mockMvc.perform(get("/order/{id}", orderId).with(as(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAYMENT_COMPLETED"))
                .andExpect(jsonPath("$.data.items[0].lineTotal").value(9000))
                .andExpect(jsonPath("$.data.shipping.receiverName").value("홍길동"));

        // 5) 환불 요청
        mockMvc.perform(post("/order/{id}/refund", orderId).with(as(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("reason", "단순 변심"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("REFUND_REQUESTED"));
    }

    @Test
    @DisplayName("사용자 결제 검증: 금액 위변조 시 422 + 포트원 결제취소 호출")
    void completeAmountMismatch() throws Exception {
        Member user = saveMember("hacker@test.com", MemberRole.USER);
        Product product = saveProduct(4500L, 10);

        String prepareBody = objectMapper.writeValueAsString(Map.of(
                "items", List.of(Map.of("productId", product.getId(), "quantity", 2)),
                "shipping", Map.of("receiverName", "홍길동", "receiverPhone", "01012345678",
                        "zipcode", "06133", "address", "서울")));
        String prepareResult = mockMvc.perform(post("/order/prepare").with(as(user))
                        .contentType(MediaType.APPLICATION_JSON).content(prepareBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String paymentId = json(prepareResult).get("data").get("paymentId").asString();

        // 실제 결제 금액이 100원으로 조작된 상황
        when(portOneClient.getPayment(any())).thenReturn(new PortOnePayment(
                paymentId, 100L, "PAID", "Card", "TOSSPAYMENTS", "http://receipt", LocalDateTime.now()));

        mockMvc.perform(post("/order/complete").with(as(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("paymentId", paymentId))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("OR006"));

        org.mockito.Mockito.verify(portOneClient).cancelPayment(org.mockito.ArgumentMatchers.eq(paymentId), any());
    }

    @Test
    @DisplayName("타인의 주문 상세 접근은 403")
    void othersOrderForbidden() throws Exception {
        Member owner = saveMember("owner@test.com", MemberRole.USER);
        Member other = saveMember("other@test.com", MemberRole.USER);
        Product product = saveProduct(4500L, 10);

        String prepareBody = objectMapper.writeValueAsString(Map.of(
                "items", List.of(Map.of("productId", product.getId(), "quantity", 1)),
                "shipping", Map.of("receiverName", "홍길동", "receiverPhone", "01012345678",
                        "zipcode", "06133", "address", "서울")));
        String prepareResult = mockMvc.perform(post("/order/prepare").with(as(owner))
                        .contentType(MediaType.APPLICATION_JSON).content(prepareBody))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String paymentId = json(prepareResult).get("data").get("paymentId").asString();

        when(portOneClient.getPayment(any())).thenReturn(new PortOnePayment(
                paymentId, 4500L, "PAID", "Card", "TOSSPAYMENTS", "http://receipt", LocalDateTime.now()));
        String completeResult = mockMvc.perform(post("/order/complete").with(as(owner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("paymentId", paymentId))))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        long orderId = json(completeResult).get("data").get("orderId").asLong();

        mockMvc.perform(get("/order/{id}", orderId).with(as(other)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("OR009"));
    }

    @Test
    @DisplayName("관리자 주문관리: 리스트 → 상세 → 상태변경(배송준비중)")
    void adminOrderManagement() throws Exception {
        Member user = saveMember("cust@test.com", MemberRole.USER);
        Member admin = saveMember("admin@test.com", MemberRole.ADMIN);
        Product product = saveProduct(4500L, 10);

        placePaidOrder(user, product, 9000L);
        long orderId = orderIdOf(user);

        // 리스트
        mockMvc.perform(get("/admin/order").with(as(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.orders[0].buyerEmail").value("cust@test.com"));

        // 상세
        mockMvc.perform(get("/admin/order/{id}", orderId).with(as(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.payment.pgProvider").value("TOSSPAYMENTS"))
                .andExpect(jsonPath("$.data.manualOverride").value(false));

        // 상태 변경 PAYMENT_COMPLETED → PREPARING_SHIPMENT
        mockMvc.perform(patch("/admin/order/{id}/status", orderId).with(as(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "PREPARING_SHIPMENT"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PREPARING_SHIPMENT"))
                .andExpect(jsonPath("$.data.manualOverride").value(true));

        // 잘못된 전이: 배송완료로 바로 점프 불가 → 422
        mockMvc.perform(patch("/admin/order/{id}/status", orderId).with(as(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "PAYMENT_COMPLETED"))))
                .andExpect(status().isBadRequest()); // PAYMENT_COMPLETED 는 지정 불가 상태값(OR011)
    }

    @Test
    @DisplayName("관리자 배송비 정책: 조회 후 변경")
    void adminShippingPolicy() throws Exception {
        Member admin = saveMember("admin2@test.com", MemberRole.ADMIN);

        mockMvc.perform(get("/admin/shipping-policy").with(as(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.baseFee").value(0))
                .andExpect(jsonPath("$.data.freeThreshold").value(20000));

        mockMvc.perform(patch("/admin/shipping-policy").with(as(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("baseFee", 2500))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.baseFee").value(2500))
                .andExpect(jsonPath("$.data.freeThreshold").value(20000));
    }

    @Test
    @DisplayName("일반 사용자는 관리자 API 접근 불가 (403)")
    void userCannotAccessAdmin() throws Exception {
        Member user = saveMember("plain@test.com", MemberRole.USER);
        mockMvc.perform(get("/admin/order").with(as(user)))
                .andExpect(status().isForbidden());
    }

    // 결제까지 완료된 주문을 만들고 paymentId 를 반환한다.
    private String placePaidOrder(Member user, Product product, long amount) throws Exception {
        String prepareBody = objectMapper.writeValueAsString(Map.of(
                "items", List.of(Map.of("productId", product.getId(), "quantity", 2)),
                "shipping", Map.of("receiverName", "홍길동", "receiverPhone", "01012345678",
                        "zipcode", "06133", "address", "서울")));
        String prepareResult = mockMvc.perform(post("/order/prepare").with(as(user))
                        .contentType(MediaType.APPLICATION_JSON).content(prepareBody))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String paymentId = json(prepareResult).get("data").get("paymentId").asString();

        when(portOneClient.getPayment(any())).thenReturn(new PortOnePayment(
                paymentId, amount, "PAID", "Card", "TOSSPAYMENTS", "http://receipt", LocalDateTime.now()));
        mockMvc.perform(post("/order/complete").with(as(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("paymentId", paymentId))))
                .andExpect(status().isOk());
        return paymentId;
    }

    // 사용자의 첫 주문 id 를 목록 API 로 조회한다.
    private long orderIdOf(Member user) throws Exception {
        String listResult = mockMvc.perform(get("/order/list").with(as(user)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return json(listResult).get("data").get("orders").get(0).get("orderId").asLong();
    }
}
