package com.kitchas.kitchenassistant.activities.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.assistant.models.recipe.Ingredient;

import java.util.List;

public class IngredientAdapter extends BaseAdapter<Ingredient> {
    public IngredientAdapter(@NonNull Context context, int resource, @NonNull List<Ingredient> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Ingredient ingredient = objects.get(position);

        IngredientAdapter.ViewHolder view_holder;
        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
            view_holder = new IngredientAdapter.ViewHolder(convertView);
            convertView.setTag(view_holder);
        } else {
            view_holder = (IngredientAdapter.ViewHolder) convertView.getTag();
        }

        if (ingredient != null) {
            view_holder.title.setText(ingredient.getTitle());
            view_holder.description.setText(ingredient.getDescription());
            view_holder.amount.setText(String.valueOf(ingredient.getAmount()));
            view_holder.unit.setText(String.valueOf(ingredient.getUnit()));
        }

        return convertView;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title, description, unit, amount;
        final View view;

        ViewHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.adapter_ingredient_title);
            this.description = view.findViewById(R.id.adapter_ingredient_description);
            this.unit = view.findViewById(R.id.adapter_ingredient_units);
            this.amount = view.findViewById(R.id.adapter_ingredient_amount);
            this.view = view;
        }

        public View getView() {
            return view;
        }
    }
}
