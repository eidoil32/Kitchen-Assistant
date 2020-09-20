package com.kitchas.kitchenassistant.assistant.models.recipe;

import android.content.Context;

import com.kitchas.kitchenassistant.utils.requests.HTTPManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ingredient implements Serializable {
    private String title, description;
    private float amount;
    private Units unit;
    private int priority;
    private String id;

    public Ingredient(String title, String description, float amount, Units unit, int priority) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.unit = unit;
        this.priority = priority;
    }

    public Ingredient(String id, String title, String description, float amount, Units unit, int priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.unit = unit;
        this.priority = priority;
    }

    public static Ingredient loadFromJSON(JSONObject json) throws JSONException {
        Units units = Units.NONE;
        try {
            units = Units.valueOf(json.getString("unit").toUpperCase());
        } catch (IllegalArgumentException ignored) {}
        return new Ingredient(
                json.getString("_id"),
                json.getString("title"),
                json.getString("description"),
                Float.parseFloat(json.getString("amount")),units,
                json.getInt("priority"));
    }

    public static JSONArray convertToJSON(List<Ingredient> ingredients) throws JSONException {
        JSONArray json = new JSONArray();
        for (int i = 0; i < ingredients.size(); i++) {
            json.put(ingredients.get(i).parseJSON(i + 1));
        }

        return json;
    }

    public static void updateRecipe(Context context, String recipe_id, List<Ingredient> ingredients, List<Ingredient> old_data) {
        HTTPManager helper = HTTPManager.getInstance();
        Map<String, String> parameters = new HashMap<>();
        JSONArray parameters_array = new JSONArray();

        int i = 0;
        for (Ingredient ingredient : ingredients) {
            try {
                if (!old_data.contains(ingredient)) {
                    parameters_array.put(ingredient.parseJSON(ingredient.getPriority()));
                } else {
                    old_data.remove(ingredient);
                }
            } catch (JSONException ignored) { }
        }

        for (Ingredient ingredient : old_data) {
            helper.DELETERequest("user/recipe/" + recipe_id + "/ingridients/" + ingredient.getId(),
                    System.out::println, System.out::println, context);
        }

        if (parameters_array.length() > 0) {
            parameters.put("ingredients", parameters_array.toString());
            helper.POSTRequest("user/recipe/" + recipe_id + "/ingridients",
                    parameters, System.out::println, System.out::println, context);
        }
    }

    public String getId() {
        return this.id;
    }

    private JSONObject parseJSON(int priority) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("unit", this.unit.toString().toUpperCase());
        json.put("title", this.title);
        json.put("description", this.description);
        json.put("amount", this.amount);
        json.put("priority", priority);

        return json;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Ingredient)) {
            return false;
        }

        Ingredient ingredient = (Ingredient)obj;
        return this.unit == ingredient.getUnit() &&
                this.amount == ingredient.getAmount() &&
                this.title.equals(ingredient.getTitle());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Units getUnit() {
        return unit;
    }

    public void setUnit(Units unit) {
        this.unit = unit;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(amount).append(" ").append(unit).append(" ").append(title);
        if (description != null) {
            result.append(" (").append(description).append(")");
        }

        return result.toString();
    }
}
