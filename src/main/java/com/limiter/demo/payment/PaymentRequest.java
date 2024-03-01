package com.limiter.demo.payment;

import lombok.Data;

@Data
public class PaymentRequest {

    private String email;
    private String currency;
    private int amount;
    private String phone;
    private String reference;
    private String description;

}
