package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestTimeslot {
    private Timeslot timeslot;
    private Equipment equipment;

    @BeforeEach 
    void runBefore() {
        timeslot = new Timeslot(10, 11);
        equipment = new Equipment("Ball", 1);

    }

    @Test 
    void testConstructor() {
        assertFalse(timeslot.isBooked());
        assertEquals(10, timeslot.getStart());
        assertEquals(11, timeslot.getEnd());
        assertEquals(new ArrayList<Equipment>(), timeslot.getEquipment());

    }

    @Test
    void testBook() {
        assertFalse(timeslot.isBooked());
        timeslot.book("Henry");
        assertEquals("Henry", timeslot.getName());
        assertTrue(timeslot.isBooked());

        timeslot.cancel();
        assertNull(timeslot.getName());

    }

    @Test 
    void testAddEquipment() {
        timeslot.addEquipment(equipment);
        ArrayList<Equipment> l = new ArrayList<>();
        l.add(equipment);
        assertEquals(l, timeslot.getEquipment());
    }
}
