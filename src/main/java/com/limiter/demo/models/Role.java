package com.limiter.demo.models;


import javax.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name="roles")
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
}