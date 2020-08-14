package com.kitchas.kitchenassistant.assistant.models.recipe.instructions;

import java.util.LinkedList;
import java.util.List;

public class Instructions {
    public List<Step> steps;

    public Instructions() {
        this.steps = new LinkedList<>();
    }

    public boolean isEmpty() {
        return steps.isEmpty();
    }

    public boolean add(Step step) {
        if (!steps.contains(step)) {
            return steps.add(step);
        }
        return false;
    }
}
