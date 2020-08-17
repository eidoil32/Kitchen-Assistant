package com.kitchas.kitchenassistant.assistant.models.recipe;

import com.kitchas.kitchenassistant.assistant.models.recipe.instructions.Instructions;
import com.kitchas.kitchenassistant.assistant.models.recipe.instructions.Step;
import com.kitchas.kitchenassistant.assistant.user.User;
import com.kitchas.kitchenassistant.utils.GeneralException;
import com.kitchas.kitchenassistant.utils.Tools;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class Recipe {
    private String id;
    private List<Ingredient> ingredients;
    private List<Tag> tags;
    private User creator;
    private String title, description;
    private Instructions instructions;
    private int rate, edate, adate;
    private int total_time; //total time in seconds

    public Recipe(String title, User creator, int adate, boolean new_recipe) {
        this.title = title;
        this.creator = creator;
        this.adate = adate;
        if (new_recipe) {
            this.edate = Tools.getCurrentTimeStamp();
        }
        this.ingredients = new LinkedList<>();
        this.tags = new LinkedList<>();
        this.instructions = new Instructions();
    }

    public Recipe(String title, User creator) {
        this(title, creator, Tools.getCurrentTimeStamp(), true);
    }

    public void recipeIsReady() throws GeneralException {
        if (ingredients.isEmpty()) {
            throw new GeneralException("EMPTY_INGREDIENTS");
        } else if (tags.isEmpty()) {
            throw new GeneralException("EMPTY_TAGS");
        } else if (description.isEmpty()) {
            throw new GeneralException("EMPTY_DESCRIPTION");
        } else if (!instructions.isEmpty()) {
            throw new GeneralException("EMPTY_INSTRUCTIONS");
        }
    }

    public void addIngredient(Ingredient ingredient) {
        if (!this.ingredients.contains(ingredient))
            this.ingredients.add(ingredient);
    }

    public void addInstruction(Step step) {
        if (this.instructions.add(step)) {
            this.total_time += step.getTime();
        }
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
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

    public Instructions getInstructions() {
        return instructions;
    }

    public void setInstructions(Instructions instructions) {
        this.instructions = instructions;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getEdate() {
        return edate;
    }

    public void setEdate(int edate) {
        this.edate = edate;
    }

    public int getAdate() {
        return adate;
    }

    public void setAdate(int adate) {
        this.adate = adate;
    }

    public int getTotal_time() {
        return total_time;
    }

    public void setTotal_time(int total_time) {
        this.total_time = total_time;
    }

    public static Recipe getRecipeFromJSON(JSONObject jsonObject) {
        // TODO implement this method, response from server will be in JSON format
        // so we need to convert json to recipe.
        // This method should deal with any corrupted JSONObject, and throw the current exception.
        return null;
    }
}
