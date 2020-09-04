package com.kitchas.kitchenassistant.assistant.models.recipe.instructions;

import org.json.JSONException;
import org.json.JSONObject;

public class Step {
    private String description;
    private String special_notes;
    private int time;

    public Step(String description, String special_notes, int time) {
        this.description = description;
        this.special_notes = special_notes;
        this.time = time;
    }

    public static Step loadFromJSON(JSONObject json) throws JSONException {
        return new Step(json.getString("description"), json.getString("specialNotes"), json.getInt("time"));
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecial_notes() {
        return special_notes;
    }

    public void setSpecial_notes(String special_notes) {
        this.special_notes = special_notes;
    }
}
