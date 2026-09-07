package com.market.BuyFromHome.service;

import com.market.BuyFromHome.repository.OrderRepository;
import com.market.BuyFromHome.repository.PaymentRepository;
import com.market.BuyFromHome.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final String DEFAULT_CURRENCY = "NGN";

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
}
