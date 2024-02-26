package com.limiter.demo.rest.controllers;

import com.limiter.demo.models.Availability;
import com.limiter.demo.models.Product;
import com.limiter.demo.models.UserEntity;
import com.limiter.demo.repositories.ProductRepository;
import com.limiter.demo.repositories.PurchaseObjectRepo;
import com.limiter.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/auth")
@CrossOrigin(origins = "*")

public class ProductController {
    private final UserRepository userRepository;
    private final PurchaseObjectRepo purchaseObjectRepo;
    private final ProductRepository productRepository;
    @Autowired
    ProductController(UserRepository userRepository, PurchaseObjectRepo purchaseObjectRepo, ProductRepository productRepository)
    {
        this.userRepository=userRepository;
        this.purchaseObjectRepo = purchaseObjectRepo;
        this.productRepository=productRepository;
    }
    @PostMapping("product/{quantity}/create")
    public Object addProduct
            (@PathVariable int quantity,
             @RequestParam("name") String name,
             @RequestParam("image") MultipartFile image,
             @RequestParam("price") double price,
             @RequestParam("description") String description) throws IOException
    {
        Product p = new Product();
        p.setAddedDate(new Date());
        p.setDescription(description);
        p.setQuantity(quantity);
        p.setPrice(Double.valueOf(price));
        p.setName(name);
        try {
            p.setImage(image.getBytes());
        }
        catch (IOException exception)
        {
            return "Image could not be uploaded: "+ exception.getCause()+ exception.getMessage();
        }
        productRepository.save(p);
        return new ResponseEntity<>("Product added", HttpStatus.CREATED);
    }

    @PutMapping("product/{id}/{quantity}/increment")
    public Object incrementProduct
            (@PathVariable int quantity,
             @PathVariable long id
             )
    {
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent())
        {
            product.get().setQuantity(quantity+product.get().getQuantity());
            productRepository.save(product.get());
            return new ResponseEntity<>("Product Quantity incremented", HttpStatus.OK);
        }

        return new ResponseEntity<>("COULD NOT FIND PRODUCT", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("product/{id}/{quantity}/decrement")
    public Object decrementProduct
            (@PathVariable int quantity,
             @PathVariable long id
            )
    {
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent())
        {
            if(quantity > product.get().getQuantity())
            {
                return new ResponseEntity<>("CANNOT REMOVE MORE THAN PRESENT QUANTITY",HttpStatus.BAD_REQUEST);
            }
            else if(quantity <= product.get().getQuantity())
            {
                product.get().setQuantity(product.get().getQuantity()-quantity);
                productRepository.save(product.get());
                return new ResponseEntity<>("Product Quantity decremented", HttpStatus.OK);
            }


        }

        return new ResponseEntity<>("COULD NOT FIND PRODUCT", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("product/{id}/update")
    public Object modifyProduct(@PathVariable long id,
                                @RequestParam("description") String description,
                                @RequestParam("quantity") int quantity,
                                @RequestParam("price") double price,
                                @RequestParam("image") MultipartFile image,
                                @RequestParam("name") String name) throws IOException

    {
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent())
        {
            product.get().setPrice(price);
            product.get().setDescription(description);
            product.get().setQuantity(quantity);
            product.get().setName(name);
            product.get().setImage(image.getBytes());
            productRepository.save(product.get());
            return new ResponseEntity<>("Product "+product.get().getName()+ "updated", HttpStatus.OK);
        }
        return new ResponseEntity<>("UNABLE TO FIND PRODUCT", HttpStatus.BAD_REQUEST);

    }


    @DeleteMapping("product/{id}/delete")
    public Object deleteProduct(@PathVariable long id) throws IOException
//
    {
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent())
        {

            productRepository.deleteById(id);
            return new ResponseEntity<>("Product "+product.get().getName()+ "deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("UNABLE TO FIND PRODUCT, CANNOT DELETE IT", HttpStatus.BAD_REQUEST);

    }


}
