package com.kitchas.kitchenassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.adapters.LastRecipeAdapter;
import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;
import com.kitchas.kitchenassistant.assistant.models.search.Search;
import com.kitchas.kitchenassistant.assistant.user.User;
import com.kitchas.kitchenassistant.utils.Tools;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends BaseActivity
        implements MaterialSearchBar.OnSearchActionListener {
    private MaterialSearchBar search_bar;
    private DrawerLayout drawer;
    private User user = null;
    private ImageView search_icon_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User user = User.getInstance(this);
        if (!user.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            this.start();
        }
    }

    private void start() {
        setContentView(R.layout.activity_application);
        Tools.hideTopBar(this);

        // Set all view of activity
        this.search_icon_view = findViewById(R.id.main_search_icon);
        this.search_bar = findViewById(R.id.searchBar);

        setSearchBarActions();
        ListView last_recipes = findViewById(R.id.main_last_recipes_list_view);
        List<Recipe> recipes = this.getExampleRecipes();
        BaseAdapter adapter = new LastRecipeAdapter(this, R.layout.adapter_last_recipe, new LinkedList<Recipe>());
        last_recipes.setAdapter(adapter);
        last_recipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Recipe recipe = (Recipe) adapterView.getItemAtPosition(position);
                System.out.println("Selected recipe: " + recipe.getTitle());
            }
        });
    }

    private List<Recipe> getExampleRecipes() {
        List<Recipe> recipes = new LinkedList<>();
        Recipe recipe = new Recipe("Test recipe 1", User.getInstance(this));
        recipe.setDescription("This is example for last recipe, we need to get it from local db and ...");
        recipe.setRate(4);
        recipe.setTotal_time(10000);

        recipes.add(recipe);
        return recipes;
    }

    private void setSearchBarActions() {
        search_bar.setOnSearchActionListener(this);
        search_bar.setCardViewElevation(10);
        List<String> lastSearches = Search.loadSearchSuggestionFromDisk(this);
        search_bar.setLastSuggestions(lastSearches);

        this.search_icon_view.setOnClickListener(this::searchIconClicked);
    }

    private void searchIconClicked(View view) {
        TransitionManager.beginDelayedTransition(findViewById(R.id.main_activity_main_constraint));
        search_icon_view.setVisibility(View.GONE);
        search_bar.setVisibility(View.VISIBLE);
        System.out.println(Arrays.toString(search_bar.getLastSuggestions().toArray()));
        search_bar.openSearch();
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled) {
            TransitionManager.beginDelayedTransition(findViewById(R.id.main_activity_main_constraint));
            search_icon_view.setVisibility(View.VISIBLE);
            search_bar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        Search search = new Search(this);
        search.saveSearch(text.toString(), search_bar.getLastSuggestions());
        search.searchInUserRecipe(text.toString(), true, (results) -> {
            System.out.println("Got the results from Server!");
        }, (error) -> {
            System.out.println("ERROR SEARCHING!");
        });
    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_NAVIGATION:
                drawer.openDrawer(GravityCompat.START);
                break;
            case MaterialSearchBar.BUTTON_SPEECH:
                break;
            case MaterialSearchBar.BUTTON_BACK:
                search_bar.closeSearch();
                break;
        }
    }
}
