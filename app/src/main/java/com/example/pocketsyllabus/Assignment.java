package com.example.pocketsyllabus;

public class Assignment {

    // instance vars
    private String name;
    private String dueDate;

    // constructor
    public Assignment( String name, String dueDate) {
        this.name = name;
        this.dueDate = dueDate;
    }

    // getters
    public String getName() {
        return this.name;
    }

    public String getDueDate() {
        return this.dueDate;
    }

    // setters
    public void setName( String name ) {
        this.name = name;
    }

    public void setDueDate( String dueDate ) {
        this.dueDate = dueDate;
    }
}
