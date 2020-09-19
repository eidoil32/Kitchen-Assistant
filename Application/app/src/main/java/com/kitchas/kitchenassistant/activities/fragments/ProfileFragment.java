package com.kitchas.kitchenassistant.activities.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.LoginActivity;
import com.kitchas.kitchenassistant.assistant.user.User;
import com.kitchas.kitchenassistant.utils.requests.HTTPManager;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment{
    protected FragmentActivity listener;
    private User user;
    private ImageView image;

    public ProfileFragment(User user) {
        this.user = user;
    }
    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView name = this.listener.findViewById(R.id.profile_name);
        this.image = this.listener.findViewById(R.id.profile_image);
        TextInputLayout email = this.listener.findViewById(R.id.profile_email);
        TextInputLayout age = this.listener.findViewById(R.id.profile_age);
        Button save = this.listener.findViewById(R.id.profile_save);
        Button logout = this.listener.findViewById(R.id.profile_logout);

        String user_age = String.valueOf(user.getAge());

        email.getEditText().setText(user.getEmail());
        if (user.getAge() != 0) {
            age.getEditText().setText(String.valueOf(user.getAge()));
        }
        if (user.getAvatar() == null) {
            image.setImageResource(R.drawable.ic_user_profile_picture);
        } else {
            Glide.with(view)
                    .load(user.getAvatar())
                    .centerCrop()
                    .into(image);
        }
        name.setText(user.getName());

        image.setOnClickListener(view1 -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto , 1); //one can be replaced with any action code
        });

        save.setOnClickListener(view1 -> {
            if (age.getEditText() != null &&
                    !age.getEditText().getText().toString().equals(user_age)) {
                Map<String, String> params = new HashMap<>();
                params.put("age", age.getEditText().getText().toString());
                HTTPManager.getInstance().PATCHRequest("users/profile", params, response -> {
                    user.setAge(age.getEditText().getText().toString());
                    age.clearFocus();
                    Toast.makeText(this.listener, R.string.PROFILE_UPDATE_SUCCESS, Toast.LENGTH_SHORT).show();
                }, error -> {
                    Toast.makeText(this.listener, R.string.PROFILE_UPDATE_FAILED, Toast.LENGTH_SHORT).show();
                }, this.listener);
            }
        });

        logout.setOnClickListener(view1 -> {
            AlertDialog dialog = new MaterialAlertDialogBuilder(this.listener)
                    .setTitle(R.string.LOGOUT_TITLE_DIALOG)
                    .setPositiveButton(R.string.OK_BTN_TEXT, (dialogInterface, i) -> {
                        HTTPManager.getInstance().POSTRequest("users/logout", new HashMap<>(),
                                response -> {
                                    if (User.getInstance(this.listener).logout(this.listener)) {
                                        Intent intent = new Intent(this.listener, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("USER_LOGGED_OUT", true);
                                        startActivity(intent);
                                    }
                                }, error -> {
                                    Toast.makeText(this.listener, R.string.LOGOUT_FAILED, Toast.LENGTH_SHORT).show();
                                }, this.listener);
                    })
                    .setNegativeButton(R.string.CANCEL_BTN, (dialogInterface, i) -> dialogInterface.cancel())
                    .show();
        });
    }

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
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
                    androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this.listener)
                            .setTitle(R.string.UPDATE_PROFILE_PIC)
                            .setPositiveButton(R.string.NEXT_BTN, (dialogInterface, buttonPressed) -> {
                                Uri selectedImage = imageReturnedIntent.getData();
                                this.image.setImageURI(selectedImage);
                                try {
                                    Bitmap bitmap =  MediaStore.Images.Media.getBitmap(this.listener.getContentResolver(), selectedImage);
                                    HTTPManager.getInstance().POSTMultipartRequest(
                                            "user/me/avatar", new HashMap<>(), bitmap, response -> {
                                                try {
                                                    user.setAvatar(response.getString("avatar"));
                                                    Toast.makeText(this.listener, R.string.PROFILE_UPDATE_SUCCESS, Toast.LENGTH_SHORT).show();
                                                } catch (JSONException ignored) {}
                                            }, error -> {
                                                Toast.makeText(this.listener, R.string.PROFILE_UPDATE_FAILED, Toast.LENGTH_LONG).show();
                                            }, this.listener);
                                } catch (Exception ignored) {
                                    Toast.makeText(this.listener, R.string.PROFILE_UPDATE_FAILED, Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton(R.string.CANCEL_BTN, (dialogInterface, buttonPressed) -> {})
                            .show();
                }
                break;
        }
    }
}
