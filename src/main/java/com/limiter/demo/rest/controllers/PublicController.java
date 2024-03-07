package com.limiter.demo.rest.controllers;
import org.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.limiter.demo.models.Product;
import com.limiter.demo.models.Purchaseobject;
import com.limiter.demo.models.TemporaryObject;
import com.limiter.demo.models.UserEntity;
import com.limiter.demo.payment.Code;
import com.limiter.demo.payment.PaymentRequest;
import com.limiter.demo.payment.PaymentResponse;
import com.limiter.demo.payment.PaymentService;
import com.limiter.demo.repositories.ProductRepository;
import com.limiter.demo.repositories.PurchaseObjectRepo;
import com.limiter.demo.repositories.TemporaryObjectRepo;
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
    @Autowired
    TemporaryObjectRepo temporaryObjectRepo;
    Code c1 = new Code();

    @GetMapping("product/all")
    public Object viewAllProducts() {
        return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
    }

    private static final String API_URL = "https://api.notchpay.co/payments/initialize";
    private static final String VERIFY_URL = "https://api.notchpay.co/payments/";

    @PostMapping("products/confirm")
    public Object confirmProducts(@RequestBody List<Product> products) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> client = userRepository.findByUsername(auth.getName());
        List<Double> sums = new ArrayList<>();


        try {
            for(Product p: products)
            {
                TemporaryObject to = new TemporaryObject();
                to.setId(p.getId());
                to.setName(p.getName());
                to.setDescription(p.getDescription());
                to.setPrice(p.getPrice());
                to.setQuantity(p.getQuantity());
                temporaryObjectRepo.save(to);
            }
            System.out.println(temporaryObjectRepo.findAll());
            for (Product p : products) {
                sums.add(p.getPrice() * p.getQuantity());
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
    }

@PostMapping("product/buy")
    public Object sendSomething( @RequestParam("email") String email,
                                 @RequestParam("currency") String currency,
                                 @RequestParam("amount") int amount,
                                 @RequestParam("phone") String phone,
                                 @RequestParam("reference") String reference,
                                 @RequestParam("description") String description) throws JsonProcessingException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Optional<UserEntity> user = userRepository.findByUsername(auth.getName());
    if(user.isPresent())
    {
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
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(response.getBody());
        JsonNode referenceNode = root.path("transaction").path("reference");
        String ref = referenceNode.asText();

       /* String[] elements = response.getBody().split(":");
        String[] value = elements[25].split(",");
        String code = value[0].replaceAll("^\"|\"$|\\\"", "");*/
        c1.setContent(ref);
        c1.setReference(ref);


        map.put(getPaymentStatus(), updatePayment(c1.getContent(),phone));

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {


                String jsonString=getPaymentStatus();
                ObjectMapper Mapper = new ObjectMapper();
                try {
                    JsonNode rootNode = Mapper.readTree(jsonString);
                    JsonNode statusNode = rootNode.path("transaction").path("status");
                    String statusValue = statusNode.asText();
                    System.out.println("the status is: "+statusValue);
                    if(statusValue.equals("pending") || statusValue.equals("failed") || statusValue.equals("expired"))
                    {
                        System.out.println("ACTION FAILED AFTER 70 SECONDS");

                    }
                    else {

                                for (TemporaryObject t: temporaryObjectRepo.findAll()) {

                                    Purchaseobject po = new Purchaseobject();
                                    po.setName(t.getName());
                                    po.setBought(true);
                                    po.setUser_id(user.get().getId());
                                    po.setDescription(t.getDescription());
                                    po.setQuantity(t.getQuantity());
                                    po.setBought(true);
                                    po.setPrice(t.getPrice());
                                   purchaseObjectRepo.save(po);
                                }
                            System.out.println(temporaryObjectRepo.findAll());
                            temporaryObjectRepo.deleteAll();
                        }

                }
                catch (JsonProcessingException e)
                {
                    throw new RuntimeException(e);
                }

            }

        };
        long delay = 70 * 1000; //20 seconds in milliseconds
        timer.schedule(task, delay);

        // Return response body

        return new ResponseEntity<>("PLEASE VALIDATE ON YOUR PHONE",HttpStatus.CREATED);
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
    public String updatePayment(String reference,String phone) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("Authorization", notchPayApiKey);

            // Prepare request body
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = "{ \"channel\": \"cm.mobile\", \"data\" : { \"phone\": \"+237699189765\" } }";
            // Parse the JSON string into a JSONObject
            JSONObject jsonObject = new JSONObject(jsonBody);

            // Navigate to the 'data' object and then to the 'phone' field
            JSONObject dataObject = jsonObject.getJSONObject("data");

            // Update the 'phone' field with the new phone number
            dataObject.put("phone", phone);

            // Convert the modified JSONObject back to a string
            String updatedJsonBody = jsonObject.toString();

            System.out.println("Updated JSON: " + updatedJsonBody);
            HttpEntity<String> requestEntity = new HttpEntity<>(updatedJsonBody, headers);

            // Send PUT request
            ResponseEntity<String> response = restTemplate.exchange(VERIFY_URL+reference, HttpMethod.PUT, requestEntity, String.class);
            // Return response body
            return response.getBody();
        } catch (Exception e) {
           return  e.getMessage()+e.getLocalizedMessage();
        }
//        String jsonBody = "{ \"channel\" : \"cm.orange\" , data : { phone: +237699189765 } }";

        // Create request entity

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
