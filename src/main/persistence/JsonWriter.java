package persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.JSONObject;

import model.Activity;

public class JsonWriter {
    private static final int TAB = 4;
    private PrintWriter writer;
    private String destination;


    // EFFECTS: constructs a writer to write to a destination file
    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens writer and throws FileNotFoundException if
    // file can't be opened
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }

    public void write(ArrayList<Activity> as) {
        JSONObject json = new JSONObject();
        for (Activity a : as) {
            json.put(a.getName(), a.toJson());
        }
        saveToFile(json.toString(TAB));
    }

    // MODIFIES: this
    // EFFECTS: closes writer
    public void close() {
        writer.close();
    }

    // MODIFIES: this
    // EFFECTS: writes string to file
    private void saveToFile(String json) {
        writer.print(json);
    }
}
