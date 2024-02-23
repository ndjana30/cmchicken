package com.limiter.demo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="product")
@Data
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    @Lob
    private byte[] image;
    private double price;
    private String description;
    private Date addedDate;
    private int quantity;

}