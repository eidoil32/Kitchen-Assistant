package com.kitchas.kitchenassistant.activities.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.adapters.FullRecipeDetailAdapter;
import com.kitchas.kitchenassistant.activities.adapters.LastRecipeAdapter;
import com.kitchas.kitchenassistant.assistant.models.CustomPair;
import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;
import com.kitchas.kitchenassistant.assistant.user.User;
import com.kitchas.kitchenassistant.assistant.user.User;
import com.kitchas.kitchenassistant.utils.Tools;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RecipeViewFragment extends Fragment {
    private FragmentActivity listener;
    private String recipe_id;
    private Recipe recipe;

    public RecipeViewFragment(String recipe_id) {
        this.recipe_id = recipe_id;
        this.recipe = null;
    }

    public RecipeViewFragment(Recipe recipe) {
        this.recipe_id = recipe.getId();
        this.recipe = recipe;
    }

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

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_recipe_view, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.recipe == null) {
            ProgressDialog progress = Tools.showLoading(view.getContext());
            Recipe.loadRecipeByID(this.recipe_id, this.getContext(), _recipe -> {
                if (_recipe instanceof Recipe) {
                    Recipe recipe = (Recipe) _recipe;
                    loadRecipe(recipe);

                    User user = User.getInstance(view.getContext());
                    user.saveRecipeToLastViewed(view.getContext(), recipe.getId());

                    progress.dismiss();
                }
            }, error -> {
                System.out.println("Error loading recipe");
                System.out.println(error);
            });
        } else {
            loadRecipe(this.recipe);
        }
    }

    private void loadRecipe(Recipe recipe) {
        TextView title = (TextView) this.listener.findViewById(R.id.view_recipe_title);
        TextView description = (TextView) this.listener.findViewById(R.id.view_recipe_description);
        ImageView image = (ImageView) this.listener.findViewById(R.id.view_recipe_image);
        if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
            Glide.with(this)
                    .load(recipe.getImage())
                    .centerCrop()
                    .into(image);
        }
        RatingBar rating_bar = (RatingBar) this.listener.findViewById(R.id.view_recipe_rating_bar);
        rating_bar.setRating((float)recipe.getRate());
        rating_bar.setVisibility(View.VISIBLE);
        TextView rating_value = (TextView) this.listener.findViewById(R.id.view_recipe_rating_text);
        rating_value.setText(String.format("%.1f/5", recipe.getRate()));
        title.setText(recipe.getTitle());
        description.setText(recipe.getDescription());
        ListView full_details = this.listener.findViewById(R.id.view_recipe_list_view);
        List<CustomPair<String, String>> details = new LinkedList<>();
        details.add(new CustomPair<String, String>(this.listener.getString(R.string.STEP), recipe.printSteps()));
        details.add(new CustomPair<String, String>(this.listener.getString(R.string.INGREDIENTS), recipe.printIngredients()));
        BaseAdapter adapter = new FullRecipeDetailAdapter(this.listener, R.layout.adapter_view_recipe_full, details);
        full_details.setAdapter(adapter);
        TextView total_time = (TextView) this.listener.findViewById(R.id.view_recipe_total_time_text);
        total_time.setText(recipe.getTotalTimeCook());
        this.listener.findViewById(R.id.view_recipe_clock_time_icon).setVisibility(View.VISIBLE);
    }

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
    }

    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
