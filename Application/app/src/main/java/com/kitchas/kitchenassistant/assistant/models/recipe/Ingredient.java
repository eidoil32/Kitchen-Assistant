package com.kitchas.kitchenassistant.assistant.models.recipe;

import org.json.JSONException;
import org.json.JSONObject;

public class Ingredient {
    private String title, description;
    private int amount;
    private Units unit;
    private int priority;

    public Ingredient(String title, String description, int amount, Units unit, int priority) {
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
                json.getString("title"),
                json.getString("description"),
                json.getInt("amount"),units,
                json.getInt("priority"));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Ingredient)) {
            return false;
        }

        Ingredient ingredient = (Ingredient)obj;
        return this.unit == ingredient.getUnit() &&
                this.amount != ingredient.getAmount() &&
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

    public int getAmount() {
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
}
