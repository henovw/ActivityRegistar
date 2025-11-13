package model;

import org.json.JSONObject;

import persistence.Writable;

// Represents a name of equipment, and the quantity available
public class Equipment implements Writable {
    private String name; // name of equipment
    private int quantity; // number available

    // REQUIRES: name with a non-zero length, 
    // an int of number available > 0
    // EFFECTS: name of equipment set to given name,
    // num available set to given quantity
    public Equipment(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return this.name;
    }

    public int getQuantity() {
        return this.quantity;
    }

    // REQUIRES: an integer >= 0
    // MODIFIES: this
    // EFFECTS: sets quantity of equipment to given
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // REQUIRES: a non-zero length string
    // MODIFIES: this
    // EFFECTS: sets name to given
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("quantity", quantity);
        return json;
    }
}
