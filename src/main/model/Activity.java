package model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import persistence.Writable;

// Represents an activity with a name, list of equipment, 
// available times and booked times
public class Activity implements Writable {
    private String name; // name of activity
    private ArrayList<Equipment> equipment; // list of available equipment
    private ArrayList<Timeslot> timeslots; // list of times

    // REQUIRES: an activity name with a non-zero length 
    // EFFECTS: name of activity is set to the name
    public Activity(String name) {
        this.name = name;
        this.equipment = new ArrayList<>();
        this.timeslots = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Equipment> getEquipment() {
        return this.equipment;
    }

    public ArrayList<Timeslot> getTimeslots() {
        return this.timeslots;
    }

    // REQUIRES: a piece of equipment
    // MODIFIES: this
    // EFFECTS: adds the equipment to the list of equipment
    public void addEquipment(Equipment e) {
        this.equipment.add(e);
    }

    // MODIFIES: this
    // EFFECTS: resets available equipment
    public void resetEquipment() {
        equipment.removeAll(equipment);
    }

    // REQUIRES: a start and ending time in hours (24 hour clock)
    // MODIFIES: this
    // EFFECTS: creates unbooked timeslots for each hour from the 
    // start hour to end hour
    public void createTimeslots(int start, int end) {
        while (start < end) {
            timeslots.add(new Timeslot(start, start + 1));
            start++;
        }
    }

    // REQUIRES: a start hour of reservation, 24 hour clock
    // MODIFIES: this
    // EFFECTS: removes reservation with timeslot
    public void cancelReservation(int start) {
        for (Timeslot t : timeslots) {
            if (t.getStart() == start && t.isBooked()) {
                t.cancel();
            }
        }
    }

    // REQUIRES: a name, equipment to make reservation on timeslot given
    // MODIFIES: this
    // EFFECTS: makes a reservation at a time with a name
    public void makeReservation(String name, ArrayList<Equipment> equipment, Timeslot t) {
        t.book(name);
        for (Equipment e : equipment) {
            t.addEquipment(e);
        }
    }

    // EFFECTS: returns all timeslots that are unbooked
    public ArrayList<Timeslot> getAvailableReservations() {
        ArrayList<Timeslot> ret = new ArrayList<>();

        for (Timeslot t : timeslots) {
            if (!t.isBooked()) {
                ret.add(t);
            }
        }

        return ret;
    }

    // EFFECTS: adds a given timeslot to all timeslots
    // REQUIRES: timeslot does not already exist
    public void addTimeslot(Timeslot e) {
        timeslots.add(e);
    }

    // EFFECTS: returns all timeslots that are booked
    public ArrayList<Timeslot> getBookedReservations() {
        ArrayList<Timeslot> ret = new ArrayList<>();

        for (Timeslot t : timeslots) {
            if (t.isBooked()) {
                ret.add(t);
            }
        }

        return ret;
    }

    @Override 
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("equipment", equipmentToJson());
        json.put("timeslots", timeslotsToJson());

        return json;
    }

    // EFFECTS: returns equipment as a JSON array
    private JSONArray equipmentToJson() {
        JSONArray json = new JSONArray();
        for (Equipment e : equipment) {
            json.put(e.toJson());
        }

        return json;
    }

    // EFFECTS: returns timeslots as a JSON array
    private JSONArray timeslotsToJson() {
        JSONArray json = new JSONArray();
        for (Timeslot t : timeslots) {
            json.put(t.toJson());
        }

        return json;
    }
    
}
