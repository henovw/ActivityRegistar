package persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.Activity;
import model.Equipment;
import model.Timeslot;

// Represents a reader that reads all classes from JSON stored in file
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads an activity file and returns it
    // throws IOException if error occurs
    public ArrayList<Activity> read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseActivityList(jsonObject);
    }

    // EFFECTS: reads source file as a string and returns it
    public String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses activity list from JSON and returns
    private ArrayList<Activity> parseActivityList(JSONObject jsonObject) {
        JSONObject swimming = jsonObject.getJSONObject("Swimming");
        JSONObject bowling = jsonObject.getJSONObject("Bowling");
        JSONObject tennis = jsonObject.getJSONObject("Tennis");

        Activity swimmingActivity = addActivity(swimming, "Swimming");
        Activity bowlingActivity = addActivity(bowling, "Bowling");
        Activity tennisActivity = addActivity(tennis, "Tennis");

        ArrayList<Activity> ret = new ArrayList<>();
        ret.add(swimmingActivity);
        ret.add(bowlingActivity);
        ret.add(tennisActivity);

        return ret;
    }

    // REQUIRES: JSON object and specified activity string
    // MODIFIES: this
    // EFFECTS: returns activity from JSON object
    private Activity addActivity(JSONObject jsonObject, String name) {
        JSONArray jsonArray = jsonObject.getJSONArray("timeslots");
        Activity a = new Activity(name);
        addEquipment(a, jsonObject.getJSONArray("equipment"));
        for (Object json : jsonArray) {
            JSONObject timeslot = (JSONObject) json;
            addTimeslot(a, timeslot);
        }

        return a;
    }

    // REQUIRES: an activity to add equipment to, a JSON array of equipment
    // MODIFIES: this
    // EFFECTS: adds equipment from JSON file
    private void addEquipment(Activity a, JSONArray jsonArray) {
        for (Object json : jsonArray) {
            JSONObject workable = (JSONObject) json;
            String name = workable.getString("name");
            int quantity = workable.getInt("quantity");
            a.addEquipment(new Equipment(name, quantity));
        }
    }

    // REQUIRES: an activity to add timeslots to, a JSON array of equipment
    // MODIFIES: this
    // EFFECTS: adds timeslots from JSON
    private void addTimeslot(Activity a, JSONObject jsonObject) {
        int start = jsonObject.getInt("start");
        int end = jsonObject.getInt("end");

        Timeslot t = new Timeslot(start, end);
        try {
            String name = jsonObject.getString("name");
            t.book(name);
        } catch (JSONException e) {
            // pass
        }
        
        getEquipment(jsonObject.getJSONArray("equipment"), t);
        a.addTimeslot(t);
        
    }

    // REQUIRES: JSON array of equipment, a timeslot
    // MODIFIES: this
    // EFFECTS: adds equipment to a timeslot
    private void getEquipment(JSONArray jsonArray, Timeslot t) {
        for (Object json : jsonArray) {
            JSONObject workable = (JSONObject) json;
            String name = workable.getString("name");
            int quantity = workable.getInt("quantity");
            t.addEquipment(new Equipment(name, quantity));
        }

    }

    


}
