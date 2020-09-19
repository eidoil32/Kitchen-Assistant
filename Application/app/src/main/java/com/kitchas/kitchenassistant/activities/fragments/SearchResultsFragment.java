package com.kitchas.kitchenassistant.activities.fragments;

import android.os.Bundle;
import android.view.View;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.adapters.MinRecipeAdapter;
import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;

import org.json.JSONArray;

import java.util.List;

public class SearchResultsFragment extends HomeFragment {
    private JSONArray searchResults;

    public SearchResultsFragment(JSONArray searchResults) {
        super();
        this.searchResults = searchResults;
    }

    @Override
    protected void loadLastViewedRecipes() {
        Recipe.fetchRecipesJSONArray(this.searchResults, recipes -> {
            List<Recipe> recipeList = (List<Recipe>) recipes;
            this.adapter = new MinRecipeAdapter(this.listener, R.layout.adapter_last_recipe, recipeList);
            this.recipes_list_view.setAdapter(adapter);
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.title.setText(getString(R.string.SEARCH_RESULTS));
    }
}
