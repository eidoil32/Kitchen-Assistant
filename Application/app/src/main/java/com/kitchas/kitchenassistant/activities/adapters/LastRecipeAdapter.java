package com.kitchas.kitchenassistant.activities.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;
import com.kitchas.kitchenassistant.utils.Settings;

import java.util.List;

public class LastRecipeAdapter extends BaseAdapter<Recipe> {
    private List<Recipe> recipes;
    private int resource;
    private ViewHolder view_holder;
    private LayoutInflater inflater;

    public LastRecipeAdapter(@NonNull Context context, int resource, List<Recipe> recipes) {
        super(context ,resource, recipes);
        this.recipes = recipes;
        this.resource = resource;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Recipe recipe = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
            this.view_holder = new ViewHolder(convertView);
            convertView.setTag(view_holder);
        } else {
            this.view_holder = (ViewHolder) convertView.getTag();
        }

        if (recipe != null) {
            this.view_holder.title.setText(recipe.getTitle());
            this.view_holder.description.setText(recipe.getDescription());
            if (recipe.getImage() != null) {
                byte[] decodedString = Base64.decode(recipe.getImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                this.view_holder.recipe_image.setImageBitmap(decodedByte);
            }
            this.view_holder.rating_bar.setRating((float)recipe.getRate());
            this.view_holder.rating_bar.setOnRatingBarChangeListener((ratingBar, rate, b) -> {
                recipe.rate(rate, context, response -> {
                    ratingBar.setRating(recipe.getRate());
                }, error -> {});
            });
            this.view_holder.rating_value.setText(String.format("%.1f/5", recipe.getRate()));
            this.view_holder.total_time.setText(String.format("%d hours", recipe.getTotal_time()));
            this.view_holder.show_more.setOnClickListener(view -> {
                
            });
        }

        return convertView;
    }

    @Override
    public long getItemId(int index) {
        Recipe item = getItem(index);
        return this.recipes.indexOf(item);
    }

    @Override
    public int getCount() {
        return recipes.size();
    }

    private class ViewHolder {
        final TextView title, description, total_time, rating_value;
        final Button show_more;
        final View view;
        final RatingBar rating_bar;
        final ImageView recipe_image;

        ViewHolder(View v) {
            this.title = v.findViewById(R.id.adapter_last_recipe_title);
            this.description = v.findViewById(R.id.adapter_last_recipe_description);
            //this.rating = v.findViewById(R.id.adapter_last_recipe_rating_text);
            this.total_time = v.findViewById(R.id.adapter_last_recipe_total_time_text);
            this.view = v;
            this.recipe_image = v.findViewById(R.id.adapter_last_recipe_image);
            this.show_more = v.findViewById(R.id.adapter_last_recipe_show_more);
            this.rating_bar = v.findViewById(R.id.adapter_last_recipe_rating_bar);
            this.rating_value = v.findViewById(R.id.adapter_last_recipe_rating_text);
        }

        public View getView() {
            return view;
        }
    }
}