package com.market.BuyFromHome.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.BuyFromHome.dto.requestDto.paymentRequest.PaymentRequestDto;
import com.market.BuyFromHome.dto.responseDto.paymentResponse.PaymentResponseDto;
import com.market.BuyFromHome.enums.PaymentMethod;
import com.market.BuyFromHome.enums.PaymentStatus;
import com.market.BuyFromHome.security.CurrentUserProvider;
import com.market.BuyFromHome.security.JwtUtil;
import com.market.BuyFromHome.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private CurrentUserProvider currentUserProvider;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when orderId is missing")
    void shouldReturn400WhenOrderIdIsMissing() throws Exception {
        String requestBody = """
                {
                    "paymentMethod": "CARD"
                }
                """;

        mockMvc.perform(post("/api/v1/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when paymentMethod is missing")
    void shouldReturn400WhenPaymentMethodIsMissing() throws Exception {
        String requestBody = """
                {
                    "orderId": 5
                }
                """;

        mockMvc.perform(post("/api/v1/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 201 when request is valid")
    void shouldReturn201WhenRequestIsValid() throws Exception {

        when(currentUserProvider.getCurrentUserId()).thenReturn(1L);

        PaymentRequestDto requestDto = PaymentRequestDto.builder()
                .orderId(5L)
                .paymentMethod(PaymentMethod.CARD)
                .build();

        PaymentResponseDto responseDto = PaymentResponseDto.builder()
                .paymentId(1L)
                .orderId(5L)
                .amount(new BigDecimal("5000.00"))
                .currency("NGN")
                .status(PaymentStatus.PENDING)
                .paymentMethod(PaymentMethod.CARD)
                .build();

        when(paymentService.createPayment(anyLong(), any(PaymentRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}