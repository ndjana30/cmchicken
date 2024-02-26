package com.limiter.demo.rest.controllers;

import com.limiter.demo.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/public")
@CrossOrigin(origins = "*")

public class PublicController {
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("product/all")
    public Object viewAllProducts()
    {
        return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
    }
}
