package com.limiter.demo.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class PaymentService {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${notchpay.api.url}")
    private String notchPayApiUrl;

    @Value("${notchpay.api.key}")
    private String notchPayApiKey;

    public PaymentResponse initializePayment(PaymentRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.setAuthorization("Bearer " + notchPayApiKey);
        headers.setBearerAuth("Bearer " + notchPayApiKey);

        HttpEntity<PaymentRequest> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject(notchPayApiUrl + "/payments/initialize", entity, PaymentResponse.class);
    }
}
