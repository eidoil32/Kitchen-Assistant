package com.kitchas.kitchenassistant.assistant.models.recipe.instructions;

import android.content.Context;

import com.kitchas.kitchenassistant.assistant.models.recipe.Ingredient;
import com.kitchas.kitchenassistant.utils.requests.HTTPManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    public static void updateRecipe(Context context, String recipe_id, List<Step> instructions, List<Step> old_data) {
        HTTPManager helper = HTTPManager.getInstance();
        Map<String, String> parameters = new HashMap<>();
        JSONArray parameters_array = new JSONArray();
        int i = 0;
        for (Step step : instructions) {
            try {
                if (!old_data.contains(step)) {
                    parameters_array.put(step.parseJSON(++i));
                } else {
                    old_data.remove(step);
                }
            } catch (JSONException ignored) { }
        }

        for (Step step : old_data) {
            helper.DELETERequest("user/recipe/" + recipe_id + "/instructions/" + step.getId(),
                    System.out::println, System.out::println, context);
        }

        if (parameters_array.length() > 0) {
            parameters.put("instructions", parameters_array.toString());
            helper.POSTRequest("user/recipe/" + recipe_id + "/instructions",
                    parameters, System.out::println, System.out::println, context);
        }
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
