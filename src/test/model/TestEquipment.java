package model;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class TestEquipment {
    private Equipment e1;
    private Equipment e2;
    
    @BeforeEach
    void runBefore() {
        e1 = new Equipment("Paddle", 10);
        e2 = new Equipment("Racket", 1);
    }

    @Test
    void testConstructor() {
        assertEquals("Paddle", e1.getName());
        assertEquals(1, e2.getQuantity());
    }

    @Test
    void testSetName() {
        assertEquals("Racket", e2.getName());
        e2.setName("Basketball");
        assertEquals("Basketball", e2.getName());
    }

    @Test
    void testSetQuantity() {
        assertEquals(10, e1.getQuantity());
        e1.setQuantity(9);
        assertEquals(9, e1.getQuantity());
    }
}
