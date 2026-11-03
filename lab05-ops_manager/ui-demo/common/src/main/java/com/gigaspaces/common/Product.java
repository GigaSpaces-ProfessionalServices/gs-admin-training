package com.gigaspaces.common;

import com.gigaspaces.annotation.pojo.SpaceId;

public class Product {
    private Integer id;
    private String name;

    public Product() {
    }

    public Product(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Product createProduct(int id) {
        return new Product(id, RandomUtils.nextString());
    }

    @SpaceId
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
