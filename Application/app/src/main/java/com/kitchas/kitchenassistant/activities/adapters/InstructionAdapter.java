package com.kitchas.kitchenassistant.activities.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.kitchas.kitchenassistant.R;
import com.kitchas.kitchenassistant.assistant.models.recipe.Ingredient;
import com.kitchas.kitchenassistant.assistant.models.recipe.instructions.Step;

import java.util.List;

public class InstructionAdapter extends BaseAdapter<Step> {
    public InstructionAdapter(@NonNull Context context, int resource, @NonNull List<Step> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Step step = objects.get(position);

        InstructionAdapter.ViewHolder view_holder;
        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
            view_holder = new InstructionAdapter.ViewHolder(convertView);
            convertView.setTag(view_holder);
        } else {
            view_holder = (InstructionAdapter.ViewHolder) convertView.getTag();
        }

        if (step != null) {
            if (step.getSpecial_notes() == null || step.getSpecial_notes().isEmpty()) {
                view_holder.special_note.setVisibility(View.GONE);
            } else {
                view_holder.special_note.setText(step.getSpecial_notes());
            }
            view_holder.description.setText(step.getDescription());
            view_holder.time.setText(String.format("%d %s", step.getTime(), context.getString(R.string.POSTFIX_MINUTES)));
            view_holder.position.setText(String.valueOf(position + 1));
        }

        return convertView;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        final TextView description, special_note, time, position;
        final View view;

        ViewHolder(View view) {
            super(view);
            this.description = view.findViewById(R.id.adapter_instruction_description);
            this.special_note = view.findViewById(R.id.adapter_instruction_notes);
            this.time = view.findViewById(R.id.adapter_instruction_time);
            this.position = view.findViewById(R.id.adapter_instruction_position);
            this.view = view;
        }

        public View getView() {
            return view;
        }
    }
}
