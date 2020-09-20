package com.kitchas.kitchenassistant.assistant.models.recipe.instructions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Instructions implements Serializable {
    private List<Step> steps;

    public Instructions() {
        this.steps = new LinkedList<>();
    }

    public static JSONArray convertToJSON(Instructions instructions) throws JSONException {
        JSONArray json = new JSONArray();
        for (int i = 0; i < instructions.steps.size(); i++) {
            json.put(instructions.steps.get(i).parseJSON(i + 1));
        }

        return json;
    }

    public List<Step> getSteps() {
        return this.steps;
    }

    public boolean isEmpty() {
        return steps.isEmpty();
    }

    public boolean add(Step step) {
        if (!steps.contains(step)) {
            return steps.add(step);
        }
        return false;
    }

    public String printSteps() {
        StringBuilder steps = new StringBuilder();
        int i = 1;
        for (Step step : this.steps) {
            if (step.getSpecial_notes() != null && !step.getSpecial_notes().isEmpty()) {
                steps.append(String.format("%d. %s (%s)\n", i++, step.getDescription(), step.getSpecial_notes()));
            } else {
                steps.append(String.format("%d. %s\n", i++, step.getDescription()));
            }
        }

        return steps.toString();
    }

    public void setSteps(List<Step> instructions) {
        this.steps = instructions;
    }
}
