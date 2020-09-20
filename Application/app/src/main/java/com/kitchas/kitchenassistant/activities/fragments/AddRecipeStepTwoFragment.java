package com.kitchas.kitchenassistant.activities.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.activities.AddRecipeActivity;
import com.kitchas.kitchenassistant.activities.EditRecipeActivity;
import com.kitchas.kitchenassistant.activities.adapters.FullRecipeDetailAdapter;
import com.kitchas.kitchenassistant.activities.adapters.IngredientAdapter;
import com.kitchas.kitchenassistant.activities.adapters.InstructionAdapter;
import com.kitchas.kitchenassistant.activities.helpers.OnSwipeTouchListener;
import com.kitchas.kitchenassistant.assistant.models.recipe.Ingredient;
import com.kitchas.kitchenassistant.assistant.models.recipe.Recipe;
import com.kitchas.kitchenassistant.assistant.models.recipe.Tag;
import com.kitchas.kitchenassistant.assistant.models.recipe.Units;
import com.kitchas.kitchenassistant.assistant.models.recipe.instructions.Instructions;
import com.kitchas.kitchenassistant.assistant.models.recipe.instructions.Step;
import com.kitchas.kitchenassistant.utils.GeneralException;
import com.kitchas.kitchenassistant.utils.Tools;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class AddRecipeStepTwoFragment extends Fragment {
    private FragmentActivity listener;
    private ImageView image;
    private Recipe recipe_base;
    final private List<String> tags = new LinkedList<>();
    final private List<Tag> old_tags_list = new LinkedList<>();
    final private List<Ingredient> ingredients  = new LinkedList<>() , old_ingredients_list = new LinkedList<>();
    final private List<Step> instructions_list = new LinkedList<>(), old_instructions_list = new LinkedList<>();
    private IngredientAdapter ingredients_adapter;
    private InstructionAdapter instructions_adapter;

    // activity elements
    private ChipsInput tags_chip_input;
    private Button add_instruction, add_ingredients, finish;
    private SwipeMenuListView ingredients_listView, instructions_listView;

    public AddRecipeStepTwoFragment(JSONObject recipe_base) {
        super();
        this.recipe_base = Recipe.fetchBasic(recipe_base);
    }

    public AddRecipeStepTwoFragment(Recipe recipe_base) {
        super();
        this.recipe_base = recipe_base;
    }

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
        return inflater.inflate(R.layout.activity_create_recipe_step_2, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.add_instruction = this.listener.findViewById(R.id.create_recipe_step_2_add_instructions);
        this.add_ingredients = this.listener.findViewById(R.id.create_recipe_step_2_add_ingredients);
        this.finish = this.listener.findViewById(R.id.create_recipe_step_2_finish);
        TextView title = this.listener.findViewById(R.id.create_recipe_step_2_title);
        title.setText(recipe_base.getTitle());
        this.ingredients_listView = this.listener.findViewById(R.id.create_recipe_step_2_ingredients_list);
        this.instructions_listView = this.listener.findViewById(R.id.create_recipe_step_2_instructions_list);

        this.setChipSettings();
        if (this.recipe_base.isInEditMode()) {
            SwipeMenuCreator creator = menu -> {
                SwipeMenuItem deleteItem = new SwipeMenuItem(listener);
                deleteItem.setBackground(R.color.white);
                deleteItem.setWidth(90);
                deleteItem.setIcon(R.drawable.ic_baseline_delete_24);
                menu.addMenuItem(deleteItem);
            };
            this.instructions_listView.setMenuCreator(creator);
            this.instructions_listView.setOnMenuItemClickListener((position, menu, index) -> {
                this.instructions_list.remove(position);
                this.instructions_adapter.notifyDataSetChanged();
                return false;
            });

            creator = menu -> {
                SwipeMenuItem deleteItem = new SwipeMenuItem(listener);
                deleteItem.setBackground(R.color.white);
                deleteItem.setWidth(90);
                deleteItem.setIcon(R.drawable.ic_baseline_delete_24);
                menu.addMenuItem(deleteItem);
            };
            this.ingredients_listView.setMenuCreator(creator);
            this.ingredients_listView.setOnMenuItemClickListener((position, menu, index) -> {
                this.ingredients.remove(position);
                this.ingredients_adapter.notifyDataSetChanged();
                return false;
            });

            this.instructions_list.addAll(this.recipe_base.getInstructions().getSteps());
            this.old_instructions_list.addAll(this.recipe_base.getInstructions().getSteps());
            this.ingredients.addAll(this.recipe_base.getIngredients());
            this.old_ingredients_list.addAll(this.recipe_base.getIngredients());
            this.old_tags_list.addAll(this.recipe_base.getTags());
            this.recipe_base.getTags().forEach(tag -> {
                tags_chip_input.addChip(tag.getTitle(), tag.getTitle());
            });
        }

        this.setAddInstruction();
        this.setAddIngredients();
        this.setFinish();
    }

    private void setFinish() {
        this.finish.setOnClickListener(view -> {
            List<Tag> selected_tags = Tag.convertString(tags);
            recipe_base.setTags(selected_tags);
            Instructions instructions = new Instructions();
            instructions.setSteps(instructions_list);
            recipe_base.setInstructions(instructions);
            recipe_base.setIngredients(ingredients);

            Map<String, Object> old_data = new HashMap<>();
            old_data.put("instructions", old_instructions_list);
            old_data.put("ingredients", old_ingredients_list);
            old_data.put("tags", old_tags_list);

            if (recipe_base.isInEditMode()) {
                recipe_base.update(this.listener, old_data, response -> {
                    ((EditRecipeActivity) this.listener).endEdit(this.recipe_base.getId());
                }, error -> {
                    Toast.makeText(this.listener, R.string.EDIT_RECIPE_FAILED, Toast.LENGTH_SHORT).show();
                });
            } else {
                recipe_base.save(this.listener, response -> {
                    ((AddRecipeActivity) this.listener).endAddRecipeEvent(this.recipe_base.getId());
                }, error -> {
                    Toast.makeText(this.listener, R.string.CREATE_RECIPE_FAILED, Toast.LENGTH_SHORT).show();
                });
            }

        });
    }

    private void setAddIngredients() {
        this.ingredients_adapter = new IngredientAdapter(this.listener, R.layout.adapter_ingredient, this.ingredients);
        this.ingredients_listView.setAdapter(ingredients_adapter);
        this.add_ingredients.setOnClickListener(view -> {
            final String[] unit_selected = {null};
            View inputTextView = LayoutInflater
                    .from(this.listener)
                    .inflate(R.layout.dialog_ingredients_page, (ViewGroup) view.getParent(), false);
            final TextInputEditText description = inputTextView.findViewById(R.id.dialog_ingredients_description_edit);
            final TextInputEditText title = inputTextView.findViewById(R.id.dialog_ingredients_title_edit);
            final TextInputLayout units = inputTextView.findViewById(R.id.dialog_ingredients_unit);
            final AutoCompleteTextView units_selector = inputTextView.findViewById(R.id.dialog_ingredients_unit_selector);
            final TextInputEditText amount = inputTextView.findViewById(R.id.dialog_ingredients_amount_edit);
            final TextInputEditText[] inputs = {title, amount};
            AlertDialog dialog = new MaterialAlertDialogBuilder(view.getContext())
                    .setTitle(R.string.ADD_NEW_INGREDIENT)
                    .setPositiveButton(R.string.BTN_ADD, null)
                    .setNegativeButton(R.string.CANCEL_BTN, null)
                    .setView(inputTextView).create();

            Context context;
            ArrayAdapter adapter = new ArrayAdapter<>(
                    this.listener,
                    android.R.layout.simple_list_item_1,
                    Units.possible_units());
            units_selector.setAdapter(adapter);
            units_selector.setOnItemClickListener((adapterView, view1, i, l) -> {
                String selected = (String)adapterView.getItemAtPosition(i);
                if (selected.equals("SELECT")) {
                    unit_selected[0] = null;
                } else {
                    if (Units.possible_units().contains(selected)) {
                        unit_selected[0] = selected;
                    } else {
                        units_selector.setError(getString(R.string.INVALID_UNIT_SELECTED));
                    }
                }
            });

            dialog.setOnShowListener(dialogInterface -> {
                Button add = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                add.setOnClickListener(add_view -> {
                    boolean required_not_empty = true;
                    for (TextInputEditText input : inputs) {
                        if (Tools.isEmpty(input)) {
                            input.setError(getString(R.string.EMPTY_REQUIRE));
                            required_not_empty = false;
                        }
                    }

                    if (unit_selected[0] == null) {
                        units.setError(getString(R.string.EMPTY_REQUIRE));
                    } else if (required_not_empty) {
                        Ingredient ingredient = new Ingredient(
                                title.getText().toString(),
                                description.getText().toString(),
                                Float.parseFloat(amount.getText().toString()),
                                Units.valueOf(unit_selected[0]),
                                ingredients.size() + 1);
                        ingredients.add(ingredient);
                        ingredients_adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                cancel.setOnClickListener(cancel_view -> dialog.dismiss());
            });

            dialog.show();
        });
    }

    private void setAddInstruction() {
        this.instructions_adapter = new InstructionAdapter(this.listener, R.layout.adapter_instruction, this.instructions_list);
        this.instructions_listView.setAdapter(instructions_adapter);

        this.add_instruction.setOnClickListener(view -> {
            View inputTextView = LayoutInflater
                    .from(this.listener)
                    .inflate(R.layout.dialog_instruction_page, (ViewGroup) view.getParent(), false);
            final TextInputEditText description = inputTextView.findViewById(R.id.dialog_instruction_description_edit);
            final TextInputEditText time = inputTextView.findViewById(R.id.dialog_instruction_time_edit);
            final TextInputEditText special_notes = inputTextView.findViewById(R.id.dialog_instruction_special_notes_edit);
            AlertDialog dialog = new MaterialAlertDialogBuilder(view.getContext())
                    .setTitle(R.string.ADD_NEW_INSTRUCTION)
                    .setPositiveButton(R.string.BTN_ADD, null)
                    .setNegativeButton(R.string.CANCEL_BTN, null)
                    .setNegativeButton(R.string.CANCEL_BTN, (dialogInterface, buttonPressed) -> {})
                    .setView(inputTextView)
                    .create();

            dialog.setOnShowListener(dialogInterface -> {
                Button add = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                add.setOnClickListener(add_view -> {
                    if (Tools.isEmpty(description)) {
                        description.setError(getString(R.string.EMPTY_REQUIRE));
                    } else {
                        if (Tools.isEmpty(time)) {
                            time.setError(getString(R.string.EMPTY_REQUIRE));
                        } else {
                            String _special_notes = null;
                            if (!Tools.isEmpty(special_notes)) {
                                _special_notes = special_notes.getText().toString();
                            }
                            Step step = new Step(description.getText().toString(), _special_notes, Integer.parseInt(time.getText().toString()));
                            instructions_list.add(step);
                            instructions_adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                });

                cancel.setOnClickListener(cancel_view -> dialog.dismiss());
            });
            dialog.show();
        });
    }

    private void setChipSettings() {
        this.tags_chip_input = this.listener.findViewById(R.id.create_recipe_step_2_chip_list);
        tags_chip_input.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                tags.add(chip.getLabel());
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                tags.remove(chip.getLabel());
            }

            @Override
            public void onTextChanged(CharSequence text) {
                int text_size = text.length();
                if (text_size > 1) {
                    if (text.charAt(text_size - 1) == ' ') {
                        String tag = text.subSequence(0, text_size - 1).toString().trim();
                        tags_chip_input.addChip(tag, tag);
                    }
                }
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