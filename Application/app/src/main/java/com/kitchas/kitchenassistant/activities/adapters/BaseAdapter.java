package com.kitchas.kitchenassistant.activities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;

import java.util.List;

abstract class BaseAdapter<T> extends ArrayAdapter<T> {
    protected Context context;
    protected int resource;
    protected List<T> objects;
    protected LayoutInflater inflater;

    public BaseAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public long getItemId(int index) {
        T item = getItem(index);
        return this.objects.indexOf(item);
    }

    @Override
    public int getCount() {
        return objects.size();
    }
}
