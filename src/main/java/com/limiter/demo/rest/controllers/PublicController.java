package com.limiter.demo.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.limiter.demo.models.Product;
import com.limiter.demo.models.Purchaseobject;
import com.limiter.demo.models.UserEntity;
import com.limiter.demo.payment.Code;
import com.limiter.demo.payment.PaymentRequest;
import com.limiter.demo.payment.PaymentResponse;
import com.limiter.demo.payment.PaymentService;
import com.limiter.demo.repositories.ProductRepository;
import com.limiter.demo.repositories.PurchaseObjectRepo;
import com.limiter.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.logging.Logger;

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
    @Autowired
    PurchaseObjectRepo purchaseObjectRepo;
    Code c1 = new Code();

    @GetMapping("product/all")
    public Object viewAllProducts() {
        return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
    }

    private static final String API_URL = "https://api.notchpay.co/payments/initialize";
    private static final String VERIFY_URL = "https://api.notchpay.co/payments/";
    public List<Product> z = new ArrayList<>();

    @PostMapping("products/confirm")
    public Object confirmProducts(@RequestBody List<Product> products) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> client = userRepository.findByUsername(auth.getName());
        List<Double> sums = new ArrayList<>();
//        if (client.isPresent()) {
        try {
            for (Product p : products) {
                sums.add(p.getPrice() * p.getQuantity());
                z.add(p);
            }
            double sum = 0;
            for (int i = 0; i < sums.size(); i++) {
                sum = sums.get(i) + sum;
            }
            return new ResponseEntity<>("SUM IS: " + sum, HttpStatus.OK);
        }
        catch (Exception ex)
        {
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
        }
//        }
//        return new ResponseEntity<>("Please Log IN", HttpStatus.UNAUTHORIZED);
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
    public Object sendSomething( @RequestParam("email") String email,
                                 @RequestParam("currency") String currency,
                                 @RequestParam("amount") int amount,
                                 @RequestParam("phone") String phone,
                                 @RequestParam("reference") String reference,
                                 @RequestParam("description") String description)
{
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Optional<UserEntity> user = userRepository.findByUsername(auth.getName());
    if(user.isPresent()) {
        List<String> finalResult = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", notchPayApiKey);
        headers.set("Accept", "application/json");

        // Prepare form data
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("email", email);
        formData.add("currency", currency);
        formData.add("amount", String.valueOf(amount));
        formData.add("phone", phone);
        formData.add("reference", reference);
        formData.add("description", description);

        // Create request entity
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        // Send POST request
        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, requestEntity, String.class);
        Map<Object, Object> map = new HashMap<>();
        String[] elements = response.getBody().split(":");
        String[] value = elements[25].split(",");
        String code = value[0].replaceAll("^\"|\"$|\\\"", "");
        c1.setContent(code);
        c1.setReference(reference);

        map.put(getPaymentStatus(), updatePayment(c1.getContent()));

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                Purchaseobject po = new Purchaseobject();
                String[] typ = getPaymentStatus().split(":");
                String[] tyl = typ[33].split(",");
                String status = tyl[0].replaceAll("^\"|\"$|\\\"", "");
                System.out.println(status);
                if(status.equals("pending") || status.equals("failed") || status.equals("expired"))
                {
                    System.out.println("ACTION FAILED AFTER 70 SECONDS");
                }
                else if (status.equals("complete"))
                {
                    System.out.println("THE STATUS IS COMPLETED");
                    if(z.isEmpty())
                    {
                        System.out.println("NO PRODUCTS");
                    }
                    else {
                        for (int i = 0; i < z.size(); i++) {
                            po.setQuantity(z.get(i).getQuantity());
                            po.setPrice(z.get(i).getPrice());
                            po.setImage(z.get(i).getImage());
                            po.setDescription(z.get(i).getDescription());
                            po.setName(z.get(i).getName());
                            po.setBought(true);
                            po.setUser_id(user.get().getId());
                            purchaseObjectRepo.save(po);
                            System.out.println("ACTION COMPLETED AFTER 70 SECONDS");
                        }
                    }
                }
//               String tt = typ[28].split(",")[0].replaceAll("^\"|\"$|\\\"", "");

//                finalResult.add(tt);
                /*for (String g : finalResult)
                {
                    if (g.equals("pending") || g.equals("failed") || g.equals("expired")) {
                        System.out.println("ACTION FAILED AFTER 30 SECONDS");
                    } else if (g.equals("completed"))
                    {

                            for(int i=0;i<z.size();i++ ) {
                                po.setQuantity(z.get(i).getQuantity());
                                po.setPrice(z.get(i).getPrice());
                                po.setImage(z.get(i).getImage());
                                po.setDescription(z.get(i).getDescription());
                                po.setName(z.get(i).getName());
                                po.setBought(true);
                                po.setUser_id(user.get().getId());
                                purchaseObjectRepo.save(po);
                            System.out.println("ACTION COMPLETED AFTER 30 SECONDS");
                        }
                    }
                }*/

            }
        };
        long delay = 70 * 1000; //70 seconds in milliseconds
        timer.schedule(task, delay);
        // Return response body

        return map;
    }
    return new ResponseEntity<>("Please login",HttpStatus.UNAUTHORIZED);
}

@GetMapping("transaction/verify")
public String getPaymentStatus() {
    RestTemplate restTemplate = new RestTemplate();

    // Prepare headers
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", notchPayApiKey);
    headers.set("Accept", "application/json");

    // Create request entity
    HttpEntity<String> requestEntity = new HttpEntity<>(headers);

    // Send GET request
    ResponseEntity<String> response = restTemplate.exchange(VERIFY_URL+c1.getContent(), HttpMethod.GET, requestEntity, String.class);
    System.out.println(VERIFY_URL+c1.getContent());
    // Return response body
    return response.getBody();
}
    public String updatePayment(String reference) {
        RestTemplate restTemplate = new RestTemplate();

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", notchPayApiKey);

        // Prepare request body
        ObjectMapper mapper = new ObjectMapper();
        String jsonBody = "{ \"channel\": \"cm.mobile\", \"data\" : { \"phone\": \"+237682193701\" } }";
//        String jsonBody = "{ \"channel\" : \"cm.orange\" , data : { phone: +237699189765 } }";

        // Create request entity
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        // Send PUT request
        ResponseEntity<String> response = restTemplate.exchange(VERIFY_URL+reference, HttpMethod.PUT, requestEntity, String.class);
        // Return response body
        return response.getBody();
    }


/*
* @GetMapping("transaction/verify")
public String getPaymentStatus(String reference) {
    RestTemplate restTemplate = new RestTemplate();

    // Prepare headers
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", notchPayApiKey);
    headers.set("Accept", "application/json");

    // Create request entity
    HttpEntity<String> requestEntity = new HttpEntity<>(headers);

    // Send GET request
    ResponseEntity<String> response = restTemplate.exchange(VERIFY_URL, HttpMethod.GET, requestEntity, String.class);

    // Return response body
    return response.getBody();
}*/

}
