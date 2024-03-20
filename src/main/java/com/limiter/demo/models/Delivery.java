package com.limiter.demo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "delivery")
@NoArgsConstructor
public class Delivery {
    @Id
    @GeneratedValue
    private long id;
    private String destination;
    private Boolean delivered=false;
    @ManyToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "delivery_products", joinColumns = @JoinColumn(name="delivery_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="product_id", referencedColumnName = "id"))
    private List<Purchaseobject> productList = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "user_id",insertable = false,updatable = false)
    private UserEntity user;
    private long user_id;
    @JsonBackReference(value = "delivery-user")
    public UserEntity getUser() {
        return user;
    }
}
