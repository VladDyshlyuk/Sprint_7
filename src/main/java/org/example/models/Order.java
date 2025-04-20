package org.example.models;

import java.util.List;

public class Order {

    private List<String> color;

    public Order(List<String> color) {
        this.color = color;
    }

    public List<String> getColor() {
        return color;
    }
}