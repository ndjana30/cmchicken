package com.limiter.demo.rest.controllers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.limiter.demo.models.Product;

import java.io.IOException;

public class ProductSerializer extends StdSerializer<Product> {
    public ProductSerializer() {
        this(Product.class);
    }

    public ProductSerializer(Class<Product> t) {
        super(t);
    }

    @Override
    public void serialize(Product value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        // Serialize other fields
        gen.writeEndObject();
    }
}