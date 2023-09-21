package com.sipios.refactoring.data;

public class Item {

    private final String type;
    private final int nb;

    public Item(String type, int quantity) {
        this.type = type;
        this.nb = quantity;
    }

    public String getType() {
        return type;
    }

    public int getNb() {
        return nb;
    }
}
