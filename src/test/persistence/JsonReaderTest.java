package persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;
import model.Activity;
import model.Equipment;
import model.Timeslot;

@ExcludeFromJacocoGeneratedReport
public class JsonReaderTest extends JsonTest {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/doesNotExist.json");
        try {
            ArrayList<Activity> activities = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptySchedule() {
        JsonReader reader = new JsonReader("./data/testReaderEmptySchedule.json");
        try {
            ArrayList<Activity> activities = reader.read();
            assertEquals(3, activities.size());
            assertEquals("Swimming", activities.get(0).getName());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void testReaderGeneralSchedule() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralSchedule.json");
        try {
            ArrayList<Activity> activities = reader.read();
            assertEquals("Bowling", activities.get(1).getName());
            Activity bowling = new Activity("Bowling");
            bowling.addEquipment(new Equipment("bowling ball", 2));
            bowling.createTimeslots(10, 11);
            bowling.getTimeslots().get(0).book("Jeremy");
            ArrayList<Equipment> equipments = new ArrayList<>();
            equipments.add(new Equipment("bowling ball", 1));
            bowling.makeReservation("Jeremy", equipments, new Timeslot(10, 11));
            checkActivity(activities.get(1), "Bowling", bowling.getEquipment(), bowling.getBookedReservations());
        } catch (IOException e) {
            fail();
        }
    }
}
