package com.sipios.refactoring.data;

// we will keep the name as is, since it *is* a request body. Instead, it might be better to adjust the attributes names...
public class Body {

    private final Item[] items;
    private final String customerType;

    public Body(Item[] is, String t) {
        this.items = is;
        this.customerType = t;
    }

    public Item[] getItems() {
        return items;
    }

    public String getCustomerType() {
        return customerType;
    }    
}
