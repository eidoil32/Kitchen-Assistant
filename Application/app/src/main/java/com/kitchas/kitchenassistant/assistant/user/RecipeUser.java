package com.kitchas.kitchenassistant.assistant.user;

import android.content.Context;

import java.io.Serializable;

public class RecipeUser implements Serializable {
    private String id;

    public RecipeUser() {
        this.id = "self";
    }

    public RecipeUser(String id) {
        this.id = id;
    }
}
