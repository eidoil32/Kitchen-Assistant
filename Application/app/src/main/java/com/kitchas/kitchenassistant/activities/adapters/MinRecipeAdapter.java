package com.kitchas.kitchenassistant.activities.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.AddRecipeActivity;
import com.kitchas.kitchenassistant.activities.MainActivity;
import com.kitchas.kitchenassistant.activities.fragments.RecipeViewFragment;
import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;
import com.kitchas.kitchenassistant.utils.Settings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class MinRecipeAdapter extends BaseAdapter<Recipe> {
    public static int num = 0;

    public MinRecipeAdapter(@NonNull Context context, int resource, List<Recipe> recipes) {
        super(context, resource, recipes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Recipe recipe = getItem(position);

        ViewHolder view_holder;
        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
            view_holder = new ViewHolder(convertView);
            convertView.setTag(view_holder);
        } else {
            view_holder = (ViewHolder) convertView.getTag();
        }

        if (recipe != null) {
            view_holder.title.setText(recipe.getTitle());
            view_holder.description.setText(recipe.getDescription());
            if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
                Glide.with(convertView)
                        .load(recipe.getImage())
                        .centerCrop()
                        .into(view_holder.recipe_image);
            }
            view_holder.rating_bar.setRating((float) recipe.getRate());
            view_holder.rating_value.setText(String.format("%.1f/5", recipe.getRate()));
            view_holder.rating_bar.setOnRatingBarChangeListener((ratingBar, rate, by_user) -> {
                if (by_user) {
                    recipe.rate(rate, context, response -> {
                        float new_rate = recipe.getRate();
                        ratingBar.setRating(new_rate);
                        view_holder.rating_value.setText(String.format("%.1f/5", recipe.getRate()));
                    }, error -> {
                        ratingBar.setRating(recipe.getRate());
                        Toast.makeText(context, context.getString(R.string.CANNOT_RATE_TWICE), Toast.LENGTH_SHORT).show();
                    });
                }
            });
            view_holder.total_time.setText(recipe.getTotalTimeCook());
            view_holder.show_more.setOnClickListener(view -> {
                MainActivity.showRecipeView();
                FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_activity_recipe_view_frame, new RecipeViewFragment(recipe.getId()));
                fragmentTransaction.commit();
            });
        }

        return convertView;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title, description, total_time, rating_value;
        final Button show_more;
        final View view;
        final RatingBar rating_bar;
        final ImageView recipe_image;

        ViewHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.adapter_last_recipe_title);
            this.description = view.findViewById(R.id.adapter_last_recipe_description);
            this.total_time = view.findViewById(R.id.adapter_last_recipe_total_time_text);
            this.view = view;
            this.recipe_image = view.findViewById(R.id.adapter_last_recipe_image);
            this.show_more = view.findViewById(R.id.adapter_last_recipe_show_more);
            this.rating_bar = view.findViewById(R.id.adapter_last_recipe_rating_bar);
            this.rating_value = view.findViewById(R.id.adapter_last_recipe_rating_text);
        }

        public View getView() {
            return view;
        }
    }
}