package com.kitchas.kitchenassistant.activities.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.adapters.MinRecipeAdapter;
import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;
import com.kitchas.kitchenassistant.utils.Tools;
import com.kitchas.kitchenassistant.utils.requests.HTTPManager;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FavoritesFragment extends Fragment
        implements SwipyRefreshLayout.OnRefreshListener {
    protected FragmentActivity listener;
    private int page = 1;
    private static List<Recipe> recipe_list = new LinkedList<>();
    private ListView recipes_list_view;
    private SwipyRefreshLayout swipe_layout;
    protected BaseAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_favorites, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView title = this.listener.findViewById(R.id.favorite_title);
        title.setText(R.string.MAIN_FAVORITE_TAB_LABEL);

        this.recipes_list_view = this.listener.findViewById(R.id.favorite_list_view);
        this.swipe_layout = this.listener.findViewById(R.id.favorite_swipe_container);
        this.swipe_layout.setOnRefreshListener(this);
        loadRecipes();
    }

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        this.swipe_layout.setRefreshing(true);
        switch (direction) {
            case TOP:
                this.loadRecipes();
                break;
            case BOTTOM:
                this.loadMoreRecipes();
                break;
        }
        this.swipe_layout.setRefreshing(false);
    }

    private void loadRecipes() {
        ProgressDialog progress = Tools.showLoading(this.listener, getString(R.string.LOADING_FAVORITE_RECIPES));
        Recipe.fetchFavoriteRecipes(this.listener, recipes -> {
            recipe_list = (List<Recipe>)recipes;
            if (!recipe_list.isEmpty()) {
                this.recipes_list_view.setVisibility(View.VISIBLE);
                this.adapter = new MinRecipeAdapter(this.listener, R.layout.adapter_last_recipe, recipe_list, false);
                this.recipes_list_view.setAdapter(adapter);
            } else {
                this.listener.findViewById(R.id.favorite_no_recipes).setVisibility(View.VISIBLE);
            }
            progress.dismiss();
        }, response -> {}, 1, 10);
    }

    public static void addNewLikedRecipe(Recipe recipe) {
        if (recipe_list != null)
            recipe_list.add(recipe);
    }

    private void loadMoreRecipes() {
        ProgressDialog progress = Tools.showLoading(this.listener, getString(R.string.LOADING_RECIPES));
        Recipe.fetchFavoriteRecipes(this.listener, recipes -> {
            List<Recipe> recipes_results = (List<Recipe>)recipes;
            recipe_list.addAll(recipes_results);
            this.recipes_list_view.setVisibility(View.VISIBLE);
            this.adapter = new MinRecipeAdapter(this.listener, R.layout.adapter_last_recipe, recipe_list);
            this.recipes_list_view.setAdapter(adapter);
            if (this.page > 1) {
                this.recipes_list_view.setSelection(((this.page - 1) * 10) - 1);
            }
            progress.dismiss();
        }, response -> {}, this.page++, 10);
    }
}
