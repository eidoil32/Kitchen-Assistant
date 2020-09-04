package com.kitchas.kitchenassistant.activities.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.assistant.models.CustomPair;
import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;

import java.util.List;
import java.util.Map;

public class FullRecipeDetailAdapter extends BaseAdapter<CustomPair<String, String>> {
    public FullRecipeDetailAdapter(@NonNull Context context, int resource, @NonNull List<CustomPair<String, String>> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder view_holder;
        if (convertView == null) {
            convertView = this.inflater.inflate(resource, parent, false);
            view_holder = new ViewHolder(convertView);
            convertView.setTag(view_holder);
        } else {
            view_holder = (ViewHolder) convertView.getTag();
        }

        CustomPair<String, String> pair = getItem(position);
        if (pair != null) {
            view_holder.title.setText(pair.getKey());
            view_holder.content.setText(pair.getValue());
        }

        return convertView;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title, content;
        final View view;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            this.title = view.findViewById(R.id.adapter_view_recipe_title);
            this.content = view.findViewById(R.id.adapter_view_recipe_content);
        }

        public View getView() {
            return view;
        }
    }
}
