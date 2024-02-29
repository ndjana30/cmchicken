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

import java.util.*;

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
    /*@PostMapping("product/{product_id}/purchase/{quantity}")
    public Object addProduct(@PathVariable long product_id,@PathVariable int quantity)
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
                p.setQuantity(quantity);
                purchaseObjectRepo.save(p);
                return new ResponseEntity<>("Product Added to cart", HttpStatus.CREATED);
            }
            else {
                return new ResponseEntity<>("Product does not exist", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Please Log IN", HttpStatus.UNAUTHORIZED);
    }
*/

    @PostMapping("products/confirm")
    public Object confirmProducts(List<Product> products)
    {
        Authentication auth  = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> client = userRepository.findByUsername(auth.getName());
        List<Double> sums= new ArrayList<>();
        Map<Double,Integer> values = new HashMap<>();
        if(client.isPresent())
        {
            for(Product p:products)
            {
                values.put(p.getPrice(),p.getQuantity());
            }
            for(Map.Entry<Double,Integer> entry: values.entrySet())
            {
                Double key = entry.getKey();
                Integer value = entry.getValue();
                Double result = key*value;
                sums.add(result);
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
