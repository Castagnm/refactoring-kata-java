package com.sipios.refactoring.exception;

public class PriceTooHighException extends Exception {
    
    public PriceTooHighException(double price, String customerType) {
        super("Price (" + price + ") is too high for " + customerType + " customer");

    }
}
