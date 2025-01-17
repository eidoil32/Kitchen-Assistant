package com.kitchas.kitchenassistant.assistant.models.recipe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.adapters.MinRecipeAdapter;
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Recipe implements Serializable {
    private String id;
    private List<Ingredient> ingredients;
    private List<Tag> tags;
    private RecipeUser creator;
    private String title, description;
    private Instructions instructions;
    private float rate;
    private long edate, adate;
    private int total_time; //total time in seconds
    private String image;
    private boolean edit_mode = false;

    private static final long serialVersionUID = 160920L;

    public Recipe(String title, RecipeUser creator, long adate, boolean new_recipe) {
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

    public static Recipe fetchBasic(JSONObject recipe_base) {
        try {
            String title = recipe_base.getString("title");
            RecipeUser creator = new RecipeUser(recipe_base.getString("creator"));
            String description = recipe_base.getString("description");
            long adate = Long.valueOf(recipe_base.getString("adate"));

            Recipe recipe = new Recipe(title, creator, adate, true);
            recipe.setId(recipe_base.getString("_id"));
            return recipe;
        } catch (JSONException e) {
            return null;
        }
    }

    public void recipeIsReady() throws GeneralException {
        if (ingredients.isEmpty()) {
            throw new GeneralException("EMPTY_INGREDIENTS");
        } else if (tags.isEmpty()) {
            throw new GeneralException("EMPTY_TAGS");
        } else if (description.isEmpty()) {
            throw new GeneralException("EMPTY_DESCRIPTION");
        } else if (instructions.isEmpty()) {
            throw new GeneralException("EMPTY_INSTRUCTIONS");
        }
    }

    public boolean isInEditMode() {
        return this.edit_mode;
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

    public long getEdate() {
        return edate;
    }

    public void setEdate(long edate) {
        this.edate = edate;
    }

    public long getAdate() {
        return adate;
    }

    public void setAdate(long adate) {
        this.adate = adate;
    }

    public int getTotal_time() {
        return total_time;
    }

    public void setTotal_time(int total_time) {
        this.total_time = total_time;
    }

    public String getTotalTimeCook() {
        float total_time_calc = this.total_time;
        String time_units = "min";
        if (total_time_calc > 60) {
            time_units = "hours";
            total_time_calc /= 60;
        }

        return String.format("%.0f %s", total_time_calc, time_units);
    }

    public static Recipe getRecipeFromJSON(JSONObject jsonObject) {
        // TODO implement this method, response from server will be in JSON format
        // so we need to convert json to recipe.
        // This method should deal with any corrupted JSONObject, and throw the current exception.
        return null;
    }

    public static void fetchListRecipes(Context context, GeneralCallback success_callback, IOnRequest error_callback, int page, int limit, List<String> recipes_ids) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("limit", String.valueOf(limit));
        parameters.put("page", String.valueOf(page));
        parameters.put("recipes", new JSONArray(recipes_ids).toString());
        User user = User.getInstance(context);
        HTTPManager.getInstance().POSTRequest("user/lastrecipes/load", parameters, response -> {
            try {
                JSONArray recipes = response.getJSONArray("recipes");
                fetchRecipesJSONArray(recipes, success_callback);
                user.removeSavedRecipes(context, response.getJSONArray("removed"));
            } catch (JSONException e) {
                error_callback.onResponse(Settings.UNKNOWN_ERROR);
            }
        }, error -> {
            System.out.println("Failed!");
            System.out.println(error);
        }, context);
    }

    public static void fetchMyRecipes(Context context, IOnRequest success, IOnRequest error, int page) {
        fetchMyRecipes(context, success, error, page, 10);
    }

    public static void fetchMyRecipes(Context context, IOnRequest success, IOnRequest error, int page, int limit) {
        HTTPManager.getInstance().GETRequest("user/recipes", response -> {
            System.out.println("In success!");
            System.out.println(response);
        }, response -> {
            System.out.println("Failed!");
            System.out.println(response);
        }, context);
    }

    public static void loadRecipeByID(String recipe_id, Context context, GeneralCallback success_callback, IOnRequest error_callback) {
        HTTPManager.getInstance().GETRequest("user/recipes/" + recipe_id,
                response -> {
                    try {
                        Recipe recipe = fetchRecipeFromJSON(response.getJSONObject("recipe"));
                        recipe.fetchInstructions(response.getJSONArray("instructions"));
                        recipe.fetchTags(response.getJSONArray("tags"));
                        recipe.fetchIngredients(response.getJSONArray("ingridients"));
                        success_callback.call(recipe);
                    } catch (JSONException e) {
                        Log.d("parsing full recipe", e.getMessage());
                        error_callback.onResponse(Settings.UNKNOWN_ERROR);
                    }
                }, error_callback, context);
    }

    private void fetchIngredients(JSONArray ingredients) {
        this.ingredients = new LinkedList<>();
        for (int i = 0; i < ingredients.length(); i++) {
            try {
                JSONObject json = ingredients.getJSONObject(i);
                Ingredient ingredient = Ingredient.loadFromJSON(json);
                this.ingredients.add(ingredient);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchTags(JSONArray tags) {
        this.tags = new LinkedList<>();
        for (int i = 0; i < tags.length(); i++) {
            try {
                JSONObject json = tags.getJSONObject(i);
                Tag tag = Tag.loadFromJSON(json);
                this.tags.add(tag);
            } catch (JSONException ignored) {
            }
        }
    }

    private void fetchInstructions(JSONArray instructions) {
        this.instructions = new Instructions();
        for (int i = 0; i < instructions.length(); i++) {
            try {
                JSONObject json = instructions.getJSONObject(i);
                Step step = Step.loadFromJSON(json);
                this.instructions.add(step);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static Recipe fetchRecipeFromJSON(JSONObject json) throws JSONException {
        RecipeUser creator = null;
        try {
            creator = new RecipeUser(json.getString("creator"));
        } catch (JSONException ignored) {
            creator = new RecipeUser("System");
        }
        Recipe recipe = new Recipe(json.getString("title"), creator, json.getInt("adate"), false);
        recipe.setId(json.getString("_id"));
        recipe.setRate(Float.parseFloat(json.getString("rate")));
        try {
            recipe.setTotal_time(json.getInt("totalTime"));
        } catch (JSONException ignored) {
            recipe.setTotal_time(0);
        }
        recipe.setDescription(json.getString("description"));
        try {
            String image = json.getString("image");
            recipe.setImage(image);
        } catch (JSONException ignored) {
            recipe.setImage(Settings.image);
        }

        return recipe;
    }

    public static void fetchRecipesJSONArray(JSONArray _recipesJSON, GeneralCallback callback) {
        List<Recipe> recipes = new LinkedList<>();
        for (int i = 0; i < _recipesJSON.length(); i++) {
            try {
                JSONObject json = _recipesJSON.getJSONObject(i);
                Recipe recipe = fetchRecipeFromJSON(json);
                recipes.add(recipe);
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                // Not destroy all recipe because one,
                // lets skip this one and keep trying to fetch others
            }
        }
        callback.call(recipes);
    }

    public static void fetchCommunityRecipes(Context context, GeneralCallback success_callback, IOnRequest error_callback, int page, int limit) {
        Map<String, String> parameters = new HashMap<>();
        @SuppressLint("DefaultLocale") String endpoint = String.format("community/recipes?limit=%d&page=%d", limit, page);
        HTTPManager.getInstance().request(endpoint, parameters, Request.Method.GET, response -> {
            if (response instanceof JSONArray) {
                fetchRecipesJSONArray((JSONArray) response, success_callback);
            }
        }, error -> {
            System.out.println("Failed!");
            System.out.println(error);
        }, context);
    }

    public static void fetchFavoriteRecipes(Context context, GeneralCallback success_callback, IOnRequest error_callback, int page, int limit) {
        List<String> recipes = User.getInstance(context).getFavoriteRecipes(context);
        fetchListRecipes(context, success_callback, error_callback, page, limit, recipes);
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
                response -> {
                    try {
                        this.rate = Float.parseFloat(response.getString("rate"));
                        success_callback.onResponse(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error_callback,
                context);
    }

    public List<String> printSteps() {
        List<String> strings = new LinkedList<>();
        int i = 1;
        for (Step step : this.instructions.getSteps()) {
            if (step.getSpecial_notes() != null && !step.getSpecial_notes().isEmpty()) {
                strings.add(String.format("%d. %s (%s)\n", i++, step.getDescription(), step.getSpecial_notes()));
            } else {
                strings.add(String.format("%d. %s\n", i++, step.getDescription()));
            }
        }
        return strings;
    }

    public List<String> printIngredients() {
        List<String> ingredients = new LinkedList<>();
        for (Ingredient ingredient : this.ingredients) {
            ingredients.add(ingredient.toString());
        }
        return ingredients;
    }

    public void save(Context context, IOnRequest success_callback, IOnRequest error_callback) {
        try {
            Map<String, String> parameters = new HashMap<>();
            JSONArray ingredients_json = Ingredient.convertToJSON(this.ingredients);
            JSONArray instructions_json = Instructions.convertToJSON(this.instructions);
            JSONArray tags_json = Tag.convertToJSON(this.tags);
            parameters.put("ingredients", ingredients_json.toString());
            parameters.put("instructions", instructions_json.toString());
            parameters.put("tags", tags_json.toString());
            parameters.put("recipe_id", this.id);
            HTTPManager.getInstance().POSTRequest("user/recipes/add/complete",
                    parameters, success_callback, error_callback, context);
        } catch (JSONException e) {
            error_callback.onResponse(Settings.CONVERT_TO_JSON_FAILED);
        }
    }

    public static void fetchUserRecipes(Context context, GeneralCallback success_callback) {
        HTTPManager.getInstance().request("user/recipes/?favorite=false",
                new HashMap<>(), Request.Method.GET, response_object -> {
                    if (response_object instanceof JSONArray) {
                        fetchRecipesJSONArray((JSONArray) response_object, success_callback);
                    }
                }, error -> {
                }, context);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Recipe) {
            return this.id.equals(((Recipe) obj).getId());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    public void setEditMode() {
        this.edit_mode = true;
    }

    public void update(Context context, Map<String, Object> old_data, IOnRequest success_callback, IOnRequest error_callback) {
        List<Tag> tags = (List<Tag>) old_data.get("tags");
        List<Ingredient> ingredients = (List<Ingredient>) old_data.get("ingredients");
        List<Step> instructions = (List<Step>) old_data.get("instructions");

        Tag.updateRecipe(context, this.id, this.tags, tags);
        Ingredient.updateRecipe(context, this.id, this.ingredients, ingredients);
        Instructions.updateRecipe(context, this.id, this.instructions.getSteps(), instructions);
        success_callback.onResponse(new JSONObject());
    }
}
