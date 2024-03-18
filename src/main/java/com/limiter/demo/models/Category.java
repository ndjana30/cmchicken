package com.limiter.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "category")
@Data
@NoArgsConstructor

public class Category implements Serializable {
    @Id
    @GeneratedValue

    private long id;

    private String name;


    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "category")
    List<Product> products;
    @JsonIgnore
    @JsonManagedReference(value = "products-category")
    public List<Product> getProducts() {
        return products;
    }
}
