package com.limiter.demo.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "category")
@Data
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "category")
    List<Product> products;
}
