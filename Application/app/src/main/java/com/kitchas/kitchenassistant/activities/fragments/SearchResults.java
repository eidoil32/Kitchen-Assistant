package com.kitchas.kitchenassistant.activities.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.BaseActivity;
import com.kitchas.kitchenassistant.activities.adapters.MinRecipeAdapter;
import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;
import com.kitchas.kitchenassistant.assistant.models.search.Search;
import com.kitchas.kitchenassistant.utils.Tools;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;

import java.util.List;

public class SearchResults extends BaseActivity
        implements SwipyRefreshLayout.OnRefreshListener {
    protected ListView recipes_list_view;
    protected SwipyRefreshLayout swipe_refresh_layout;
    private int page = 1;
    protected BaseAdapter adapter;
    private String search_query;
    private JSONArray recipes_results;
    private List<Recipe> recipes;

    protected void loadRecipes() {
        Recipe.fetchRecipesJSONArray(this.recipes_results, recipes -> {
            if (this.recipes == null){
                this.recipes = (List<Recipe>) recipes;
            } else {
                this.recipes.addAll((List<Recipe>) recipes);
            }

            this.adapter = new MinRecipeAdapter(this, R.layout.adapter_last_recipe, this.recipes);
            this.recipes_list_view.setAdapter(adapter);
            this.recipes_list_view.setSelection(((this.page - 1) * 10) - 1);
        });
    }

    private void loadMoreRecipes() {
        Search search = new Search(this);
        search.searchRecipes(this.search_query, true, (results) -> {
            this.recipes_results = results;
            this.loadRecipes();
        }, error -> {}, ++page);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Tools.hideTopBar(this);

        Intent intent = getIntent();
        this.search_query = intent.getStringExtra("query");
        this.recipes_list_view = findViewById(R.id.search_results_list_view);
        this.swipe_refresh_layout = findViewById(R.id.search_results_swipe);
        this.swipe_refresh_layout.setOnRefreshListener(this);
        ProgressDialog progress = Tools.showLoading(this, "We searching in lightning speed! please wait..");
        Search search = new Search(this);
        search.searchRecipes(this.search_query, true, (results) -> {
            progress.dismiss();
            this.recipes_results = results;
            loadRecipes();
        }, (error) -> {
            System.out.println("ERROR SEARCHING!");
            progress.dismiss();
        });
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        switch (direction) {
            case TOP:
                this.swipe_refresh_layout.setRefreshing(true);
                this.loadRecipes();
                this.swipe_refresh_layout.setRefreshing(false);
                break;
            case BOTTOM:
                this.swipe_refresh_layout.setRefreshing(true);
                this.loadMoreRecipes();
                this.swipe_refresh_layout.setRefreshing(false);
                break;
        }
    }
}
