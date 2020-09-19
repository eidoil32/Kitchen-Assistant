package com.kitchas.kitchenassistant.activities.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.adapters.MinRecipeAdapter;
import com.kitchas.kitchenassistant.activities.helpers.DoubleClickListener;
import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;
import com.kitchas.kitchenassistant.utils.Tools;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.List;

public class CommunityFragment extends Fragment
        implements SwipyRefreshLayout.OnRefreshListener {
    protected FragmentActivity listener;
    protected SwipyRefreshLayout swipe_refresh_layout;
    protected BaseAdapter adapter;
    protected ListView recipes_list_view;
    protected TextView title;
    private int page;
    private List<Recipe> recipe_list;

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void loadRecipes() {
        ProgressDialog progress = Tools.showLoading(this.listener, getString(R.string.LOADING_COMMUNITY_RECIPES));
        this.page = 1;
        Recipe.fetchCommunityRecipes(this.listener, recipes -> {
            recipe_list = (List<Recipe>)recipes;
            if (recipe_list.isEmpty()) {
                this.recipes_list_view.setVisibility(View.INVISIBLE);
                this.title.setText(R.string.NO_LAST_RECIPES);
            } else {
                this.recipes_list_view.setVisibility(View.VISIBLE);
                this.adapter = new MinRecipeAdapter(this.listener, R.layout.adapter_last_recipe, recipe_list);
                this.recipes_list_view.setAdapter(adapter);
            }
            progress.dismiss();
        }, response -> {}, this.page, 10);

    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_community_recipes, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
        this.title = this.listener.findViewById(R.id.community_recipe_list_title);
        this.title.setText(getString(R.string.COMMUNITY_RECIPES));
        this.recipes_list_view = this.listener.findViewById(R.id.community_main_last_recipes_list_view);
        this.loadRecipes();
        this.swipe_refresh_layout = this.listener.findViewById(R.id.community_swipe_container);
        this.swipe_refresh_layout.setOnRefreshListener(this);
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

    private void loadMoreRecipes() {
        ProgressDialog progress = Tools.showLoading(this.listener, getString(R.string.LOADING_RECIPES));
        Recipe.fetchCommunityRecipes(this.listener, recipes -> {
            List<Recipe> recipes_results = (List<Recipe>)recipes;
            if (!recipe_list.isEmpty()) {
                System.out.println(recipe_list.get(0).getTitle());
                this.recipe_list.addAll(recipes_results);
                this.recipes_list_view.setVisibility(View.VISIBLE);
                this.adapter = new MinRecipeAdapter(this.listener, R.layout.adapter_last_recipe, recipe_list);
                this.recipes_list_view.setAdapter(adapter);
                this.recipes_list_view.setSelection(((this.page - 1) * 10) - 1);
            }
            progress.dismiss();
        }, response -> {}, ++this.page, 10);
    }
}
