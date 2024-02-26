package com.limiter.demo.rest.controllers;

import com.limiter.demo.models.Product;
import com.limiter.demo.models.Purchaseobject;
import com.limiter.demo.models.UserEntity;
import com.limiter.demo.repositories.ProductRepository;
import com.limiter.demo.repositories.PurchaseObjectRepo;
import com.limiter.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/auth")
@CrossOrigin(origins = "*")

public class PurchasedObjectController {
    @Autowired
    private PurchaseObjectRepo purchaseObjectRepo;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @PostMapping("product/{product_id}/purchase")
    public Object addProduct(@PathVariable long product_id)
    {
        Authentication auth  = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> client = userRepository.findByUsername(auth.getName());
        if(client.isPresent())
        {
            long c_id = client.get().getId();
            Optional<Product> product = productRepository.findById(product_id);
            if(product.isPresent())
            {
                Purchaseobject p = new Purchaseobject();
                p.setName(product.get().getName());
                p.setImage(product.get().getImage());
                p.setUser_id(c_id);
                p.setPrice(product.get().getPrice());
                p.setDescription(product.get().getDescription());
                p.setAddedDate(new Date());
                p.setBought(false);
                purchaseObjectRepo.save(p);
                return new ResponseEntity<>("Product Added to cart", HttpStatus.CREATED);
            }
            else {
                return new ResponseEntity<>("Product does not exist", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Please Log IN", HttpStatus.UNAUTHORIZED);
    }

}
