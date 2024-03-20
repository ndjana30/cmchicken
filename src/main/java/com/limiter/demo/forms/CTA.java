package com.limiter.demo.forms;

import com.limiter.demo.models.Product;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "cta")
@Data
@NoArgsConstructor
public class CTA {
    @Id
    @GeneratedValue
    private long id;
    @ManyToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable
            (name = "cta_service_traiteur",joinColumns = @JoinColumn(name = "cta_id",referencedColumnName = "id"),
                    inverseJoinColumns = @JoinColumn(name = "menu_id",referencedColumnName = "id"))
    private List<Product> menus=new ArrayList<>();
    private int personNumber;
    private String number;
    private Date date;
    private String location;
}
