package com.sipios.refactoring.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sipios.refactoring.data.Body;
import com.sipios.refactoring.data.Item;
import com.sipios.refactoring.exception.PriceTooHighException;

// This controller seem to provide an interface, with a post endpoint, to calculate the content of a customer's cart.
// The cart can contains different type of items.
// The method associated to the endpoint will go through the user's cart, calculate the price, and return it.
// It also controls if the cart reaches the limit price for the customer (a standard customer can only buy up to 200, and so on...)


@RestController
@RequestMapping("/shopping")
public class ShoppingController {

    private static final String TIMEZONE = "Europe/Paris";

    @PostMapping
    public String getPrice(@RequestBody Body body) {

        Item[] cart = body.getItems();

        // an empty cart has no price.
        if (cart == null) {
            return "0";
        }

        double customerCoefficient = getCustomerCoefficient(body.getCustomerType());

        boolean isDiscountPeriod = isDiscountPeriod();

        // Compute total amount depending on the types and quantity of product and
        // if we are in winter or summer discounts periods
        double price = calculateCartPrice(cart, customerCoefficient, isDiscountPeriod);

        verifyPriceLimit(price, body.getCustomerType());

        return String.valueOf(price);
    }

    // are we in summer / winter discount periods ?
    //TODO this method is more of business logic and could be isolated in a DiscountService.
    private boolean isDiscountPeriod() {

        final int JANUARY = 0;
        final int MAY = 5;
        
        Date date = new Date();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIMEZONE));
        cal.setTime(date);

        return !isDiscountPeriodForMonth(cal, MAY) && !isDiscountPeriodForMonth(cal, JANUARY);
    }

    //TODO this method is more of business logic and could be isolated in a DiscountService.
    private boolean isDiscountPeriodForMonth(Calendar cal, int monthIndex) {
        return cal.get(Calendar.DAY_OF_MONTH) < 15 &&
        cal.get(Calendar.DAY_OF_MONTH) > 5 &&
        cal.get(Calendar.MONTH) == monthIndex;
    }

    //TODO this method is still too complicated and should be isolated in a PriceService class.
    private double calculateCartPrice(Item[] cart, double customerCoefficient, boolean isDiscountPeriod) {

        double price = 0;

        //TODO build a hashmap associating base prices and item types.
        if (isDiscountPeriod) {

            for (int i = 0; i < cart.length; i++) {
                Item it = cart[i];

                //TODO duplication code. Also, what happens if the item is of a different type than those proposed below ? There's no control !
                //TODO we might want to add exceptions.
                if (it.getType().equals("TSHIRT")) {
                    price += 30 * it.getNb() * customerCoefficient;
                } else if (it.getType().equals("DRESS")) {
                    price += 50 * it.getNb() * customerCoefficient;
                } else if (it.getType().equals("JACKET")) {
                    price += 100 * it.getNb() * customerCoefficient;
                }
            }
        } else {

            for (int i = 0; i < cart.length; i++) {
                Item it = cart[i];

                if (it.getType().equals("TSHIRT")) {
                    price += 30 * it.getNb() * customerCoefficient;
                } else if (it.getType().equals("DRESS")) {
                    price += 50 * it.getNb() * 0.8 * customerCoefficient;
                } else if (it.getType().equals("JACKET")) {
                    price += 100 * it.getNb() * 0.9 * customerCoefficient;
                }
            }
        }

        return price;
    }

    private double getCustomerCoefficient(String customerType) {
        if (customerType.equals("STANDARD_CUSTOMER")) {
            return 1;
        } else if (customerType.equals("PREMIUM_CUSTOMER")) {
            return 0.9;
        } else if (customerType.equals("PLATINUM_CUSTOMER")) {
            return 0.5;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    //TODO specific exceptions have been defined, but the test might be simplified
    //TODO (for example, in a PriceService class that throws an exception as soon as the limit is hit rather than after the full price is calculated).
    private void verifyPriceLimit(double price, String customerType) {

        try {
            if (customerType.equals("STANDARD_CUSTOMER") && price > 200) {
                    throw new PriceTooHighException(price, "standard");
                } 
            else if (customerType.equals("PREMIUM_CUSTOMER") && price > 800) {
                    throw new PriceTooHighException(price, "premium");
                }
            else if (customerType.equals("PLATINUM_CUSTOMER") && price > 2000) {
                    throw new PriceTooHighException(price, "platinum");
                }
            else if (price > 200) {
                    throw new PriceTooHighException(price, "standard");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
