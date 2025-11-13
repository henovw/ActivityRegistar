package model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import persistence.Writable;

// Represents a timeslot that starts at the given time
// and ends at the given time and can be booked with equipment
// and a name
// (24 hour time)
public class Timeslot implements Writable {
    private int start; // start hour
    private int end; // end hour
    private String name; // name of bookee
    private ArrayList<Equipment> equipment; // equipment added

    // REQUIRES: A start and an end time to make a booking
    // EFFECTS: created an unbooked timeslot with the given duration
    public Timeslot(int start, int end) {
        this.start = start;
        this.end = end;

        this.name = null;
        this.equipment = new ArrayList<>();

    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public Boolean isBooked() {
        return name != null;
    }

    public ArrayList<Equipment> getEquipment() {
        return this.equipment;
    }

    public String getName() {
        return this.name;
    }

    // REQUIRES: a name 
    // MODIFIES: this
    // EFFECTS: sets name of timeslot 
    public void book(String name) {
        this.name = name;
    }

    // REQUIRES: a piece of equipment
    // MODIFIES: this
    // EFFECTS: adds piece of equipment to equipment used in timeslot
    public void addEquipment(Equipment e) {
        this.equipment.add(e);
    }

    // MODIFIES: this
    // EFFECTS: reset equipment and name
    public void cancel() {
        this.name = null;
        this.equipment = new ArrayList<>();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("start", start);
        json.put("end", end);
        json.put("equipment", equipmentToJson());

        return json;
    }

    // EFFECTS: returns equipment as Json
    private JSONArray equipmentToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Equipment e : equipment) {
            jsonArray.put(e.toJson());
        }
        
        return jsonArray;
    }
}
