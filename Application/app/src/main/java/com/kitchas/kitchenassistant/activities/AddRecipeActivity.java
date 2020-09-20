package com.kitchas.kitchenassistant.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.fragments.AddRecipeStepOneFragment;
import com.kitchas.kitchenassistant.utils.Tools;

public class AddRecipeActivity extends AppCompatActivity {
    private ImageView back_btn;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        Tools.hideTopBar(this);

        this.back_btn = findViewById(R.id.add_recipe_back);
        this.back_btn.setOnClickListener(v -> finish());

        FragmentTransaction _fragmentTransaction = getSupportFragmentManager().beginTransaction();
        _fragmentTransaction.replace(R.id.add_recipe_activity_fragment, new AddRecipeStepOneFragment());
        _fragmentTransaction.commit();
    }

    public void endAddRecipeEvent(String recipe_id) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("recipe_id", recipe_id);
        setResult(Activity.RESULT_OK, returnIntent);
        this.finish();
    }

    public void hideBackBtn() {
        this.back_btn.setVisibility(View.GONE);
    }
}
