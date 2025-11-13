package persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Activity;
import model.Equipment;
import model.Timeslot;

public class JsonWriterTest extends JsonTest {

    @Test
    void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptySchedule() {
        try {
            ArrayList<Activity> activities = new ArrayList<>();
            ArrayList<Timeslot> timeslots = new ArrayList<>();
            activities.add(new Activity("Swimming"));
            activities.add(new Activity("Bowling"));
            activities.add(new Activity("Tennis"));
            JsonWriter writer = new JsonWriter("./data/testWriterEmptySchedule.json");
            writer.open();
            writer.write(activities);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptySchedule.json");
            activities = reader.read();
            assertEquals("Swimming", activities.get(0).getName());
            assertEquals(timeslots, activities.get(0).getBookedReservations());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralSchedule() {
        try {
            ArrayList<Activity> activities = createGeneralSchedule();
            ArrayList<Equipment> equipment = new ArrayList<>();
            equipment.add(new Equipment("pool noodle", 1));
            ArrayList<Timeslot> timeslots = makeTimeslots(10, 20);
            timeslots.get(0).book("Henry");
            timeslots.get(0).addEquipment(new Equipment("pool noodle", 1));

            JsonWriter writer = new JsonWriter("./data/testWriterGeneralSchedule.json");
            writer.open();
            writer.write(activities);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralSchedule.json");
            activities = reader.read();
            checkActivity(activities.get(0), "Swimming", equipment, timeslots);

        } catch (IOException e) {
            fail();
        }
    }
    
    private ArrayList<Activity> createGeneralSchedule() {
        ArrayList<Activity> activities = new ArrayList<>();
        ArrayList<Timeslot> timeslots = makeTimeslots(10, 20);
        ArrayList<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment("pool noodle", 1));
        Activity swimming = new Activity("Swimming");
        swimming.createTimeslots(10, 20);
        swimming.addEquipment(new Equipment("pool noodle", 1));

        swimming.getAvailableReservations().get(0).book("Henry");
        timeslots.get(0).book("Henry");
        timeslots.get(0).addEquipment(new Equipment("pool noodle", 1));
        swimming.getAvailableReservations().get(0).addEquipment(new Equipment("pool noodle", 1));
        activities.add(swimming);
        activities.add(new Activity("Bowling"));
        activities.add(new Activity("Tennis"));

        return activities;
    }

    ArrayList<Timeslot> makeTimeslots(int start, int end) {
        ArrayList<Timeslot> timeslots = new ArrayList<>();
        while (start < end) {
            timeslots.add(new Timeslot(start, start + 1));
            start++;
        }
        return timeslots;
    }
}
