package com.kitchas.kitchenassistant.activities.adapters;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.pchmn.materialchips.model.ChipInterface;

public class TagChip implements ChipInterface {
    String label;
    int id;

    public TagChip(String label, int id) {
        this.label = label;
        this.id = id;
    }

    @Override
    public Object getId() {
        return this.id;
    }

    @Override
    public Uri getAvatarUri() {
        return null;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return null;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getInfo() {
        return this.label;
    }
}
