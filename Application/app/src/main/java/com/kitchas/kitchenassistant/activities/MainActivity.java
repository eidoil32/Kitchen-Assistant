package com.kitchas.kitchenassistant.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.fragments.RecipeViewFragment;
import com.kitchas.kitchenassistant.activities.fragments.SearchResults;
import com.kitchas.kitchenassistant.activities.fragments.TabAdapter;
import com.kitchas.kitchenassistant.assistant.models.search.Search;
import com.kitchas.kitchenassistant.assistant.motiondetection.MotionDetector;
import com.kitchas.kitchenassistant.assistant.user.User;
import com.kitchas.kitchenassistant.utils.Settings;
import com.kitchas.kitchenassistant.utils.Tools;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.List;

public class MainActivity extends BaseActivity
        implements MaterialSearchBar.OnSearchActionListener {
    private MaterialSearchBar search_bar;
    private DrawerLayout drawer;
    private User user = null;
    private ImageView search_icon_view;
    private static FloatingActionButton fab;
    private boolean listenToSpeech = false;
    private ViewPager viewPager;
    private static FrameLayout frameLayout;
    private ImageView micBtn;
    private static FragmentTransaction fragmentTransaction;

    public static void showRecipeView() {
        fab.hide();
        frameLayout.setVisibility(View.VISIBLE);
        frameLayout.bringToFront();
    }

    public static void showRecipeView(String recipe_id) {
        showRecipeView();
        fragmentTransaction.replace(R.id.main_activity_recipe_view_frame, new RecipeViewFragment(recipe_id));
        fragmentTransaction.commit();
    }

    public static void hideRecipeView() {
        frameLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (getIntent().getBooleanExtra("USER_LOGGED_OUT", false)) {
            finish();
        }

        User user = User.getInstance(this);
        MotionDetector.ActiveMotionDetector(this, result -> {
            boolean res = getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
            if (res && result && !listenToSpeech) {
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
            user.refreshData(this);
            this.start();
        }

        micBtn = findViewById(R.id.main_mic_icon);
        micBtn.setOnClickListener(view -> {
            if (!listenToSpeech) {
                listenToSpeech = true;
                speechToTextManager.listen(this, 100);
            }
        });
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
                if (tab.getPosition() != TabAdapter.PROFILE_TAB_NO) {
                    fab.show();
                } else {
                    fab.hide();
                }
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
            startActivityForResult(intent, Settings.ADD_RECIPE_FINISH_OK);
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

        switch (requestCode) {
            case Settings.ADD_RECIPE_FINISH_OK:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        String id = data.getStringExtra("recipe_id");
                        showRecipeView(id);
                    }
                }
                break;
            default:
                break;
        }

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
        search_bar.closeSearch();
        Intent intent = new Intent(MainActivity.this, SearchResults.class);
        intent.putExtra("query", text.toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(intent);
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
            fab.show();
            frameLayout.setVisibility(View.GONE);
        } else {
            if (this.viewPager.getCurrentItem() == TabAdapter.HOME_TAB_NO) {
                finishAffinity();
            } else {
                this.viewPager.setCurrentItem(TabAdapter.HOME_TAB_NO);
            }
        }
    }
}
