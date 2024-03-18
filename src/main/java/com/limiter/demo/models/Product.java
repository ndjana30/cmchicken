package com.limiter.demo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.limiter.demo.rest.controllers.ProductSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="product")
@Data
@NoArgsConstructor
public class Product implements Serializable {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    @Lob
    @Column(nullable = true)
    private byte[] image;
    private double price;
    private String description;
    private Date addedDate;
    private int quantity;
    @ManyToOne
    @JoinColumn(name="category_id",insertable = false,updatable = false)
    private Category category;
    private long category_id;
    @JsonBackReference(value = "products-category")
    public Category getCategory() {
        return category;
    }
}