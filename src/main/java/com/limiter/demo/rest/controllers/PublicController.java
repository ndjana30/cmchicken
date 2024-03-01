package com.limiter.demo.rest.controllers;

import com.limiter.demo.models.Product;
import com.limiter.demo.models.UserEntity;
import com.limiter.demo.payment.PaymentRequest;
import com.limiter.demo.payment.PaymentResponse;
import com.limiter.demo.payment.PaymentService;
import com.limiter.demo.repositories.ProductRepository;
import com.limiter.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("api/public")
@CrossOrigin(origins = "*")

public class PublicController {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${notchpay.api.url}")
    private String notchPayApiUrl;

    @Value("${notchpay.api.key}")
    private String notchPayApiKey;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping("product/all")
    public Object viewAllProducts() {
        return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping("products/confirm")
    public Object confirmProducts(@RequestBody List<Product> products) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> client = userRepository.findByUsername(auth.getName());
        List<Double> sums = new ArrayList<>();
        if (client.isPresent()) {
            for (Product p : products) {
                sums.add(p.getPrice() * p.getQuantity());
            }
            double sum = 0;
            for (int i = 0; i < sums.size(); i++) {
                sum = sums.get(i) + sum;
            }
            return new ResponseEntity<>("SUM IS: " + sum, HttpStatus.OK);
        }
        return new ResponseEntity<>("Please Log IN", HttpStatus.UNAUTHORIZED);
    }


    /*
    @RequestParam("email") String email,
                                @RequestParam("currency") String currency,
                                @RequestParam("amount") int amount,
                                @RequestParam("phone") String phone,
                                @RequestParam("reference") String reference,
                                @RequestParam("description") String description
    */
@PostMapping("product/buy")
    public PaymentResponse sendSomething( @RequestBody PaymentRequest request)
{
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.setAuthorization("Bearer " + notchPayApiKey);
    headers.setBearerAuth("Bearer " + notchPayApiKey);

    HttpEntity<PaymentRequest> entity = new HttpEntity<>(request, headers);

    return restTemplate.postForObject(notchPayApiUrl + "/payments/initialize", entity, PaymentResponse.class);
}



}
