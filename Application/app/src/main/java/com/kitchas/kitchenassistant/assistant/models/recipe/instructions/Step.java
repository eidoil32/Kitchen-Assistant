package com.kitchas.kitchenassistant.assistant.models.recipe.instructions;

import androidx.annotation.Nullable;

import com.kitchas.kitchenassistant.utils.JSONHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Step implements Serializable {
    private String description;
    private String special_notes;
    private int time;

    public Step(String description, String special_notes, int time) {
        this.description = description;
        this.special_notes = special_notes;
        this.time = time;
    }

    public static Step loadFromJSON(JSONObject json) throws JSONException {
        return new Step(JSONHelper.tryString(json,"description"),
                JSONHelper.tryString(json,"specialNotes"),
                JSONHelper.tryInt(json,"time"));
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Step) {
            Step object = (Step)obj;
            return this.description.equals(object.getDescription());
        }

        return false;
    }

    public JSONObject parseJSON(int priority) throws JSONException {
        JSONObject step = new JSONObject();
        step.put("description", this.description);
        step.put("time", this.time);
        step.put("priority", priority);
        return step;
    }
}
