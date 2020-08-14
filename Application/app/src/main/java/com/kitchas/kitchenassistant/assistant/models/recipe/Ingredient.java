package com.kitchas.kitchenassistant.assistant.models.recipe;

public class Ingredient {
    private String title, description;
    private int amount;
    private Units unit;

    public Ingredient(String title, String description, int amount, Units unit) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.unit = unit;
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
}
