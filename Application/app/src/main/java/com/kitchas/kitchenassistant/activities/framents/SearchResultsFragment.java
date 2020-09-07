package com.kitchas.kitchenassistant.activities.framents;

import androidx.fragment.app.Fragment;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.adapters.LastRecipeAdapter;
import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class SearchResultsFragment extends HomeFragment {
    private JSONArray searchResults;

    public SearchResultsFragment(JSONArray searchResults) {
        super();

        this.searchResults = new JSONArray();
        for (int i = 0; i < searchResults.length(); i++) {
            try {
                this.searchResults.put(searchResults.getJSONObject(i).getJSONObject("recipe"));
            } catch (JSONException ignored) { }
        }
    }

    @Override
    protected void loadRecipes() {
        Recipe.fetchRecipesJSONArray(this.searchResults, recipes -> {
            List<Recipe> recipeList = (List<Recipe>) recipes;
            this.adapter = new LastRecipeAdapter(this.listener, R.layout.adapter_last_recipe, recipeList);
            this.recipes_list_view.setAdapter(adapter);
        });
    }
}
