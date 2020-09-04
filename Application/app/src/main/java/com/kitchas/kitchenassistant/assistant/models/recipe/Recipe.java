package com.kitchas.kitchenassistant.assistant.models.recipe;

import android.content.Context;

import com.android.volley.Request;
import com.kitchas.kitchenassistant.assistant.models.recipe.instructions.Instructions;
import com.kitchas.kitchenassistant.assistant.models.recipe.instructions.Step;
import com.kitchas.kitchenassistant.assistant.user.RecipeUser;
import com.kitchas.kitchenassistant.assistant.user.User;
import com.kitchas.kitchenassistant.utils.GeneralException;
import com.kitchas.kitchenassistant.utils.Settings;
import com.kitchas.kitchenassistant.utils.Tools;
import com.kitchas.kitchenassistant.utils.callbacks.GeneralCallback;
import com.kitchas.kitchenassistant.utils.requests.HTTPManager;
import com.kitchas.kitchenassistant.utils.requests.IOnRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Recipe {
    private String id;
    private List<Ingredient> ingredients;
    private List<Tag> tags;
    private RecipeUser creator;
    private String title, description;
    private Instructions instructions;
    private float rate;
    private int edate, adate;
    private int total_time; //total time in seconds
    private String image;

    public Recipe(String title, RecipeUser creator, int adate, boolean new_recipe) {
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

    public Recipe(String title, RecipeUser creator) {
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

    public RecipeUser getCreator() {
        return creator;
    }

    public void setCreator(RecipeUser creator) {
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

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
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

    public static void fetchMyRecipes(Context context, IOnRequest success, IOnRequest error, int page) {
        fetchMyRecipes(context, success, error, page, 10);
    }

    public static void fetchMyRecipes(Context context, IOnRequest success, IOnRequest error, int page, int limit) {
        Map<String, String> parameters = new HashMap<>();
        HTTPManager.getInstance().GETRequest("user/recipes", parameters, response -> {
            System.out.println("In success!");
            System.out.println(response);
        }, response -> {
            System.out.println("Failed!");
            System.out.println(response);
        }, context);
    }

    public static void fetchCommunityRecipes(Context context, GeneralCallback success_callback, IOnRequest error_callback, int page, int limit) {
        Map<String, String> parameters = new HashMap<>();
        HTTPManager.getInstance().request("community/recipes", parameters, Request.Method.GET, response -> {
            if (response instanceof JSONArray) {
                JSONArray responseArray = (JSONArray) response;
                List<Recipe> recipes = new LinkedList<>();
                try {
                    for (int i = 0; i < responseArray.length(); i++) {
                        JSONObject json = responseArray.getJSONObject(i);
                        RecipeUser creator = new RecipeUser(json.getString("creator"));
                        Recipe recipe = new Recipe(json.getString("title"), creator, json.getInt("adate"), false);
                        recipe.setId(json.getString("_id"));
                        recipe.setRate(json.getInt("rate"));
                        recipe.setTotal_time(100);
                        recipe.setDescription(json.getString("description"));
                        try {
                            recipe.setImage(json.getString("image"));
                        } catch (JSONException ignored) {
                            recipe.setImage(Settings.image);
                        }
                        recipes.add(recipe);
                    }
                    success_callback.call(recipes);
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }
        }, error -> {
            System.out.println("Failed!");
            System.out.println(error);
        }, context);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void rate(float rating, Context context, IOnRequest success_callback, IOnRequest error_callback) {
        Map<String, String> paramaters = new HashMap<>();
        paramaters.put("rate", String.valueOf(rating));
        HTTPManager.getInstance().POSTRequest("user/recipe/" + this.id + "/rate",
                paramaters,
                success_callback,
                error_callback,
                context);
    }
}
