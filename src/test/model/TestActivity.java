package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestActivity {
    private Activity a1;

    private Equipment e1;
    private Equipment e2;

    private ArrayList<Equipment> elist;

    
    @BeforeEach
    void runBefore() {
        a1 = new Activity("Pickleball");

        e1 = new Equipment("Racket", 2);
        e2 = new Equipment("Pool Noodle", 1);

        elist = new ArrayList<>();
    }

    @Test
    void testConstructor() {
        assertEquals(new ArrayList<Equipment>(), a1.getEquipment());
        assertEquals(new ArrayList<Timeslot>(), a1.getTimeslots());
        assertEquals("Pickleball", a1.getName());
    }

    @Test
    void testAddEquipment() {
        a1.addEquipment(e1);
        ArrayList<Equipment> test = new ArrayList<>();
        test.add(e1);
        assertEquals(test, a1.getEquipment());
        test.add(e2);
        a1.addEquipment(e2);
        assertEquals(test, a1.getEquipment());
    }

    @Test
    void testResetEquipment() {
        a1.addEquipment(e1);
        ArrayList<Equipment> test = new ArrayList<>();
        test.add(e1);
        assertEquals(test, a1.getEquipment());
        a1.resetEquipment();
        assertEquals(new ArrayList<Equipment>(), a1.getEquipment());
    }

    @Test 
    void testCreateTimeslots() {
        a1.createTimeslots(0, 1);
        assertFalse(a1.getTimeslots().get(0).isBooked());
        assertEquals(0, a1.getTimeslots().get(0).getStart());
        assertEquals(1, a1.getTimeslots().get(0).getEnd());

    }

    @Test 
    void testMakeCancelReservation() {
        elist.add(e1);
        a1.createTimeslots(10, 11);
        a1.cancelReservation(10);
        assertFalse(a1.getTimeslots().get(0).isBooked());
        a1.makeReservation("1", elist, a1.getTimeslots().get(0));
        assertTrue(a1.getTimeslots().get(0).isBooked());
        assertEquals(e1, a1.getTimeslots().get(0).getEquipment().get(0));
        assertEquals("1", a1.getTimeslots().get(0).getName());
        a1.cancelReservation(9);
        assertEquals("1", a1.getTimeslots().get(0).getName());
        a1.cancelReservation(10);
        assertFalse(a1.getTimeslots().get(0).isBooked());
    }

    @Test
    void testGetAvailableReservations() {
        elist.add(e1);
        a1.createTimeslots(0, 1);
        assertEquals(0, a1.getAvailableReservations().get(0).getStart());
        assertEquals(1, a1.getAvailableReservations().get(0).getEnd());
        a1.makeReservation("test", elist, a1.getTimeslots().get(0));
        assertEquals(new ArrayList<>(), a1.getAvailableReservations());
    }

    @Test
    void testGetBookedReservations() {
        elist.add(e1);
        a1.createTimeslots(0, 1);
        assertEquals(new ArrayList<>(), a1.getBookedReservations());
        a1.makeReservation("test", elist, a1.getTimeslots().get(0));
        assertEquals(0, a1.getBookedReservations().get(0).getStart());
        assertEquals(1, a1.getBookedReservations().get(0).getEnd());
        assertEquals("test", a1.getBookedReservations().get(0).getName());
    }
}
