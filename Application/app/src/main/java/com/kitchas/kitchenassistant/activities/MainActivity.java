package com.kitchas.kitchenassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.framents.AddRecipeStepOneFragment;
import com.kitchas.kitchenassistant.activities.framents.HomeFragment;
import com.kitchas.kitchenassistant.assistant.models.search.Search;
import com.kitchas.kitchenassistant.assistant.user.User;
import com.kitchas.kitchenassistant.utils.Tools;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity
        implements MaterialSearchBar.OnSearchActionListener{
    private MaterialSearchBar search_bar;
    private DrawerLayout drawer;
    private User user = null;
    private ImageView search_icon_view;
    private static FloatingActionButton fab;


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

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_activity_framelayout, new HomeFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        fab = findViewById(R.id.fab_add_recipe);
        fab.setOnClickListener(view -> {
            FragmentTransaction _fragmentTransaction = getSupportFragmentManager().beginTransaction();
            _fragmentTransaction.replace(R.id.main_activity_framelayout, new AddRecipeStepOneFragment());
            _fragmentTransaction.addToBackStack(null);
            _fragmentTransaction.commit();
            fab.hide();
        });
        setSearchBarActions();
    }

    public static FloatingActionButton getFab() {
        return fab;
    }

    @Override
    protected void speechResult(String query) {
        System.out.println(query);
        // Todo implement speechResults
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

    @Override
    public void onBackPressed() {
        FragmentManager mgr = getSupportFragmentManager();
        if (mgr.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            mgr.popBackStack();
            MainActivity.getFab().show();
        }
    }
}
