package com.limiter.demo.rest.controllers;

import com.limiter.demo.models.Product;
import com.limiter.demo.models.UserEntity;
import com.limiter.demo.repositories.ProductRepository;
import com.limiter.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/public")
@CrossOrigin(origins = "*")

public class PublicController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping("product/all")
    public Object viewAllProducts()
    {
        return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
    }
    @PostMapping("products/confirm")
    public Object confirmProducts(List<Product> products)
    {
        Authentication auth  = SecurityContextHolder.getContext().getAuthentication();

        Optional<UserEntity> client = userRepository.findByUsername(auth.getName());
        List<Double> sums= new ArrayList<>();
        if(client.isPresent())
        {

            for(Product p:products)
            {
               sums.add(p.getPrice()*p.getQuantity());
            }
            double sum=0;
            for(int i=0; i<sums.size(); i++)
            {
                sum = sums.get(i) + sum;
            }
            return new ResponseEntity<>("SUM IS: "+ sum,HttpStatus.OK);
        }
        return new ResponseEntity<>("Please Log IN", HttpStatus.UNAUTHORIZED);
    }

}
