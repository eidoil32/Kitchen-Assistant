package com.kitchas.kitchenassistant.assistant.user;

import android.content.Context;

public class RecipeUser {
    private String id;

    public RecipeUser() {
        this.id = "self";
    }

    public RecipeUser(String id) {
        this.id = id;
    }
}
