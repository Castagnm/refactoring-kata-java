package com.sipios.refactoring.controller;

import com.sipios.refactoring.UnitTest;
import com.sipios.refactoring.data.Body;
import com.sipios.refactoring.data.Item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

class ShoppingControllerTests extends UnitTest {

    @InjectMocks
    private ShoppingController controller;

    // this initial test is a good template to check the different scenarios.
    // we can use them to verify some simple scenarios and confirm we haven't broken anything.
    // With time we could write more complex tests but that might go over the time limit...
    @Test
    void should_not_throw() {
        Assertions.assertDoesNotThrow(
            () -> controller.getPrice(new Body(new Item[] {}, "STANDARD_CUSTOMER"))
        );
    }
}
