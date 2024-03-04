package com.limiter.demo.api;

import com.limiter.demo.models.Product;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class Trials {
    public static void main(String[] args) {
        List<String> p = new ArrayList<>();
        List<String> lm = new ArrayList<>();
        p.add("bea");
        p.add("leo");
        p.add("dave");
        p.add("arthur");
        p.add("simon");
        Obj o = new Obj();
            for(int er=0;er<p.size();er++)
            {
                o.setName(p.get(er));
                lm.add(o.getName());
            }

        System.out.println(lm);
    }
}
@Data
class Obj{
    String name;
}
