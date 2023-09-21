package com.sipios.refactoring.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sipios.refactoring.data.Body;
import com.sipios.refactoring.data.Item;

// This controller seem to provide an interface, with a post endpoint, to calculate the content of a customer's cart.
// The cart can contains different type of items.
// The method associated to the endpoint will go through the user's cart, calculate the price, and return it.
// It also controls if the cart reaches the limit price for the customer (a standard customer can only buy up to 200, and so on...)


@RestController
@RequestMapping("/shopping")
public class ShoppingController {

    @PostMapping
    //TODO This method does too many things. It needs to be broken down into separate methods, each fulfilling a specific purpose.
    public String getPrice(@RequestBody Body body) {
        double price = 0;

        Date date = new Date();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        cal.setTime(date);

        double customerCoefficient = getCustomerCoefficient(body.getCustomerType());

        // Compute total amount depending on the types and quantity of product and
        // if we are in winter or summer discounts periods
        //TODO we should review the calculation of date. It looks a lot like duplication here !
        if (
            !(
                cal.get(Calendar.DAY_OF_MONTH) < 15 &&
                cal.get(Calendar.DAY_OF_MONTH) > 5 &&
                cal.get(Calendar.MONTH) == 5
            ) &&
            !(
                cal.get(Calendar.DAY_OF_MONTH) < 15 &&
                cal.get(Calendar.DAY_OF_MONTH) > 5 &&
                cal.get(Calendar.MONTH) == 0
            )
        ) {
            if (body.getItems() == null) {
                return "0";
            }

            for (int i = 0; i < body.getItems().length; i++) {
                Item it = body.getItems()[i];

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
            if (body.getItems() == null) {
                return "0";
            }

            for (int i = 0; i < body.getItems().length; i++) {
                Item it = body.getItems()[i];

                if (it.getType().equals("TSHIRT")) {
                    price += 30 * it.getNb() * customerCoefficient;
                } else if (it.getType().equals("DRESS")) {
                    price += 50 * it.getNb() * 0.8 * customerCoefficient;
                } else if (it.getType().equals("JACKET")) {
                    price += 100 * it.getNb() * 0.9 * customerCoefficient;
                }
            }
        }

        verifyPriceLimit(price, body.getCustomerType());

        return String.valueOf(price);
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

    private void verifyPriceLimit(double price, String customerType) {

        try {
            if (customerType.equals("STANDARD_CUSTOMER")) {
                if (price > 200) {
                    throw new Exception("Price (" + price + ") is too high for standard customer");
                }
            } else if (customerType.equals("PREMIUM_CUSTOMER")) {
                if (price > 800) {
                    throw new Exception("Price (" + price + ") is too high for premium customer");
                }
            } else if (customerType.equals("PLATINUM_CUSTOMER")) {
                if (price > 2000) {
                    throw new Exception("Price (" + price + ") is too high for platinum customer");
                }
            } else {
                if (price > 200) {
                    throw new Exception("Price (" + price + ") is too high for standard customer");
                }
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
