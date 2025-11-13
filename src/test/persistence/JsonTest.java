package persistence;



import java.util.ArrayList;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;
import model.Activity;
import model.Equipment;
import model.Timeslot;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExcludeFromJacocoGeneratedReport
public class JsonTest {
    protected void checkActivity(Activity a, String name, 
                    ArrayList<Equipment> equipment, ArrayList<Timeslot> timeslots) {
        
        assertEquals(a.getName(), name);
        checkTimeslots(a.getTimeslots(), timeslots);
        checkEquipment(a.getEquipment(), equipment);
    }

    protected void checkTimeslots(ArrayList<Timeslot> activityTimeslots, ArrayList<Timeslot> timeslots) {
        int i = 0;
        while (i < activityTimeslots.size()) {
            assertEquals(activityTimeslots.get(i).getName(), timeslots.get(i).getName());
            assertEquals(activityTimeslots.get(i).getStart(), timeslots.get(i).getStart());
            assertEquals(activityTimeslots.get(i).getEnd(), timeslots.get(i).getEnd());
            i++;
        }
    }

    protected void checkEquipment(ArrayList<Equipment> activityEquipment, ArrayList<Equipment> equipment) {
        int i = 0;
        while (i < activityEquipment.size()) {
            assertEquals(activityEquipment.get(i). getName(), equipment.get(i).getName());
            assertEquals(activityEquipment.get(i). getQuantity(), equipment.get(i).getQuantity());
            i++;
        }
    }
}
