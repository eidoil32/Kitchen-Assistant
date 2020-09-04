package com.kitchas.kitchenassistant.activities.framents;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.adapters.LastRecipeAdapter;
import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;

import java.util.List;

public class HomeFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener{
    private FragmentActivity listener;
    private SwipeRefreshLayout swipe_refresh_layout;
    private BaseAdapter adapter;
    private ListView recipes_list_view;

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

    private void loadRecipes() {
        System.out.println("Refresh...");
        Recipe.fetchCommunityRecipes(this.listener, recipes -> {
            List<Recipe> recipeList = (List<Recipe>)recipes;
            this.adapter = new LastRecipeAdapter(this.listener, R.layout.adapter_last_recipe, recipeList);
            this.recipes_list_view.setAdapter(adapter);
        }, response -> {}, 1, 10);
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
        this.recipes_list_view = this.listener.findViewById(R.id.main_last_recipes_list_view);
        loadRecipes();
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
        this.loadRecipes();
        this.swipe_refresh_layout.setRefreshing(false);
    }
}
