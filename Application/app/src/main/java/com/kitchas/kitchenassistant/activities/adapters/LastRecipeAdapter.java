package com.kitchas.kitchenassistant.activities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;

import java.util.List;

public class LastRecipeAdapter extends BaseAdapter<Recipe> {
    public LastRecipeAdapter(@NonNull Context context, int resource, List<Recipe> recipes) {
        super(context ,resource, recipes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Recipe recipe = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_last_recipe, parent, false);
        }

        TextView title = parent.findViewById(R.id.adapter_last_recipe_title);
        TextView description = parent.findViewById(R.id.adapter_last_recipe_description);
        TextView rating = parent.findViewById(R.id.adapter_last_recipe_rating_text);
        TextView total_time = parent.findViewById(R.id.adapter_last_recipe_total_time_text);

        if (recipe != null) {
            title.setText(recipe.getTitle());
            description.setText(recipe.getDescription());
            rating.setText(recipe.getRate());
            total_time.setText(recipe.getTotal_time());
        }

        return convertView;
    }
}