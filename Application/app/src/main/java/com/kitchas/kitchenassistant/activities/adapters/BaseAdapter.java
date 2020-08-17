package com.kitchas.kitchenassistant.activities.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

abstract class BaseAdapter<T> extends ArrayAdapter<T> {

    public BaseAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
    }
}
