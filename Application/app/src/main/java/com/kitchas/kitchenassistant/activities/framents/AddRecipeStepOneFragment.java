package com.kitchas.kitchenassistant.activities.framents;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.utils.Tools;
import com.kitchas.kitchenassistant.utils.requests.HTTPManager;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class AddRecipeStepOneFragment extends Fragment {
    private FragmentActivity listener;
    private ImageView image;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_create_recipe_step_1, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.image = this.listener.findViewById(R.id.create_recipe_step_1_image);
        Button load_image = this.listener.findViewById(R.id.create_recipe_step_1_load_image);
        Button next = this.listener.findViewById(R.id.create_recipe_step_1_next);
        TextInputLayout title = this.listener.findViewById(R.id.create_recipe_step_1_title);
        TextInputLayout description = this.listener.findViewById(R.id.create_recipe_step_1_description);

        load_image.setOnClickListener(load_image_view -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto , 1); //one can be replaced with any action code
        });

        next.setOnClickListener(next_view -> {
            BitmapDrawable drawable = (BitmapDrawable) this.image.getDrawable();
            Bitmap bitmap = null;
            if (drawable != null) {
                bitmap = drawable.getBitmap();
            }

            String recipe_title = null, recipe_description = null;
            if (title.getEditText() != null) {
                recipe_title = title.getEditText().getText().toString();
            } else {
                title.setError(getString(R.string.EMPTY_TITLE));
            }
            if (description.getEditText() != null) {
                recipe_description = description.getEditText().getText().toString();
            } else {
                description.setError(getString(R.string.EMPTY_DESCRIPTION));
            }

            if (recipe_description != null && recipe_title != null) {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("title", recipe_title);
                parameters.put("description", recipe_description);
                parameters.put("adate", String.valueOf(Tools.getCurrentTimeStamp()));
                HTTPManager.getInstance().POSTMultipartRequest("user/recipes", parameters, bitmap, response -> {
                    FragmentTransaction _fragmentTransaction = ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction();
                    _fragmentTransaction.replace(R.id.main_activity_framelayout, new AddRecipeStepTwoFragment(response));
                    _fragmentTransaction.commit();
                }, error -> {
                    AlertDialog dialog = new MaterialAlertDialogBuilder(view.getContext())
                            .setTitle(R.string.ERROR_CREATING_RECIPE)
                            .setPositiveButton(R.string.OK_BTN_TEXT, (dialogInterface, i) -> {

                            })
                            .show();
                }, this.listener.getApplicationContext());
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    this.image.setImageURI(selectedImage);
                }
                break;
        }
    }
}