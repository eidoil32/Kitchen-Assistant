package com.kitchas.kitchenassistant.activities.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.adapters.MinRecipeAdapter;
import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;
import com.kitchas.kitchenassistant.assistant.user.User;
import com.kitchas.kitchenassistant.utils.Tools;
import com.kitchas.kitchenassistant.utils.requests.HTTPManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HomeFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {
    protected FragmentActivity listener;
    protected SwipeRefreshLayout swipe_refresh_layout;
    protected BaseAdapter adapter;
    protected ListView recipes_list_view;
    protected TextView title;
    private List<String> last_viewed_recipes;
    private static Set<Recipe> recipeList;

    public static void addNew(Recipe recipe) {
        if (recipeList != null) {
            recipeList.add(recipe);
        }
    }

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
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

    private void loadMyRecipes() {
        ProgressDialog progress = Tools.showLoading(this.listener, getString(R.string.LOADING_MY_RECIPES));
        Recipe.fetchUserRecipes(this.listener, recipes -> {
            List<Recipe> response_recipes = (List<Recipe>) recipes;
            if (recipeList == null) {
                recipeList = new HashSet<>();
            }
            recipeList.addAll(response_recipes);
            loadLastViewedRecipes(progress);
        });
    }

    protected void loadLastViewedRecipes(ProgressDialog progress) {
        if (!this.last_viewed_recipes.isEmpty()) {
            Recipe.fetchListRecipes(this.listener, recipes -> {
                List<Recipe> response_recipes = (List<Recipe>) recipes;
                if (response_recipes.isEmpty() && recipeList.isEmpty()) {
                    this.recipes_list_view.setVisibility(View.INVISIBLE);
                    this.title.setText(R.string.NO_LAST_RECIPES);
                } else {
                    recipeList.addAll(response_recipes);
                    this.recipes_list_view.setVisibility(View.VISIBLE);
                    this.adapter = new MinRecipeAdapter(this.listener, R.layout.adapter_last_recipe, recipeList);
                    this.recipes_list_view.setAdapter(adapter);
                }
                progress.dismiss();
            }, response -> {
                progress.dismiss();
            }, 1, 10, this.last_viewed_recipes);
        }
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_recipe_list, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
        User user = User.getInstance(view.getContext());
        this.last_viewed_recipes = user.getLastViewedRecipes(view.getContext());
        this.title = this.listener.findViewById(R.id.recipe_list_title);
        this.title.setText(getString(R.string.HOME_RECIPES));
        this.recipes_list_view = this.listener.findViewById(R.id.main_last_recipes_list_view);
        this.loadMyRecipes();
        this.recipes_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Recipe recipe = (Recipe) adapterView.getItemAtPosition(position);
            }
        });

        this.swipe_refresh_layout = this.listener.findViewById(R.id.swipe_container);
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
    public void onRefresh() {
        this.swipe_refresh_layout.setRefreshing(true);
        this.loadMyRecipes();
        this.swipe_refresh_layout.setRefreshing(false);
    }
}
