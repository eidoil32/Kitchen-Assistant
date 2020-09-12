package com.kitchas.kitchenassistant.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.framents.AddRecipeStepOneFragment;

public class AddRecipeActivity extends AppCompatActivity {
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        FragmentTransaction _fragmentTransaction = getSupportFragmentManager().beginTransaction();
        _fragmentTransaction.replace(R.id.add_recipe_activity_fragment, new AddRecipeStepOneFragment());
        _fragmentTransaction.commit();
    }
}
