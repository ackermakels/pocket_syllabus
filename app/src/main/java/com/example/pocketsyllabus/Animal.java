package com.example.pocketsyllabus;

public class Animal {

    private int id;
    private String name;
    private int quantity;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Animal(String name, int quantity) {
        super();
        this.name = name;
        this.quantity = quantity;
    }

    public Animal(String name) {
        super();
        this.name = name;
    }



}
