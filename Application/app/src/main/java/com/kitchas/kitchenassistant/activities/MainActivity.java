package com.kitchas.kitchenassistant.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.fragments.AddRecipeStepOneFragment;
import com.kitchas.kitchenassistant.activities.fragments.AddRecipeStepTwoFragment;
import com.kitchas.kitchenassistant.activities.fragments.HomeFragment;
import com.kitchas.kitchenassistant.activities.fragments.SearchResultsFragment;
import com.kitchas.kitchenassistant.activities.fragments.TabAdapter;
import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;
import com.kitchas.kitchenassistant.assistant.models.search.Search;
import com.kitchas.kitchenassistant.assistant.motiondetection.MotionDetector;
import com.kitchas.kitchenassistant.assistant.user.User;
import com.kitchas.kitchenassistant.utils.Tools;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity
        implements MaterialSearchBar.OnSearchActionListener{
    private MaterialSearchBar search_bar;
    private DrawerLayout drawer;
    private User user = null;
    private ImageView search_icon_view;
    private static FloatingActionButton fab;
    private boolean listenToSpeech = false;
    private ViewPager viewPager;
    private static FrameLayout frameLayout;

    public static void showRecipeView() {
        frameLayout.setVisibility(View.VISIBLE);
        frameLayout.bringToFront();
    }

    public static void hideRecipeView() {
        frameLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User user = User.getInstance(this);
        user.refreshData(this);
        MotionDetector.ActiveMotionDetector(this, result -> {
            if (result && !listenToSpeech) {
                listenToSpeech = true;
                speechToTextManager.listen(this, 100);
            }
        }, error -> {
            Toast.makeText(this, "Can not load speech search", Toast.LENGTH_LONG).show();
        });
        setSpeechCode(100);
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
        frameLayout = findViewById(R.id.main_activity_recipe_view_frame);
        TabLayout tabLayout = findViewById(R.id.main_toolbar_layout);
        viewPager = findViewById(R.id.main_activity_framelayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final TabAdapter tabs_adapter = new TabAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabs_adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Set all view of activity
        this.search_icon_view = findViewById(R.id.main_search_icon);
        this.search_bar = findViewById(R.id.searchBar);
        this.search_bar.bringToFront();

        fab = findViewById(R.id.fab_add_recipe);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddRecipeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);
        });
        setSearchBarActions();
    }

    public static FloatingActionButton getFab() {
        return fab;
    }

    @Override
    protected void speechResult(String query) {
        this.onSearchConfirmed(query);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.getSpeechCode()) {
            listenToSpeech = false;
        }
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
        Tools.hideKeyboard(this);
        Search search = new Search(this);
        search.saveSearch(text.toString(), search_bar.getLastSuggestions());
        ProgressDialog progress = Tools.showLoading(this, "We searching in lightning speed! please wait..");
        search.searchInUserRecipe(text.toString(), true, (results) -> {
            this.findViewById(R.id.main_toolbar_layout).setVisibility(View.GONE);
            progress.dismiss();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_activity_framelayout, new SearchResultsFragment(results));
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
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
        if (frameLayout.getVisibility() == View.VISIBLE) {
            frameLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
